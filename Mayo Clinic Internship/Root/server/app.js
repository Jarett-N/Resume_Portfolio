
// server/app.js
const db = require('../db/connection'); // Adjust path if necessary

const express = require('express');
const app = express();
const port = process.env.PORT || 3000;

// Middleware to parse JSON bodies
app.use(express.json());

// Serve static files from the "public" directory
app.use(express.static('public'));

// Example API endpoint for fetching health data
app.get('/api/health-data', (req, res) => {
  // In a real application, fetch this data from your SQL database.
  const healthData = {
    weight: '78 kg',
    bmi: '24.3',
    exercise: '45 minutes',
    calories: '2100 kcal'
  };
  res.json(healthData);
});

// Example API endpoint for updating health data
app.put('/api/health-data', (req, res) => {
  const newHealthData = req.body;
  // Here you would add logic to update the health data in your SQL database.
  res.json({
    message: 'Health data updated successfully',
    data: newHealthData
  });
});

// Start the server
app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
