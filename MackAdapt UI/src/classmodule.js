import {
  getClassQuizzes,
  getClassEnrollees,
  canCreateQuiz,
  getClassReadings,
  uploadReading,
  addReadingObjective,
  getClassName,
  lookupUserBySub,
  deleteQuiz
} from "./backend";
import "./style.css";

const params = new URLSearchParams(window.location.search);
const classId = params.get("classId"); // classId still comes from url

const googleToken = sessionStorage.getItem("googleToken");
console.log("[DEBUG] Google token from sessionStorage:", googleToken);

let userId = null;
let email = null;
let quizToDelete = null;

const deleteModal = document.getElementById("deleteQuizModal");
const cancelDeleteQuizBtn = document.getElementById("cancelDeleteQuizBtn");
const confirmDeleteQuizBtn = document.getElementById("confirmDeleteQuizBtn");
const readingBtn = document.createElement("button");

async function init() {
  if (!classId) {
    document.getElementById("main").innerHTML =
      "<p>Error: Class not specified.</p>";
    return;
  }

  if (!googleToken) {
    document.getElementById("main").innerHTML = "<p>Error: Not logged in.</p>";
    return;
  }

  const user = await lookupUserBySub(googleToken);
  console.log("[DEBUG] User object returned from backend:", user);

  if (!user || user.userId <= 0) {
    document.getElementById("main").innerHTML =
      "<p>Error: Failed to identify user.</p>";
    return;
  }

  userId = user.userId;
  email = user.email;

  readingBtn.addEventListener("click", () => {
    readingNameInput.value = "";
    objectivesContainer.innerHTML = `
      <label>
          Objective:
          <input type="text" name="objective" style="width:100%; margin-top:.25rem; margin-bottom:.25rem;">
      </label>
    `;
    readingModal.style.display = "flex";
  });

  addObjectiveBtn.addEventListener("click", () => {
    const newInput = document.createElement("label");
    newInput.innerHTML = `
      Objective:
      <input type="text" name="objective" style="width:100%; margin-top:.25rem; margin-bottom:.25rem;">
    `;
    objectivesContainer.appendChild(newInput);
  });

  cancelReadingBtn.addEventListener("click", () => {
    readingModal.style.display = "none";
  });

  saveReadingBtn.addEventListener("click", async () => {
    const readingName = readingNameInput.value.trim();
    if (!readingName) return alert("Enter a reading name");

    const objectiveInputs = objectivesContainer.querySelectorAll(
      'input[name="objective"]'
    );
    const objectives = Array.from(objectiveInputs)
      .map((i) => i.value.trim())
      .filter((v) => v.length > 0);

    try {
      const readingRes = await uploadReading(classId, userId, readingName);
      const readingId = readingRes.readingId;

      for (let obj of objectives) {
        await addReadingObjective(readingId, classId, obj);
      }

      readingModal.style.display = "none";
      await loadClassData(); // refresh
    } catch (err) {
      console.error(err);
      alert("Error adding reading.");
    }
  });

  document.addEventListener("click", (e) => {
    if (e.target.classList.contains("delete-icon")) {
      quizToDelete = e.target.dataset.quizId;
      deleteModal.style.display = "flex";
    }
  });

  cancelDeleteQuizBtn.addEventListener("click", () => {
    quizToDelete = null;
    deleteModal.style.display = "none";
  });

  confirmDeleteQuizBtn.addEventListener("click", async () => {
    if (!quizToDelete) return;
    try {
      await deleteQuiz(quizToDelete, classId, userId);
      deleteModal.style.display = "none";
      quizToDelete = null;
      await loadClassData(); //refreshes after delete
    } catch (err) {
      console.error(err);
      alert("Error deleting quiz.");
    }
  });

  await loadClassData();
}

async function loadClassData() {
  const cname = await getClassName(classId);
  document.getElementById("class-title").textContent =
    cname?.className || `Class ID: ${classId}`;

  const canCreate = await canCreateQuiz(classId, userId);

  const quizzes = await getClassQuizzes(classId);
  const quizzesContainer = document.getElementById("quizzes");
  quizzesContainer.innerHTML = "";

  if (!quizzes || quizzes.length === 0) {
    quizzesContainer.innerHTML = "<p>No quizzes available for this class.</p>";
  } else {
    quizzes.forEach((q) => {
      const card = document.createElement("div");
      card.className = "quiz-card";

      const deleteIcon = canCreate
        ? `<i class="fa-solid fa-trash delete-icon" data-quiz-id="${q.quizId}"></i>`
        : "";

      card.innerHTML = `
        <div class="quiz-card-content">
          <h3>${q.quizName || "Quiz #" + q.quizId}</h3>
          <a href="quiz.html?quizId=${q.quizId}&classId=${classId}" class="quiz-link-btn">
            <i class="fa-solid fa-play"></i> Start Quiz
          </a>
          ${deleteIcon}
        </div>
      `;
      quizzesContainer.appendChild(card);
    });
  }

  // Toolbar buttons
  const toolbar = document.getElementById("quiz-toolbar");
  toolbar.innerHTML = "";
  toolbar.style.display = canCreate ? "flex" : "none";

  if (canCreate) {
    const createBtn = document.createElement("button");
    createBtn.textContent = "Create Quiz";
    createBtn.classList.add("action-btn");
    createBtn.addEventListener("click", async () => {
      const res = await fetch(`/api/classes/${classId}/quizzcreation?userId=${userId}`, {
        method: "POST",
      });
      const data = await res.json();
      window.location.href = `quiz-create.html?quizId=${data.quizId}&classId=${classId}&userId=${userId}`;
    });

    const enrollBtn = document.createElement("button");
    enrollBtn.textContent = "Manage Enrollees";
    enrollBtn.classList.add("action-btn");
    enrollBtn.addEventListener("click", () => {
      window.location.href = `class-enroll.html?classId=${classId}&userId=${userId}`;
    });

    const resultsBtn = document.createElement("button");
    resultsBtn.textContent = "View Results";
    resultsBtn.classList.add("action-btn");
    resultsBtn.addEventListener("click", () => {
      window.location.href = `results.html?classId=${classId}&userId=${userId}`;
    });

    readingBtn.textContent = "Add Unit";
    readingBtn.classList.add("action-btn");

    toolbar.appendChild(createBtn);
    toolbar.appendChild(enrollBtn);
    toolbar.appendChild(resultsBtn);
    toolbar.appendChild(readingBtn);

    document.querySelectorAll(".delete-icon").forEach((icon) => {
      icon.style.display = "block";
    });
  }

  // Readings
  const readings = await getClassReadings(classId);
  const readingsList = document.getElementById("readings");
  readingsList.innerHTML =
    readings && readings.length
      ? readings.map((r) => `<li>${r.readingName}</li>`).join("")
      : "<li>No units added yet.</li>";

  // Enrollees
  const enrollees = await getClassEnrollees(classId);
  const enrolleesList = document.getElementById("enrollees");
  enrolleesList.innerHTML =
    enrollees && enrollees.length
      ? enrollees.map((s) => `<li>${s.firstName} ${s.lastName} (${s.email})</li>`).join("")
      : "<li>No students enrolled yet.</li>";
}

// Call init() to start everything
init();
