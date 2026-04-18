//mock data for testing to be in database
let customActivityMap = {
    "Walking": "Walking 2.0 mph, slow",
    "Jogging": "Running, 5 mph (12 minute mile)",
    "Running": "Running, 8 mph (7.5 min mile)",
    "Gardening": "",
    "Cycling": "",
    "Yoga": "",
    "Swimming": "Swimming laps, freestyle, slow",
    "Weight Lifting": "Weight lifting, light workout",
    "Exercise Class": "Teach exercise classes (& participate)"
};

document.addEventListener("DOMContentLoaded", () => {
    const activityDropdown = document.querySelector('.activityDropdown');
    const textBox = activityDropdown.querySelector('.textBox');
    const optionsContainer = activityDropdown.querySelector('.option');
    const calculateButton = document.getElementById("calculateButton");
    const searchButton = document.getElementById("searchButton");
    let selectedActivity = null;

    //populates dropdown using the customActivityMap
    Object.entries(customActivityMap).forEach(([customName]) => {
        const newOption = document.createElement("div");
        newOption.classList.add("dropdown-item", "custom-option");
        newOption.innerHTML = `
            <span>${customName}</span>
            <button class="delete-btn" title="Delete">✖</button>
        `;
        optionsContainer.appendChild(newOption);
    });

    //fetches calories burned based metrics that the API takes
    calculateButton.addEventListener("click", async (event) => {
        event.preventDefault();
        await fetchCalories(event);
    });

    //searches api for activities based on user input and sets mock values so the api doesn't get upset
    searchButton.addEventListener("click", async (event) => {
        event.preventDefault();
        document.querySelector('[name="weight"]').value = 100;
        document.querySelector('[name="duration"]').value = 100;
        await searchActivity(event);
        document.querySelector('[name="weight"]').value = '';
        document.querySelector('[name="duration"]').value = '';
    });

    //toggle dropdown menu
    textBox.addEventListener('click', () => {
        activityDropdown.classList.toggle('active');
    });

    //gives each item in the dropdown a clickable remove button
    optionsContainer.addEventListener('click', (event) => {
        const clickedOption = event.target;

        if (clickedOption.classList.contains("delete-btn")) {
            const parent = clickedOption.closest(".dropdown-item");
            if (parent) parent.remove();
        } else if (clickedOption.classList.contains("dropdown-item") || clickedOption.closest(".dropdown-item")) {
            const selected = clickedOption.closest(".dropdown-item");
            const itemText = selected.querySelector('span').textContent.trim();
            selectedActivity = customActivityMap[itemText] || itemText;
            textBox.value = selectedActivity;
            activityDropdown.classList.remove('active');
        }
    });

    //closes dropdown when you click out of it
    document.addEventListener('click', (e) => {
        if (!activityDropdown.contains(e.target)) {
            activityDropdown.classList.remove('active');
        }
    });
});


//runs the saveActivity function when the button is clicked
document.addEventListener("DOMContentLoaded", () => {
    document.getElementById("saveActivity").addEventListener("click", saveActivity);
});

// Save selected activity to dropdown and map
function saveActivity() {
    const activityName = document.querySelector('.textBox').value.trim();

    if (activityName) {
        const customName = prompt("Please enter a name for your new activity:", activityName);
        if (customName) {
            const newOption = document.createElement("div");
            newOption.classList.add("dropdown-item", "custom-option");

            newOption.innerHTML = `
                <span>${customName}</span>
                <button class="delete-btn" title="Delete">✖</button>
            `;

            document.querySelector('.activityDropdown .option').appendChild(newOption);
            customActivityMap[customName] = activityName;
            document.querySelector('.textBox').value = '';
        }
    } else {
        alert("Please select an activity to save.");
    }
}


//fetches calories based on the user input
async function fetchCalories(event) {
    event.preventDefault();
    let formData = new FormData(document.getElementById("caloriesForm"));

    let response = await fetch("/get_calories", {
        method: "POST",
        body: formData
    });

    let result = await response.json();
    let resultDiv = document.getElementById("result");

    if (result.error) {
        resultDiv.innerHTML = `<p style="color:red;">Error: ${result.error}</p>`;
    } else {
        resultDiv.innerHTML = `
            <h3>Results:</h3>
            <p><strong>Activity:</strong> ${result[0].name}</p>
            <p><strong>Total Calories Burned:</strong> ${result[0].total_calories}</p>
            <p><strong>Calories Per Hour:</strong> ${result[0].calories_per_hour}</p>
        `;

        let today = new Date().toISOString().split('T')[0];
        document.getElementById("date").value = today;
        document.getElementById("new_calorie").value = result[0].total_calories;
    }
}

//gives a list of activities similar to the user's input activity (if it exists in the API)
async function searchActivity(event) {
    event.preventDefault();
    let formData = new FormData(document.getElementById("caloriesForm"));

    let response = await fetch("/get_calories", {
        method: "POST",
        body: formData
    });

    let result = await response.json();
    let resultDiv = document.getElementById("result");

    if (result.error) {
        resultDiv.innerHTML = `<p style="color:red;">Error: ${result.error}</p>`;
    } else {
        resultDiv.innerHTML = `
            <h3>Pick which Activity to Save:</h3>
            <br>
            ${result.map((activity) => `
                <p class="result-text-block" style="cursor: pointer;">${activity.name}</p>
                <br>
                <hr>
                <br>
            `).join('')}
        `;

        document.querySelectorAll('.result-text-block').forEach(block => {
            block.addEventListener('click', () => {
                const activityName = block.textContent.trim();
                document.querySelector('.textBox').value = activityName;
                selectedActivity = activityName;
                saveActivity();
            });
        });
    }
}
