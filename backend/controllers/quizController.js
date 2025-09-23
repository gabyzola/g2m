const db = require("../config/db");

// Add a new question
async function createQuestion(req, res) {
  try {
    const { text, options, correctAnswer, difficulty, objectiveId } = req.body;
    const createdBy = req.user.id; // from Microsoft SSO middleware

    const result = await db.query(
      `INSERT INTO Questions (text, options, correct_answer, difficulty, objective_id, created_by)
       VALUES ($1, $2, $3, $4, $5, $6) RETURNING *`,
      [text, JSON.stringify(options), correctAnswer, difficulty, objectiveId, createdBy]
    );

    res.status(201).json(result.rows[0]);
  } catch (err) {
    res.status(500).json({ error: err.message });
  }
}

module.exports = { createQuestion };
