// healthRoutes.js

const express = require('express');
const router = express.Router();
const healthController = require('../controllers/healthController');

// GET endpoint to fetch health data
router.get('/', healthController.getHealthData);

// PUT endpoint to update health data
router.put('/', healthController.updateHealthData);

module.exports = router;
