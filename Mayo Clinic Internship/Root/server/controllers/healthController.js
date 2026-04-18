// server/controllers/healthController.js
const db = require('../../db/connection');

const getHealthData = (req, res) => {
  // For simplicity, we assume a single health data record for user_id = 1
  const sql = `SELECT * FROM health_metrics WHERE user_id = ?`;
  db.get(sql, [1], (err, row) => {
    if (err) {
      return res.status(500).json({ error: err.message });
    }
    if (!row) {
      return res.status(404).json({ error: 'Health data not found' });
    }
    res.json(row);
  });
};

const updateHealthData = (req, res) => {
  const { weight, bmi, exercise, calories } = req.body;
  const sql = `
    UPDATE health_metrics
    SET weight = ?,
        bmi = ?,
        exercise = ?,
        calories = ?,
        updated_at = CURRENT_TIMESTAMP
    WHERE user_id = ?`;
  db.run(sql, [weight, bmi, exercise, calories, 1], function(err) {
    if (err) {
      return res.status(500).json({ error: err.message });
    }
    res.json({
      message: 'Health data updated successfully',
      changes: this.changes
    });
  });
};

module.exports = {
  getHealthData,
  updateHealthData,
};

  