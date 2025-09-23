const express = require("express");
const router = express.Router();
const { createQuestion } = require("../controllers/questionController");
const checkJwt = require("../middleware/authMiddleware"); // Microsoft SSO

// Only instructors can upload questions
router.post("/questions", checkJwt, createQuestion);

module.exports = router;
