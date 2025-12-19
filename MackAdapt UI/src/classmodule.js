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

if (!classId) {
  document.getElementById("main").innerHTML =
    "<p>Error: Class not specified.</p>";
}

//gets the logged in user from session storage instead of url so people cant get user info from the url
const googleToken = sessionStorage.getItem("googleToken");
console.log("[DEBUG] Google token from sessionStorage:", googleToken);

let userId = null;
let email = null;

//if google token is null then no one is logged in or there's some horrible problem
if (!googleToken) {
  document.getElementById("main").innerHTML = "<p>Error: Not logged in.</p>";
} else {
  //grab the user by the google sub
  const user = await lookupUserBySub(googleToken);
  console.log("[DEBUG] User object returned from backend:", user);

  //user never got logged in...they dont exist in the db
  if (!user || user.userId <= 0) {
    document.getElementById("main").innerHTML =
      "<p>Error: Failed to identify user.</p>";
  } else {
    //user exists in db so set their email and id
    userId = user.userId;
    email = user.email;
  }
}

let quizToDelete = null;

const deleteModal = document.getElementById("deleteQuizModal");
const cancelDeleteQuizBtn = document.getElementById("cancelDeleteQuizBtn");
const confirmDeleteQuizBtn = document.getElementById("confirmDeleteQuizBtn");
const readingBtn = document.createElement("button");

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

      //adds more objective fields
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
          const readingRes = await uploadReading(classId, userId, readingName);
          const readingId = readingRes.readingId;

          for (let obj of objectives) {
            await addReadingObjective(readingId, classId, obj);
          }

          // alert("Reading added successfully!");
          readingModal.style.display = "none";
          loadClassData();
        } catch (err) {
          console.error(err);
          alert("Error adding reading.");
        }
      });

//loads data for page
async function loadClassData() {
  const cname = await getClassName(classId);
  //class-title is in the html file, fill it in with cname, the class name and the class code
  document.getElementById("class-title").textContent =
    cname?.className || `Class ID: ${classId}`;

    //gets class quizzes and sticks them in html under quizzes id
    const canCreate = await canCreateQuiz(classId, userId);
  const quizzes = await getClassQuizzes(classId);
  const quizzesContainer = document.getElementById("quizzes");
  quizzesContainer.innerHTML = "";

  //if no quizzes
  if (!quizzes || quizzes.length === 0) {
    quizzesContainer.innerHTML = "<p>No quizzes available for this class.</p>";
  } else {
    //if quizzes, then list them in card format
    quizzes.forEach(q => {
      const card = document.createElement("div");
      card.className = "quiz-card";

      let deleteIcon = "";
    if (canCreate) {
      deleteIcon = `<i class="fa-solid fa-trash delete-icon" data-quiz-id="${q.quizId}"></i>`;
    }

    card.innerHTML = `
    <div class="quiz-card-content">
      <h3>${q.quizName || "Quiz #" + q.quizId}</h3>

      <a 
        href="quiz.html?quizId=${q.quizId}&classId=${classId}"
        class="quiz-link-btn"
      >
        <i class="fa-solid fa-play"></i>
        Start Quiz
      </a>

      ${deleteIcon}
    </div>
  `;

    quizzesContainer.appendChild(card);
    });
  }

  //check if user is an isntructor or not
  try {
    const canCreate = await canCreateQuiz(classId, userId);
    const toolbar = document.getElementById("quiz-toolbar");
    toolbar.innerHTML = "";   
    toolbar.style.display = "none";

    if (canCreate) {
      toolbar.style.display = "flex";   // show toolbar only for instructors

      // CREATE QUIZ
      const btn = document.createElement("button");
      btn.textContent = "Create Quiz";
      btn.classList.add("action-btn");
      toolbar.appendChild(btn);

      btn.addEventListener("click", async () => {
        const res = await fetch(`/api/classes/${classId}/quizzcreation?userId=${userId}`, {
          method: "POST"
        });
        const data = await res.json();
        window.location.href = `quiz-create.html?quizId=${data.quizId}&classId=${classId}&userId=${userId}`;
      });

      // MANAGE ENROLLEES
      const enrollBtn = document.createElement("button");
      enrollBtn.textContent = "Manage Enrollees";
      enrollBtn.classList.add("action-btn");
      toolbar.appendChild(enrollBtn);

      enrollBtn.addEventListener("click", () => {
        window.location.href = `class-enroll.html?classId=${classId}&userId=${userId}`;
      });

      // VIEW RESULTS
      const resultsBtn = document.createElement("button");
      resultsBtn.textContent = "View Results";
      resultsBtn.classList.add("action-btn");
      toolbar.appendChild(resultsBtn);

      resultsBtn.addEventListener("click", () => {
        window.location.href = `results.html?classId=${classId}&userId=${userId}`;
      });

      // ADD READING
      readingBtn.textContent = "Add Unit";
      readingBtn.classList.add("action-btn");
      toolbar.appendChild(readingBtn);
    document.querySelectorAll(".delete-icon").forEach(icon => {
        icon.style.display = "block";
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
    readingsList.innerHTML = "<li>No units added yet.</li>";
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

document.addEventListener("click", e => {
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
    loadClassData(); // refresh after delete
  } catch (err) {
    console.error(err);
    alert("Error deleting quiz.");
  }
});

loadClassData();
