//this is like main.js, but specifically for the class module since this can get tricky
//TO DOs

//gets backend.js
import { getClassQuizzes, getClassEnrollees, canCreateQuiz, getClassReadings, uploadReading, addReadingObjective, getClassName } from "./backend.js";

//gets class id from a query string (in the url)
const params = new URLSearchParams(window.location.search);
const classId = params.get("classId");

//error check
if (!classId) {
  document.getElementById("main").innerHTML = "<p>Error: No class selected.</p>";
}

//load quizzes and class enrollees based on the class id
async function loadClassData() {

  //placeholder info for now... we can get more info about the class later
  const cname = await getClassName(classId);

  if (cname && cname.className) {
    document.getElementById("class-title").textContent = cname.className;
  } else {
    document.getElementById("class-title").textContent = `Class ID: ${classId}`;
  }
  //load quizzes
  const quizzes = await getClassQuizzes(classId);
  const quizzesContainer = document.getElementById("quizzes");
  quizzesContainer.innerHTML = "";

  //error check
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

  try {
    const res = await fetch(`/api/canCreate/${classId}`);
    const data = await res.json();

    if (data.canCreate) {
      const btn = document.createElement("button");
      btn.textContent = "Create Quiz";
      btn.id = "createQuizBtn";
      btn.style.marginBottom = "1rem";

      //button
      quizzesContainer.prepend(btn);
      btn.addEventListener("click", async () => {
        try {
          const res = await fetch(`/api/classes/${classId}/quizzcreation`, {
            method: "POST"
          });

          if (!res.ok) throw new Error("Failed to create quiz");

          const data = await res.json();
          const newQuizId = data.quizId;

          if (newQuizId > 0) {
            window.location.href = `quiz-create.html?quizId=${newQuizId}&classId=${classId}`;
          } else {
            alert("Error creating quiz on server");
          }

        } catch (err) {
          console.error("Error creating quiz:", err);
          alert("Failed to create quiz. See console for details.");
        }
      });

      //create enrollment button
      const enrollBtn = document.createElement("button");
      enrollBtn.textContent = "Manage Enrollees";
      enrollBtn.id = "enrollStudentBtn";
      enrollBtn.style.marginBottom = "1rem";
      quizzesContainer.prepend(enrollBtn);

      enrollBtn.addEventListener("click", () => {
        //redirect to enrollment page with classId in query string
        window.location.href = `class-enroll.html?classId=${classId}`;
      });

      //add a reading button- massive debugging needed i fear
      const readingBtn = document.createElement("button"); //create the button
      readingBtn.textContent = "Add Reading"; //label the button
      readingBtn.style.marginBottom = "1rem"; //styling
      quizzesContainer.prepend(readingBtn); //pre load the button

      const readingModal = document.getElementById("readingModal"); //create modal
      const objectivesContainer = document.getElementById("objectivesContainer"); //objectives  section
      const addObjectiveBtn = document.getElementById("addObjectiveBtn"); //add objective button, should add more objective fields
      const cancelReadingBtn = document.getElementById("cancelReadingBtn"); //cancel, gets rid of modal
      const saveReadingBtn = document.getElementById("saveReadingBtn"); //save reading --> THIS MUST CALL UPLOAD READING
      const readingNameInput = document.getElementById("readingNameInput"); //input field

      readingBtn.addEventListener("click", () => { //adds event listener for Add Reading button
        readingNameInput.value = ""; //clears og input
        objectivesContainer.innerHTML = `  //adds objectives section
          <label>
            Objective:
            <input type="text" name="objective" style="width:100%; margin-top:.25rem; margin-bottom:.25rem;">
          </label>
        `;
        readingModal.style.display = "flex"; //display, shoudlnt cause any issues
      });

      addObjectiveBtn.addEventListener("click", () => { //adds event listener for add objective button (this creates more fields)
        const newInput = document.createElement("label"); 
        newInput.innerHTML = ` 
          Objective:
          <input type="text" name="objective" style="width:100%; margin-top:.25rem; margin-bottom:.25rem;">
        `; //objective input --> DOUBLE CHECK THIS
        objectivesContainer.appendChild(newInput); //add input
      });

      cancelReadingBtn.addEventListener("click", () => { //cancel button just gets rid of modal
        readingModal.style.display = "none";
      });

      saveReadingBtn.addEventListener("click", async () => { //SAVE READING--IMPORTANT CHECK THIS PLS
        const readingName = readingNameInput.value.trim();
        if (!readingName) return alert("Enter a reading name"); //checks input

        const objectiveInputs = objectivesContainer.querySelectorAll('input[name="objective"]');
        const objectives = Array.from(objectiveInputs)
          .map(i => i.value.trim())
          .filter(v => v.length > 0);

        try {
          // upload reading
          const uploadRes = await fetch(`/api/classes/${classId}/readingupload`, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              instructorId: 7, // replace dynamically if needed
              classId,
              readingName,
              filePath: ""
            })
          });

          if (!uploadRes.ok) throw new Error("Failed to upload reading");
          const readingData = await uploadRes.json();
          const readingId = readingData.readingId;

          // add objectives
          for (let obj of objectives) {
            await fetch(`/api/readings/${readingId}/objectives`, {
              method: "POST",
              headers: { "Content-Type": "application/json" },
              body: JSON.stringify({ classId, objectiveName: obj })
            });
          }

          alert("Reading added successfully!");
          readingModal.style.display = "none";
          loadClassData(); // refresh readings list
        } catch (err) {
          console.error(err);
          alert("Error adding reading. Check console for details.");
        }
      });
    }
  } catch (err) {
    console.error("Error checking canCreate:", err);
  }

  //make another sidebar but for readings
  const readings = await getClassReadings(classId); //gets all the class readings
  const list1 = document.getElementById("readings");
  list1.innerHTML = "";
  if (!readings || readings.length === 0) { //checks if there are any readings
    list1.innerHTML = "<li>No readings added yet.</li>";
  } else {
    readings.forEach(s => { //list readings
      const li = document.createElement("li");
      li.textContent = `${s.readingName}`;
      list1.appendChild(li);
    });
  }

  //same thing but for enrollees, we gotta stick this in a sidebar
  const enrollees = await getClassEnrollees(classId); //sptres in enrollees
  const list = document.getElementById("enrollees");
  list.innerHTML = "";
  if (!enrollees || enrollees.length === 0) { //checks if there are any enrollees
    list.innerHTML = "<li>No students enrolled yet.</li>";
  } else {
    enrollees.forEach(s => { //list enrollees
      const li = document.createElement("li");
      li.textContent = `${s.firstName} ${s.lastName} (${s.email})`;
      list.appendChild(li);
    });
  }
}

//loads everything
loadClassData();
