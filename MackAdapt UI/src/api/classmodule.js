import {
  getClassQuizzes,
  getClassEnrollees,
  canCreateQuiz,
  getClassReadings,
  uploadReading,
  addReadingObjective,
  getClassName,
  lookupUserBySub
} from "./backend.js";

const params = new URLSearchParams(window.location.search);
const classId = params.get("classId"); // classId still comes from URL

if (!classId) {
  document.getElementById("main").innerHTML =
    "<p>Error: Class not specified.</p>";
}

// Instead of userId from URL, get logged-in user from sessionStorage
const googleToken = sessionStorage.getItem("googleToken");
console.log("[DEBUG] Google token from sessionStorage:", googleToken);

let userId = null;
let email = null;

if (!googleToken) {
  document.getElementById("main").innerHTML = "<p>Error: Not logged in.</p>";
} else {
  const user = await lookupUserBySub(googleToken);
  console.log("[DEBUG] User object returned from backend:", user);

  if (!user || user.userId <= 0) {
    document.getElementById("main").innerHTML =
      "<p>Error: Failed to identify user.</p>";
  } else {
    userId = user.userId;
    email = user.email;
  }
}

async function loadClassData() {
  const cname = await getClassName(classId);
  document.getElementById("class-title").textContent =
    cname?.className || `Class ID: ${classId}`;

  const quizzes = await getClassQuizzes(classId);
  const quizzesContainer = document.getElementById("quizzes");
  quizzesContainer.innerHTML = "";

  if (!quizzes || quizzes.length === 0) {
    quizzesContainer.innerHTML = "<p>No quizzes available for this class.</p>";
  } else {
    quizzes.forEach(q => {
      const card = document.createElement("div");
      card.className = "quiz-card";
      card.innerHTML = `
        <h3>${q.quizName || "Quiz #" + q.quizId}</h3>
        <a href="quiz.html?quizId=${q.quizId}&classId=${classId}&userId=${userId}">Go to Quiz</a>
      `;
      quizzesContainer.appendChild(card);
    });
  }

  //check if user is an isntructor or not
  try {
    const canCreate = await canCreateQuiz(classId, userId);  // pass userId now

    if (canCreate) {
      //create a quiz
      const btn = document.createElement("button");
      btn.textContent = "Create Quiz";
      btn.style.marginBottom = "1rem";
      quizzesContainer.prepend(btn);

      btn.addEventListener("click", async () => {
        const res = await fetch(`/api/classes/${classId}/quizzcreation?userId=${userId}`, {
          method: "POST"
        });
        const data = await res.json();
        window.location.href = `quiz-create.html?quizId=${data.quizId}&classId=${classId}&userId=${userId}`;
      });

      //manage the enrollees
      const enrollBtn = document.createElement("button");
      enrollBtn.textContent = "Manage Enrollees";
      enrollBtn.style.marginBottom = "1rem";
      quizzesContainer.prepend(enrollBtn);

      enrollBtn.addEventListener("click", () => {
        window.location.href = `class-enroll.html?classId=${classId}&userId=${userId}`;
      });

      //add a reading
      const readingBtn = document.createElement("button");
      readingBtn.textContent = "Add Reading";
      readingBtn.style.marginBottom = "1rem";
      quizzesContainer.prepend(readingBtn);

      const readingModal = document.getElementById("readingModal");
      const objectivesContainer = document.getElementById("objectivesContainer");
      const addObjectiveBtn = document.getElementById("addObjectiveBtn");
      const cancelReadingBtn = document.getElementById("cancelReadingBtn");
      const saveReadingBtn = document.getElementById("saveReadingBtn");
      const readingNameInput = document.getElementById("readingNameInput");

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

        const objectiveInputs = objectivesContainer.querySelectorAll('input[name="objective"]');
        const objectives = Array.from(objectiveInputs)
          .map(i => i.value.trim())
          .filter(v => v.length > 0);

        try {
          //pass logged in userId here
          const readingRes = await uploadReading(classId, userId, readingName);
          const readingId = readingRes.readingId;

          for (let obj of objectives) {
            await addReadingObjective(readingId, classId, obj);
          }

          alert("Reading added successfully!");
          readingModal.style.display = "none";
          loadClassData();
        } catch (err) {
          console.error(err);
          alert("Error adding reading.");
        }
      });
    }
  } catch (err) {
    console.error("Error checking canCreate:", err);
  }

  // sidebar: readings
  const readings = await getClassReadings(classId);
  const readingsList = document.getElementById("readings");
  readingsList.innerHTML = "";

  if (!readings || readings.length === 0) {
    readingsList.innerHTML = "<li>No readings added yet.</li>";
  } else {
    readings.forEach(r => {
      const li = document.createElement("li");
      li.textContent = `${r.readingName}`;
      readingsList.appendChild(li);
    });
  }

  // sidebar: enrollees
  const enrollees = await getClassEnrollees(classId);
  const enrolleesList = document.getElementById("enrollees");
  enrolleesList.innerHTML = "";

  if (!enrollees || enrollees.length === 0) {
    enrolleesList.innerHTML = "<li>No students enrolled yet.</li>";
  } else {
    enrollees.forEach(s => {
      const li = document.createElement("li");
      li.textContent = `${s.firstName} ${s.lastName} (${s.email})`;
      enrolleesList.appendChild(li);
    });
  }
}

loadClassData();
