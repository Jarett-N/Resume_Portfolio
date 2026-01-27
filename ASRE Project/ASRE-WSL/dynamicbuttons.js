document.addEventListener('DOMContentLoaded', () => {
    // Log to verify script is loaded
    console.log("JavaScript loaded");

    // Fetch actions from the server
    fetch('/actions')
        .then(response => response.json())
        .then(actions => {
            console.log("Actions fetched:", actions);
            const container = document.getElementById('button-container');
            if (!container) console.log("Container not found");

            // Create buttons for each action
            actions.forEach(action => {

                // Change this to change the buttons themselves
                const button = document.createElement('button');
                button.id = `action-button-${action.id}`;
                button.textContent = action.name;

                // Change this for the button behavior
                button.onclick = () => {
                    fetch('/trigger_action', {
                        method: 'POST',
                        headers: { 'Content-Type': 'application/json' },
                        body: JSON.stringify({ action_id: action.id })
                    })
                        .then(response => response.json())
                        .then(data => {
                            if (data.success) {
                                alert(`Action triggered: ${action.name}`);
                            } else {
                                alert(`Error: ${data.error}`);
                            }
                        })
                        .catch(error => console.error('Error:', error));
                };

                // Append button to container
                container.appendChild(button);
            });
        })
        .catch(error => console.error('Error fetching actions:', error));
});