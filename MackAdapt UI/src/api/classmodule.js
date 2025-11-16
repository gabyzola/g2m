//this is like main.js, but specifically for the class module since this can get tricky 

//gets backend.js
import { getClassQuizzes, getClassEnrollees } from "./backend.js";

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
  document.getElementById("class-title").textContent = `Class ID: ${classId}`;

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
        <a href="/src/quiz.html?quizId=${q.quizId}">Go to Quiz</a>
      `;
      quizzesContainer.appendChild(card);
    });
  }

  //same thing but for enrollees, we gotta stick this in a sidebar
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
