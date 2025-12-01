import {
  getClassQuizzes,
  getClassEnrollees,
  canCreateQuiz,
  getClassReadings,
  uploadReading,
  addReadingObjective,
  getClassName
} from "./backend.js";

const params = new URLSearchParams(window.location.search);
const classId = params.get("classId");

if (!classId) {
  document.getElementById("main").innerHTML = "<p>Error: No class selected.</p>";
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
        <a href="quiz.html?quizId=${q.quizId}&classId=${classId}">Go to Quiz</a>
      `;
      quizzesContainer.appendChild(card);
    });
  }

  //if person logged in is an instructor
  try {
    const res = await fetch(`/api/canCreate/${classId}`);
    const data = await res.json();

    if (data.canCreate) {
      //creates quiz  option
      const btn = document.createElement("button");
      btn.textContent = "Create Quiz";
      btn.style.marginBottom = "1rem";
      quizzesContainer.prepend(btn);

      btn.addEventListener("click", async () => {
        const res = await fetch(`/api/classes/${classId}/quizzcreation`, {
          method: "POST"
        });
        const data = await res.json();
        window.location.href = `quiz-create.html?quizId=${data.quizId}&classId=${classId}`;
      });

      //add enrollees
      const enrollBtn = document.createElement("button");
      enrollBtn.textContent = "Manage Enrollees";
      enrollBtn.style.marginBottom = "1rem";
      quizzesContainer.prepend(enrollBtn);

      enrollBtn.addEventListener("click", () => {
        window.location.href = `class-enroll.html?classId=${classId}`;
      });

      //addsreading
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
      }); //i think this is good up until now. debugging the rest

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
      }); //this is working too, more reading objective fields are added -> we want this!!

      //saves the readings and objectives
      saveReadingBtn.addEventListener("click", async () => {
        const readingName = readingNameInput.value.trim();
        if (!readingName) return alert("Enter a reading name");

        const objectiveInputs = objectivesContainer.querySelectorAll('input[name="objective"]');
        const objectives = Array.from(objectiveInputs)
          .map(i => i.value.trim())
          .filter(v => v.length > 0);

        try {
          //create readings
          // user/instructor id is hardcoded for now until auth is properly set up;
          const readingRes = await uploadReading(classId, 4, readingName); 
          const readingId = readingRes.readingId;

          //create objectives
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

 //sidebar stuff
  const readings = await getClassReadings(classId);
  const list1 = document.getElementById("readings");
  list1.innerHTML = "";

  if (!readings || readings.length === 0) {
    list1.innerHTML = "<li>No readings added yet.</li>";
  } else {
    readings.forEach(r => {
      const li = document.createElement("li");
      li.textContent = `${r.readingName}`;
      list1.appendChild(li);
    });
  }

  const enrollees = await getClassEnrollees(classId);
  const list = document.getElementById("enrollees");
  list.innerHTML = "";

  if (!enrollees || enrollees.length === 0) {
    list.innerHTML = "<li>No students enrolled yet.</li>";
  } else {
    enrollees.forEach(s => {
      const li = document.createElement("li");
      li.textContent = `${s.firstName} ${s.lastName} (${s.email})`;
      list.appendChild(li);
    });
  }
}

loadClassData();
