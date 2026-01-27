(() => {
  const els = {
    grid: document.getElementById('actions-grid'),
    search: document.getElementById('action-search'),
    category: document.getElementById('action-category'),
    showHidden: document.getElementById('action-show-hidden'),
    refresh: document.getElementById('action-refresh'),
    metaModal: document.getElementById('meta-modal'),
    metaForm: document.getElementById('meta-form'),
    metaCloseBtns: document.querySelectorAll('[data-close-meta]'),
    metaId: document.getElementById('meta-id'),
    metaLabel: document.getElementById('meta-label'),
    metaCategory: document.getElementById('meta-category'),
    metaColor: document.getElementById('meta-color'),
    metaIcon: document.getElementById('meta-icon'),

    // Settings + Tabs
    settingsBtn: document.querySelector('.settings-btn'),
    settingsModal: document.getElementById('settings-modal'),
    settingsForm: document.getElementById('settings-form'),
    settingsClose: document.querySelector('#settings-modal .modal-close'),
    settingsCancel: document.getElementById('settings-cancel'),

    tabActionsBtn: document.getElementById('tab-actions-btn'),
    tabActions: document.getElementById('tab-actions'),
  };

  const KEY_META = 'asre.action.meta';
  const KEY_HIDDEN = 'asre.action.hidden';
  const KEY_PREFS = 'asre.settings';

  const Store = {
    base: [],
    meta: loadJSON(KEY_META, {}),
    hidden: new Set(loadJSON(KEY_HIDDEN, [])),
    async load() {
      try {
        const res = await fetch('/api/actions', { headers: { 'Accept': 'application/json' } });
        if (!res.ok) throw new Error('HTTP ' + res.status);
        this.base = normalize(await res.json());
      } catch (e) {
        console.warn('Use mock actions:', e.message);
        toast('Backend unavailable, using mock actions.', true);
        this.base = normalize(mockActions());
      }
      ensureMeta(this.base, this.meta);
      saveJSON(KEY_META, this.meta);
      populateCategories(this.base, els.category);
    },
    list({ q = '', cat = '', includeHidden = false } = {}) {
      const needle = q.trim().toLowerCase();
      return this.base
        .map(a => ({ ...a, ...(this.meta[a.id] || {}) }))
        .filter(a => includeHidden || !this.hidden.has(a.id))
        .filter(a => !cat || (a.category || '').toLowerCase() === cat.toLowerCase())
        .filter(a => !needle || [a.name, a.label, a.category].some(v => String(v).toLowerCase().includes(needle)));
    },
    setHidden(id, val) {
      val ? this.hidden.add(id) : this.hidden.delete(id);
      saveJSON(KEY_HIDDEN, [...this.hidden]);
    },
    updateMeta(id, patch) {
      this.meta[id] = { ...(this.meta[id] || {}), ...patch };
      saveJSON(KEY_META, this.meta);
    }
  };

  function render() {
    if (!els.grid) return;
    els.grid.setAttribute('aria-busy', 'true');
    const items = Store.list({
      q: els.search?.value || '',
      cat: els.category?.value || '',
      includeHidden: !!els.showHidden?.checked
    });
    els.grid.innerHTML = items.map(renderCard).join('');
    items.forEach(bindCardHandlers);
    els.grid.setAttribute('aria-busy', 'false');
  }

  function renderCard(a) {
    const isHidden = Store.hidden.has(a.id);
    return `
      <article class="action-card" data-id="${esc(a.id)}" aria-hidden="${isHidden ? 'true' : 'false'}">
        <header class="action-head">
          <span class="action-swatch" style="background:${esc(a.color)}"></span>
          <div>
            <div class="action-title">${esc(a.icon || '')} ${esc(a.label)}</div>
            <div class="action-sub">${esc(a.name)} · <strong>${esc(a.category || 'Uncategorized')}</strong></div>
          </div>
        </header>
        <div class="action-buttons">
          <button class="action-btn primary" data-exec>Run</button>
          <button class="action-btn" data-edit>Edit</button>
          <button class="action-btn" data-hide>${isHidden ? 'Unhide' : 'Hide'}</button>
        </div>
      </article>`;
  }

  const cssEscape = window.CSS?.escape ? (s) => CSS.escape(s) : (s) => String(s).replace(/"/g, '\\"');

  function bindCardHandlers(a) {
    const card = els.grid?.querySelector(`.action-card[data-id="${cssEscape(a.id)}"]`);
    if (!card) return;
    card.querySelector('[data-exec]')?.addEventListener('click', () => runAction(a));
    card.querySelector('[data-edit]')?.addEventListener('click', () => openMeta(a));
    card.querySelector('[data-hide]')?.addEventListener('click', (ev) => {
      const nowHidden = !Store.hidden.has(a.id);
      Store.setHidden(a.id, nowHidden);
      if (!els.showHidden?.checked && nowHidden) {
        card.remove();
      } else {
        card.setAttribute('aria-hidden', nowHidden ? 'true' : 'false');
        ev.currentTarget.textContent = nowHidden ? 'Unhide' : 'Hide';
      }
    });
  }

  function openMeta(a) {
    if (!els.metaModal) return;
    els.metaId.value = a.id;
    els.metaLabel.value = a.label || '';
    els.metaCategory.value = a.category || '';
    els.metaColor.value = a.color || '#4f46e5';
    els.metaIcon.value = a.icon || '';
    els.metaModal.hidden = false;
  }

  function closeMeta() {
    if (els.metaModal) els.metaModal.hidden = true;
  }

  els.metaForm?.addEventListener('submit', (e) => {
    e.preventDefault();
    const id = els.metaId?.value;
    if (!id) { closeMeta(); return; }
    Store.updateMeta(id, {
      label: (els.metaLabel?.value || '').trim(),
      category: (els.metaCategory?.value || '').trim(),
      color: els.metaColor?.value || '#4f46e5',
      icon: (els.metaIcon?.value || '').trim(),
    });
    closeMeta();
    render();
  });

  els.metaCloseBtns?.forEach(b => b.addEventListener('click', closeMeta));
  els.metaModal?.addEventListener('click', (e) => { if (e.target === els.metaModal) closeMeta(); });

  async function runAction(a) {
    try {
      const res = await fetch(`/api/actions/${encodeURIComponent(a.id)}/run`, { method: 'POST' });
      if (!res.ok) throw new Error('HTTP ' + res.status);
      toast(`✓ Running: ${a.label}`);
    } catch (err) {
      toast(`Failed: ${a.label} – ${err.message}`, true);
    }
  }

  const debounce = (fn, t = 200) => {
    let h;
    return (...args) => { clearTimeout(h); h = setTimeout(() => fn(...args), t); };
  };

  els.search?.addEventListener('input', debounce(render, 150));
  els.category?.addEventListener('change', render);
  els.showHidden?.addEventListener('change', render);
  els.refresh?.addEventListener('click', async () => { await Store.load(); render(); });


  // -------- Settings --------
  const Settings = {
    btn: els.settingsBtn,
    modal: els.settingsModal,
    form: els.settingsForm,
    close: els.settingsClose,
    cancel: els.settingsCancel,
  };

  const themeSelect = document.getElementById('theme-select');
  const cbModeSelect = document.getElementById('theme-cb-mode');

  function openSettings() {
    if (!Settings.modal) return;
    Settings.modal.hidden = false;
    Settings.modal.focus?.();
  }

  function closeSettings() {
    if (!Settings.modal) return;
    Settings.modal.hidden = true;
    Settings.btn.focus?.();
  }

  Settings.btn?.addEventListener('click', (e) => { e.preventDefault(); openSettings(); });
  Settings.close?.addEventListener('click', closeSettings);
  Settings.cancel?.addEventListener('click', closeSettings);
  Settings.modal?.addEventListener('click', (e) => { if (e.target === Settings.modal) closeSettings(); });
  document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape' && !Settings.modal.hidden) closeSettings();
  });

  // Save settings
  Settings.form.addEventListener('submit', (e) => {
    e.preventDefault();
    const getVal = (id) => document.getElementById(id)?.value;
    const getChk = (id) => !!document.getElementById(id)?.checked;

    const pref = {
      theme:  getVal('theme-select') || 'light',
      cbMode: cbModeSelect ? cbModeSelect.value : '',
      btnSize: getVal('btn-size') || 'normal',
      fontSize: getVal('font-size') || 'm',
      port: Number(getVal('port-input')) || 9559,
      accContrast: getChk('acc-contrast'),
      accFocus: getChk('acc-focus'),
    };

    saveJSON(KEY_PREFS, pref);
    applyPrefs(pref);
    closeSettings();
    toast('Settings saved');
  });

  function normalizeTheme(t) {
    if (t === 'cb' || t === 'color-blind') return 'cb';
    if (t === 'dark') return 'dark';
    return 'light';
  }

  function applyPrefs(pref) {
    if (!pref) return;

    // Theme + Color-blind mode
    const base = normalizeTheme(pref.theme);
    const cbMode = pref.cbMode || '';

    if (base === 'cb') {
      document.documentElement.setAttribute('data-theme', cbMode || 'cb');
      if (cbModeSelect) cbModeSelect.disabled = false;
    } else {
      document.documentElement.setAttribute('data-theme', base);
      if (cbModeSelect) cbModeSelect.disabled = true;
    }

    // Font + buttons
    const fs = pref.fontSize || 'm';
    const px = fs === 's' ? '14px' : fs === 'l' ? '18px' : '16px';
    document.documentElement.style.setProperty('--base-font', px);

    document.documentElement.classList.toggle('large-buttons', pref.btnSize === 'large');
    document.documentElement.classList.toggle('hc', !!pref.accContrast);
    document.documentElement.classList.toggle('always-focus', !!pref.accFocus);
  }

  themeSelect?.addEventListener('change', () => {
    const p = loadJSON(KEY_PREFS, {});
    const selected = themeSelect.value;
    p.theme = selected === 'color-blind' ? 'cb' : selected;
    p.cbMode = cbModeSelect?.value || '';
    applyPrefs(p);
    saveJSON(KEY_PREFS, p);
  });

  cbModeSelect?.addEventListener('change', () => {
    const p = loadJSON(KEY_PREFS, {});
    const selected = themeSelect?.value || 'cb';
    p.theme = selected === 'color-blind' ? 'cb' : selected;
    p.cbMode = cbModeSelect.value;
    applyPrefs(p);
    saveJSON(KEY_PREFS, p);
  });

  // -------- Init settings --------
  const _loadedPref = loadJSON(KEY_PREFS, null);
  applyPrefs(_loadedPref);

  (function syncSettingsUI(pref) {
    if (!pref) return;
    const selTheme = document.getElementById('theme-select');
    const selBtn = document.getElementById('btn-size');
    const selFont = document.getElementById('font-size');
    const chkHC = document.getElementById('acc-contrast');
    const chkFocus = document.getElementById('acc-focus');

    if (selTheme) selTheme.value = (pref.theme === 'cb') ? 'color-blind' : (pref.theme || 'light');
    if (cbModeSelect) cbModeSelect.value = pref.cbMode || '';
    if (selBtn) selBtn.value = pref.btnSize || 'normal';
    if (selFont) selFont.value = pref.fontSize || 'm';
    if (chkHC) chkHC.checked = !!pref.accContrast;
    if (chkFocus) chkFocus.checked = !!pref.accFocus;
  })(_loadedPref);

  // -------- Init --------
  (async function init() {
    await Store.load();
    render();
  })();

  function ensureMeta(list, meta) {
    list.forEach(a => {
      if (!meta[a.id]) {
        meta[a.id] = { label: a.label || a.name, category: a.category || '', color: a.color || '#4f46e5', icon: a.icon || '' };
      }
    });
  }

  function populateCategories(list, select) {
    if (!select) return;
    const cats = [...new Set(list.map(a => (a.category || '').trim()).filter(Boolean))].sort();
    const current = select.value;
    select.innerHTML =
      '<option value="">All Categories</option>' +
      cats.map(c => `<option value="${esc(c)}">${esc(c)}</option>`).join('');
    if ([...select.options].some(o => o.value === current)) select.value = current;
  }

  function normalize(arr) {
    return (arr || []).map(x => {
      const id = x.id || x.name || (crypto?.randomUUID ? crypto.randomUUID() : (Date.now() + Math.random().toString(16).slice(2)));
      return {
        id: String(id),
        name: String(x.name || ''),
        label: String(x.label || x.name || ''),
        category: String(x.category || ''),
        color: String(x.color || '#4f46e5'),
        icon: String(x.icon || ''),
      };
    });
  }

  function mockActions() {
    return [
      { id: 'wave_hello', name: 'WaveHello', label: 'Wave', category: 'Movement', color: '#3b82f6', icon: '✋' },
      { id: 'dance', name: 'Dance', label: 'Dance', category: 'Movement', color: '#f59e0b', icon: '🎵' },
      { id: 'sit_down', name: 'SitDown', label: 'Sit Down', category: 'Pose', color: '#e5e7eb', icon: '' },
      { id: 'hello', name: 'Hello', label: 'Hello', category: 'Speech', color: '#facc15', icon: '💬' },
      { id: 'turn_right', name: 'TurnRight', label: 'Turn Right', category: 'Movement', color: '#a78bfa', icon: '↪️' },
      { id: 'turn_left', name: 'TurnLeft', label: 'Turn Left', category: 'Movement', color: '#8b5cf6', icon: '↩️' },
      { id: 'hidden_item', name: 'HiddenItem', label: 'Hidden Item', category: 'Misc', color: '#e5e7eb', icon: '▪︎' },
    ];
  }

  function toast(msg, isError = false) {
    const t = document.createElement('div');
    Object.assign(t.style, {
      position: 'fixed',
      right: '1rem',
      top: '1rem',
      zIndex: 9999,
      padding: '.6rem .8rem',
      borderRadius: '.6rem',
      border: '1px solid #0001',
      boxShadow: '0 6px 20px rgba(0,0,0,.12)',
      background: isError ? '#fef2f2' : '#ecfeff'
    });
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), 1800);
  }

  function esc(s) {
    return String(s).replace(/[&<>\"']/g, c => ({ '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;' }[c]));
  }
  function loadJSON(k, d) { try { return JSON.parse(localStorage.getItem(k)) ?? d; } catch { return d; } }
  function saveJSON(k, v) { localStorage.setItem(k, JSON.stringify(v)); }
})();

// XAR File Upload
(() => {
  const fileInput = document.createElement('input');
  fileInput.type = 'file';
  fileInput.accept = '.xar,.zip';
  fileInput.multiple = false;
  fileInput.style.display = 'none';
  fileInput.id = 'xar-file-input';
  document.body.appendChild(fileInput);

  function createUploadButton() {
    const uploadBtn = document.createElement('button');
    uploadBtn.id = 'upload-xar-btn';
    uploadBtn.className = 'btn';
    uploadBtn.type = 'button';
    uploadBtn.innerHTML = '📤 Upload XAR';
    uploadBtn.setAttribute('aria-label', 'Upload XAR or ZIP file');
    const toolbar = document.querySelector('.actions-toolbar');
    if (toolbar) toolbar.appendChild(uploadBtn);
    return uploadBtn;
  }

  const uploadBtn = createUploadButton();
  uploadBtn?.addEventListener('click', () => fileInput.click());

  fileInput.addEventListener('change', async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    uploadBtn.disabled = true;
    uploadBtn.innerHTML = '⏳ Uploading...';

    try {
      const formData = new FormData();
      formData.append('file', file);
      const response = await fetch('/upload_xar', { method: 'POST', body: formData });
      const data = await response.json();

      if (data.success) {
        toast(`✓ Converted ${data.files.length} file(s)`);
        const refreshBtn = document.getElementById('action-refresh');
        refreshBtn?.click();
      } else {
        toast(`Upload failed: ${data.error}`, true);
      }
    } catch (error) {
      toast(`Upload failed: ${error.message}`, true);
    } finally {
      uploadBtn.disabled = false;
      uploadBtn.innerHTML = '📤 Upload XAR';
      fileInput.value = '';
    }
  });

  function toast(msg, isError = false) {
    const t = document.createElement('div');
    Object.assign(t.style, {
      position: 'fixed',
      right: '1rem',
      top: '1rem',
      zIndex: 9999,
      padding: '.6rem .8rem',
      borderRadius: '.6rem',
      border: '1px solid #0001',
      boxShadow: '0 6px 20px rgba(0,0,0,.12)',
      background: isError ? '#fef2f2' : '#ecfeff'
    });
    t.textContent = msg;
    document.body.appendChild(t);
    setTimeout(() => t.remove(), 2500);
  }
})();

//Conneciton stabalizer
//--------------------------------
const ConnectionMonitor = {
  isOnline: true,
  checkInterval: 5000, // Check every 5 seconds
  timeoutDuration: 3000, // 3 second timeout
  intervalId: null,
  retryCount: 0,
  maxRetries: 3,

  start() {
    this.intervalId = setInterval(() => this.check(), this.checkInterval);
    this.check(); // Check immediately on start
  },

  stop() {
    if (this.intervalId) {
      clearInterval(this.intervalId);
      this.intervalId = null;
    }
  },

  async check() {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), this.timeoutDuration);
      
      const response = await fetch('/health', {
        method: 'GET',
        signal: controller.signal
      });
      
      clearTimeout(timeoutId);
      
      if (response.ok) {
        if (!this.isOnline) {
          this.onReconnect();
        }
        this.isOnline = true;
        this.retryCount = 0;
      } else {
        throw new Error('Health check failed');
      }
    } catch (error) {
      this.retryCount++;
      
      if (this.isOnline) {
        this.onDisconnect();
      }
      this.isOnline = false;
      
      if (this.retryCount >= this.maxRetries) {
        this.onMaxRetriesReached();
      } else {
        console.log('Reconnection attempted, failed');
      }
    }
  },

  onDisconnect() {
    console.log('Reconnection attempted, failed');
    // Disable action buttons silently
    disableActionButtons();
  },

  onReconnect() {
    console.log('Reconnection attempted, successful');
    // Re-enable action buttons
    enableActionButtons();
    // Reload data silently
    Store.load().then(() => render());
  },

  onMaxRetriesReached() {
    console.log('Reconnect Failed.');
  }
};

function disableActionButtons() {
  document.querySelectorAll('[data-exec]').forEach(btn => {
    btn.disabled = true;
    btn.style.opacity = '0.5';
    btn.style.cursor = 'not-allowed';
  });
}

function enableActionButtons() {
  document.querySelectorAll('[data-exec]').forEach(btn => {
    btn.disabled = false;
    btn.style.opacity = '1';
    btn.style.cursor = 'pointer';
  });
}

// Utility function for fetch with retry
async function fetchWithRetry(url, options = {}, retries = 3, delay = 1000) {
  for (let i = 0; i < retries; i++) {
    try {
      const controller = new AbortController();
      const timeoutId = setTimeout(() => controller.abort(), 5000); // 5 second timeout
      
      const response = await fetch(url, {
        ...options,
        signal: controller.signal
      });
      
      clearTimeout(timeoutId);
      
      if (!response.ok) {
        throw new Error(`HTTP ${response.status}`);
      }
      
      return response;
    } catch (error) {
      if (i === retries - 1) {
        throw error; // Last attempt failed
      }
      
      // Wait before retrying (exponential backoff)
      await new Promise(resolve => setTimeout(resolve, delay * Math.pow(2, i)));
    }
  }
}

// Update Store object (modify the existing one)
const Store = {
  base: [],
  meta: loadJSON(KEY_META, {}),
  hidden: new Set(loadJSON(KEY_HIDDEN, [])),
  
  async load() {
    try {
      const response = await fetchWithRetry('/actions');
      const actions = await response.json();
      
      this.base = normalize(actions);
      ensureMeta(this.base, this.meta);
      saveJSON(KEY_META, this.meta);
      populateCategories(this.base, els.category);
      
      return true;
    } catch (error) {
      console.error('Error loading actions:', error);
      
      // Fallback to mock data if fetch fails
      this.base = normalize(mockActions());
      console.log('Using fallback mock data');
      return false;
    }
  },
  
  list({ q = '', cat = '', includeHidden = false } = {}) {
    const needle = q.trim().toLowerCase();
    return this.base
      .map(a => ({ ...a, ...(this.meta[a.id] || {}) }))
      .filter(a => includeHidden || !this.hidden.has(a.id))
      .filter(a => !cat || (a.category || '').toLowerCase() === cat.toLowerCase())
      .filter(a => !needle || [a.name, a.label, a.category].some(v => String(v).toLowerCase().includes(needle)));
  },
  
  setHidden(id, val) {
    val ? this.hidden.add(id) : this.hidden.delete(id);
    saveJSON(KEY_HIDDEN, [...this.hidden]);
  },
  
  updateMeta(id, patch) {
    this.meta[id] = { ...(this.meta[id] || {}), ...patch };
    saveJSON(KEY_META, this.meta);
  }
};

// Update runAction function (replace the existing one)
async function runAction(a) {
  if (!ConnectionMonitor.isOnline) {
    console.error('Cannot execute action: No server connection');
    return;
  }
  
  try {
    const response = await fetchWithRetry(`/execute/${a.id}`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' }
    }, 2, 500); // 2 retries, 500ms delay
    
    const result = await response.json();
    console.log(`Executed action: ${a.label}`);
  } catch (error) {
    console.error(`Failed to execute action: ${a.label}`, error);
  }
}

// Start monitoring when the script loads (add to end of your existing init)
(async function init() {
  await Store.load();
  render();
  ConnectionMonitor.start(); // Add this line
})();

// Stop monitoring when page unloads
window.addEventListener('beforeunload', () => {
  ConnectionMonitor.stop();
});