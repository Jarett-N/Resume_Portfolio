<#  =====================================================================
ASRE Launcher (Windows PowerShell)  — start_asre.ps1
-----------------------------------------------------------------------
What it does
 - Opens a Windows firewall rule for the app port
 - Creates a portproxy from Windows -> WSL so browsers/clients can reach Flask
 - Starts your WSL distro and launches the Flask Core inside it
 - Waits until the Flask process exits (or Ctrl+C)
 - Cleans up: removes portproxy + firewall rule, and can terminate WSL

USAGE EXAMPLES
  .\start_asre.ps1
  .\start_asre.ps1 -Port 5000 -Distro "Ubuntu" -ProjectRoot ".\asre" -FlaskEntrypoint "launch_asre_wsl.py"
  .\start_asre.ps1 -Stop          # only perform cleanup (remove rules/portproxy, stop distro if requested)

NOTES
 - Run in an elevated PowerShell window (Administrator).
 - The script assumes your Flask app listens on 0.0.0.0:$Port inside WSL.
 - If you only need localhost access on Windows, set -ExposeExternally:$false.
 - If your distro name differs (e.g., "Ubuntu-22.04"), change -Distro accordingly.
===================================================================== #>

[CmdletBinding(SupportsShouldProcess=$true)]
param(
  [int]$Port = 5000,
  [string]$Distro = "Ubuntu",
  [string]$ProjectRoot = ".",              # Windows path to your project root (this script’s folder by default)
  [string]$FlaskEntrypoint = "launch_asre_wsl.py",
  [switch]$Stop,                           # Do cleanup only and exit
  [switch]$TerminateWSLOnExit = $true,     # After Flask exits, wsl.exe -t $Distro
  [switch]$ExposeExternally = $true,       # Open firewall + portproxy for 0.0.0.0:$Port
  [int]$StartupWaitSeconds = 60            # How long to try to detect WSL IP & set up proxy
)

# ---------------------------- Helpers ----------------------------



function Assert-Admin {
  $id = [Security.Principal.WindowsIdentity]::GetCurrent()
  $p = New-Object Security.Principal.WindowsPrincipal($id)
  if (-not $p.IsInRole([Security.Principal.WindowsBuiltInRole]::Administrator)) {
    throw "Please run PowerShell as Administrator."
  }
}

function Write-Info($msg)  { Write-Host "[ASRE] $msg" }
function Write-Warn($msg)  { Write-Warning "[ASRE] $msg" }
function Write-Err($msg)   { Write-Error "[ASRE] $msg" }

# Friendly, unique names to avoid collisions
$fwRuleName   = "ASRE_Firewall_$Port"
$ppRuleKey    = "listenaddress=0.0.0.0;listenport=$Port"
$ppRuleName   = "ASRE_PortProxy_$Port"

function Add-FirewallRule {
  param([int]$TcpPort)
  if (-not $ExposeExternally) {
    Write-Info "Skipping firewall open (ExposeExternally = false)."
    return
  }
  $existing = Get-NetFirewallRule -DisplayName $fwRuleName -ErrorAction SilentlyContinue
  if ($existing) {
    Write-Info "Firewall rule '$fwRuleName' already exists."
  } else {
    New-NetFirewallRule -DisplayName $fwRuleName -Direction Inbound -Action Allow `
      -Protocol TCP -LocalPort $TcpPort | Out-Null
    Write-Info "Firewall rule '$fwRuleName' added for TCP $TcpPort."
  }
}

function Remove-FirewallRule {
  $existing = Get-NetFirewallRule -DisplayName $fwRuleName -ErrorAction SilentlyContinue
  if ($existing) {
    Remove-NetFirewallRule -DisplayName $fwRuleName
    Write-Info "Firewall rule '$fwRuleName' removed."
  } else {
    Write-Info "No firewall rule '$fwRuleName' to remove."
  }
}

function Get-WSLIP {
  param([string]$DistroName)

  # Ask the distro for its first IP (eth0). hostname -I prints space-separated list.
  $cmd = @("wsl.exe","-d",$DistroName,"-e","sh","-lc","hostname -I | awk '{print \$1}'")
  $ip  = & $cmd 2>$null
  $ip  = $ip -replace '\s',''
  if ($ip -and $ip -match '^\d{1,3}(\.\d{1,3}){3}$') { return $ip }
  return $null
}

function Add-PortProxy {
  param([int]$TcpPort, [string]$WSLIP)

  if (-not $ExposeExternally) {
    Write-Info "Skipping portproxy (ExposeExternally = false)."
    return
  }

  # Remove any existing proxy on this listen port first
  Remove-PortProxy -TcpPort $TcpPort | Out-Null

  $args = "interface","portproxy","add","v4tov4","listenport=$TcpPort","listenaddress=0.0.0.0","connectport=$TcpPort","connectaddress=$WSLIP"
  & netsh $args | Out-Null
  Write-Info "Portproxy added: 0.0.0.0:$TcpPort -> $WSLIP:$TcpPort"
}

function Remove-PortProxy {
  param([int]$TcpPort)

  # Query current entries to check if exists
  $list = (& netsh interface portproxy show v4tov4) 2>$null
  if ($list -and ($list -join "`n") -match ":\s*$TcpPort\s") {
    & netsh interface portproxy delete v4tov4 listenport=$TcpPort listenaddress=0.0.0.0 | Out-Null
    Write-Info "Portproxy removed for 0.0.0.0:$TcpPort"
  } else {
    Write-Info "No portproxy present for 0.0.0.0:$TcpPort"
  }
}

function Convert-ToWSLPath {
  param([string]$WindowsPath)
  $full = Resolve-Path -LiteralPath $WindowsPath | Select-Object -ExpandProperty Path
$escapedFull = $full.Replace("'", "'\"'\"'")
$cmd = @("wsl.exe","-d",$Distro,"-e","bash","-lc","wslpath -a '$escapedFull'")
  $wslp = & $cmd
  if (-not $wslp) { throw "Failed to convert Windows path to WSL path: $WindowsPath" }
  return ($wslp -replace '\s+$','')
}

function Start-FlaskInWSL {
  param(
    [string]$DistroName,
    [string]$WSLProjectDir,
    [string]$EntrypointPy,
    [int]$TcpPort
  )
  # Ensure dependencies & start Flask Core
  $bash = @"
set -e
cd '$WSLProjectDir'
# Optional: ensure venv/deps here, e.g., python3 -m venv .venv && . .venv/bin/activate && pip install -r requirements.txt
# Export port if your entrypoint reads it; otherwise Flask file should bind to 0.0.0.0:$TcpPort
export ASRE_PORT=$TcpPort
python3 '$EntrypointPy'
"@

  $tempFile = New-TemporaryFile
  Set-Content -LiteralPath $tempFile $bash -NoNewline -Encoding UTF8

  Write-Info "Launching Flask Core inside WSL ($DistroName) ..."
  # Use wsl.exe to run the script. This blocks until the python process exits.
  & wsl.exe -d $DistroName -e bash -lc "set -e; chmod +x '$(Convert-ToWSLPath $tempFile)'; bash '$(Convert-ToWSLPath $tempFile)'; rm -f '$(Convert-ToWSLPath $tempFile)'" 
}

function Stop-WSLDistro {
  param([string]$DistroName)
  Write-Info "Terminating WSL distro '$DistroName' ..."
  & wsl.exe -t $DistroName 2>$null
}

# ---------------------------- Main ----------------------------

try {
  Assert-Admin

  if ($Stop) {
    Write-Info "Cleanup-only mode (-Stop)."
    Remove-PortProxy -TcpPort $Port
    Remove-FirewallRule
    if ($TerminateWSLOnExit) { Stop-WSLDistro -DistroName $Distro }
    return
  }



  # 1) Open firewall
  Add-FirewallRule -TcpPort $Port

  # 2) Resolve project path to WSL
  $projectWin = Resolve-Path -LiteralPath $ProjectRoot | Select-Object -ExpandProperty Path
  $projectWsl = Convert-ToWSLPath $projectWin
  Write-Info "Project (Windows): $projectWin"
  Write-Info "Project (WSL)    : $projectWsl"
  Write-Info "Flask Entrypoint : $FlaskEntrypoint"
  Write-Info "Port             : $Port"
  Write-Info "Distro           : $Distro"

  # 3) Ensure the distro is up (a benign command warms it up)
  & wsl.exe -d $Distro -e sh -lc "echo ASRE WSL up" | Out-Null

  # 4) Discover WSL IP & add portproxy (if exposing)
  if ($ExposeExternally) {
    $deadline = (Get-Date).AddSeconds($StartupWaitSeconds)
    $wslIp = $null
    while (-not $wslIp -and (Get-Date) -lt $deadline) {
      $wslIp = Get-WSLIP -DistroName $Distro
      if (-not $wslIp) { Start-Sleep -Seconds 2 }
    }
    if (-not $wslIp) {
      Write-Warn "Could not determine WSL IP; continuing without portproxy. External clients may not reach the app."
    } else {
      Add-PortProxy -TcpPort $Port -WSLIP $wslIp
    }
  }

  # 5) Start Flask in WSL (blocks until Flask exits)
  Write-Info "Press Ctrl+C to stop. The script will then clean up."
  $script:stopping = $false
  $cancelHandler = {
    if (-not $script:stopping) {
      $script:stopping = $true
      Write-Host "`n[ASRE] Ctrl+C received — shutting down cleanly..." -ForegroundColor Yellow
      # Let the Flask app handle its own shutdown endpoint if it has one; otherwise we just break after process exits
    }
  }
  $null = Register-EngineEvent PowerShell.Exiting -Action $cancelHandler
  $null = Register-EngineEvent ConsoleCancel -SourceIdentifier ConsoleCancelEvent -Action {
    $cancelHandler.Invoke()
    $_.Cancel = $true
  }

  Start-FlaskInWSL -DistroName $Distro -WSLProjectDir $projectWsl -EntrypointPy $FlaskEntrypoint -TcpPort $Port

} catch {
  Write-Err $_.Exception.Message
} finally {
  Write-Info "Beginning cleanup ..."
  try { Remove-PortProxy -TcpPort $Port } catch { Write-Warn "Portproxy cleanup warning: $($_.Exception.Message)" }
  try { Remove-FirewallRule } catch { Write-Warn "Firewall cleanup warning: $($_.Exception.Message)" }
  if ($TerminateWSLOnExit) {
    try { Stop-WSLDistro -DistroName $Distro } catch { Write-Warn "WSL termination warning: $($_.Exception.Message)" }
  }
  Write-Info "Cleanup complete."
}
