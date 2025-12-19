import { createQuiz, addQuestion, addReadingToQuiz } from "./src/api/backend.js";
import "./style.css";

  const query = new URLSearchParams(window.location.search);
  const quizId = query.get("quizId");
  const classId = query.get("classId");
  const userId = query.get("userId");

  const readingSelect = document.getElementById("readingSelect");
  const objectiveSelect = document.querySelector("#objective");
  const questionContainer = document.getElementById("questionContainer");

  let questionIndex = 0;
 
  // Create a new question block
  function createQuestionBlock() {
    const block = document.createElement("div");
    block.classList.add("panel");
    block.style.border = "1px solid #ccc";
    block.style.padding = "1rem";
    block.style.marginBottom = "1rem";

    const idx = questionIndex++;

    // Use latest objectives
    block.innerHTML = `
      <h4>Question ${idx + 1}</h4>

      <label>Question Text</label>
      <textarea class="q-text"></textarea>

      <label>Difficulty</label>
      <select class="q-difficulty">
        <option>Easy</option>
        <option>Medium</option>
        <option>Hard</option>
      </select>

      <label>Learning Objective</label>
      <select class="q-objective">
        ${objectiveSelect.innerHTML}
      </select>

      <label>A)</label>
      <input class="q-A">

      <label>B)</label>
      <input class="q-B">

      <label>C)</label>
      <input class="q-C">

      <label>D)</label>
      <input class="q-D">

      <label>Correct Answer (A/B/C/D)</label>
      <input class="q-correct" maxlength="1" placeholder="A/B/C/D">
    `;

    return block;
  }

  // Add a new question
  document.getElementById("newQuestionBtn").addEventListener("click", () => {
    questionContainer.appendChild(createQuestionBlock());
  });

  // Fetch readings
  async function loadReadings() {
    try {
      const res = await fetch(`/api/classes/${classId}/readings`);
      if (!res.ok) throw new Error("Failed to fetch readings");

      const data = await res.json();
      readingSelect.innerHTML = "";

      if (!data || data.length === 0) {
        const op = document.createElement("option");
        op.textContent = "No readings available";
        op.disabled = true;
        op.selected = true;
        readingSelect.appendChild(op);
        return;
      }

      data.forEach(reading => {
        const op = document.createElement("option");
        op.value = reading.readingId;
        op.textContent = reading.readingName;
        readingSelect.appendChild(op);
      });

      // Load objectives for first reading
      loadObjectives(data[0].readingId);

    } catch (err) {
      console.error("Failed to load readings:", err);
      readingSelect.innerHTML = "<option disabled selected>Error loading readings</option>";
    }
  }

  // Fetch objectives for a reading
  async function loadObjectives(readingId) {
    try {
      const res = await fetch(`/api/quizzes/${readingId}/readingobjectives`);
      if (!res.ok) throw new Error("Failed to fetch objectives");

      const data = await res.json();
      objectiveSelect.innerHTML = "";

      if (!data || data.length === 0) {
        const op = document.createElement("option");
        op.textContent = "No objectives available";
        op.disabled = true;
        op.selected = true;
        objectiveSelect.appendChild(op);
      } else {
        data.forEach(obj => {
          const op = document.createElement("option");
          op.value = obj.objectiveId;
          op.textContent = obj.objectiveName;
          objectiveSelect.appendChild(op);
        });
      }

      // Update all existing question blocks
      refreshQuestionObjectives();

      // If no questions exist, create first one
      if (questionIndex === 0) {
        questionContainer.appendChild(createQuestionBlock());
      }

    } catch (err) {
      console.error("Failed to load objectives:", err);
      objectiveSelect.innerHTML = "<option disabled selected>Error loading objectives</option>";
    }
  }

  // When user changes reading from dropdown
  readingSelect.addEventListener("change", () => {
    const selectedReadingId = parseInt(readingSelect.value);
    if (selectedReadingId) loadObjectives(selectedReadingId);
  });

  // Link a reading to quiz
  document.getElementById("addReadingBtn").addEventListener("click", async () => {
    const readingId = parseInt(readingSelect.value);
    if (!readingId) return alert("Please select a reading first.");

    const result = await addReadingToQuiz(quizId, readingId);
    if (result && result.success) {
      alert("Reading linked to quiz!");
      await loadQuizObjectives();  // reload objectives
      refreshQuestionObjectives(); // update all question dropdowns
    } else {
      alert("Failed to link reading.");
      console.error(result);
    }
  });

  // Update all question blocks with latest objectives
  function refreshQuestionObjectives() {
    const html = objectiveSelect.innerHTML;
    document.querySelectorAll(".q-objective").forEach(sel => sel.innerHTML = html);
  }

  // Save quiz
  document.getElementById("saveQuizBtn").addEventListener("click", async () => {
    const quizName = document.getElementById("quizName").value.trim();
    if (!quizName) return alert("Please enter a quiz name!");
    if (!quizId) return alert("Quiz ID not found in URL.");

    const nameRes = await fetch(`/api/quizzes/${quizId}/name`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ quizName })
    });

    const nameResult = await nameRes.json();
    if (!nameRes.ok || nameResult.status !== "success") {
      alert("Failed to update quiz name.");
      return;
    }

    const blocks = document.querySelectorAll("#questionContainer .panel");
    for (const block of blocks) {
      const questionData = {
        questionText: block.querySelector(".q-text").value.trim(),
        difficulty: block.querySelector(".q-difficulty").value,
        choiceA: block.querySelector(".q-A").value.trim(),
        choiceB: block.querySelector(".q-B").value.trim(),
        choiceC: block.querySelector(".q-C").value.trim(),
        choiceD: block.querySelector(".q-D").value.trim(),
        correctAnswer: block.querySelector(".q-correct").value.trim().toUpperCase(),
        objectiveId: parseInt(block.querySelector(".q-objective").value)
      };

      const result = await addQuestion(quizId, questionData);
      if (!result || result.status !== "success") {
        alert("A question failed to save.");
        return;
      }
    }

    alert("Quiz saved successfully!");
    window.location.href = `class-module.html?classId=${classId}&userId=${userId}`;
  });

  // Load quiz objectives from backend
  async function loadQuizObjectives() {
    try {
      const res = await fetch(`/api/quizzes/${quizId}/objectives`);
      if (!res.ok) throw new Error("Failed to fetch quiz objectives");

      const objectives = await res.json();
      objectiveSelect.innerHTML = "";

      if (!objectives || objectives.length === 0) {
        const op = document.createElement("option");
        op.textContent = "No objectives available";
        op.disabled = true;
        op.selected = true;
        objectiveSelect.appendChild(op);
      } else {
        objectives.forEach(obj => {
          const op = document.createElement("option");
          op.value = obj.objectiveId;
          op.textContent = obj.objectiveName;
          objectiveSelect.appendChild(op);
        });
      }

      refreshQuestionObjectives();
    } catch (err) {
      console.error("Failed to load quiz objectives:", err);
      objectiveSelect.innerHTML = "<option disabled selected>Error loading objectives</option>";
    }
  }

  // Initial load
  await loadReadings();
  await loadQuizObjectives();