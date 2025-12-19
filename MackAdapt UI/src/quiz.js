import { 
  getStudentQuestions, 
  getQuizObjectives, 
  selectObjectiveForQuiz, 
  startAttemptSession, 
  finalizeAttemptSession, 
  saveStudentAnswer, 
  getAttemptResults,
  assignBadge,
  lookupUserBySub
} from "./src/api/backend.js";
import "./style.css";

function showMessage(text, type = "warning") {
  const box = document.getElementById("uiMessage");

  const styles = {
    warning: { bg: "#fff3cd", border: "#ffeeba", color: "#856404" },
    error:   { bg: "#f8d7da", border: "#f5c6cb", color: "#721c24" },
    success: { bg: "#d4edda", border: "#c3e6cb", color: "#155724" }
  };

  const s = styles[type] || styles.warning;

  box.textContent = text;
  box.style.background = s.bg;
  box.style.borderColor = s.border;
  box.style.color = s.color;
  box.style.display = "block";

  // auto-hide after 4 seconds
  setTimeout(() => {
    box.style.display = "none";
  }, 4000);
}

const params = new URLSearchParams(window.location.search);
const classId = params.get("classId");
const quizId = params.get("quizId"); 

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

const objectiveCard = document.getElementById("objective-card");
const questionCard = document.querySelector(".card"); 
const objectiveList = document.getElementById("objective-list");
const submitObjectiveBtn = document.getElementById("objectiveSubmitBtn");

questionCard.style.display = "none"; 

const qStem = document.getElementById('q-stem');
const choices = document.getElementById('choices');
const nextBtn = document.getElementById('nextBtn');
const resultModal = document.getElementById('resultModal');
const scoreLine = document.getElementById('scoreLine');

let QUESTIONS = [];
let index = 0;
let score = 0;
let userAnswers = {};
let attemptSessionId = -1;

//start attempt session for this page
async function startSession(objectiveId = null) {
  attemptSessionId = await startAttemptSession(userId, quizId, objectiveId || 0);
  if (attemptSessionId <= 0) {
    showMessage("We couldn't start your quiz session. Please try again.", "error");
  } else {
    console.log("Attempt session started:", attemptSessionId);
  }
}

//load objectives for this quiz
async function loadObjectives() {
  const objectives = await getQuizObjectives(quizId);

  if (!objectives || objectives.length === 0) {
    objectiveCard.style.display = "none";
    questionCard.style.display = "block";
    await startSession(); // no objective selected
    loadQuestions();
    return;
  }

  objectiveList.innerHTML = objectives.map(obj => `
    <label style="display:block; margin:.6rem 0;">
      <input type="radio" name="objective" value="${obj.objectiveId}">
      ${obj.objectiveName}
    </label>
  `).join("");

  objectiveCard.style.display = "block";
}

// handle student selecting an objective
submitObjectiveBtn.addEventListener("click", async () => {
  const selected = document.querySelector("input[name='objective']:checked");
  if (!selected) {
    showMessage("Please select at least one learning objective to continue.", "warning");
    return;
  }

  const objectiveId = selected.value;
  const result = await selectObjectiveForQuiz(quizId, userId, objectiveId);

  if (result.status !== "success") {
    showMessage("One or more objectives could not be saved. Please try again.", "error");
    return;
  }

  objectiveCard.style.display = "none";
  questionCard.style.display = "block";

  await startSession(objectiveId);
  loadQuestions();
});

// load quiz questions filtered by student's objectives
async function loadQuestions() {
  const raw = await getStudentQuestions(userId, quizId);

  if (!raw || raw.length === 0) {
    qStem.textContent = "No questions available.";
    nextBtn.style.display = "none";
    return;
  }

  QUESTIONS = raw.map(q => {
    const choicesMap = {};   
    const choiceMap = {};    
    const opts = ["A", "B", "C", "D"];

    q.choices.forEach((c, i) => {
      const label = opts[i];
      choicesMap[label] = c.choiceText;
      choiceMap[label] = c; // full choice object
    });

    const correctLabel = Object.keys(choiceMap).find(label => choiceMap[label].choiceId === q.correctChoiceId);

    return {
      questionId: q.questionId,
      questionText: q.questionText,
      difficulty: q.difficulty,
      learningObjective: q.learningObjective,
      choices: choicesMap,
      choiceMap: choiceMap,
      correctChoiceId: q.correctChoiceId,
      correctAnswer: correctLabel
    };
  });

  index = 0;
  render();
}

// render current question
function render() {
  const q = QUESTIONS[index];
  qStem.textContent = q.questionText;

  const opts = ["A", "B", "C", "D"];
  choices.innerHTML = opts.map(letter => `
    <label>
      <input type="radio" name="ans" value="${letter}">
      ${q.choices[letter]}
    </label>
  `).join("");

  nextBtn.textContent = index < QUESTIONS.length - 1 ? "Submit & Next" : "Submit & Finish";
}

// Handle submitting answer & moving to next question
nextBtn.addEventListener("click", async () => {
  const selected = document.querySelector('input[name="ans"]:checked');
  if (!selected) {
    showMessage("Please select an answer before continuing.", "warning");
    return;
  }

  const chosenLetter = selected.value;
  const q = QUESTIONS[index];
  const correctLetter = q.correctAnswer;

  const allLabels = document.querySelectorAll("#choices label");
  allLabels.forEach(label => label.style.backgroundColor = ""); 

  const chosenLabel = [...allLabels].find(l => l.querySelector('input').value === chosenLetter);
  if (chosenLetter === correctLetter) {
    chosenLabel.style.backgroundColor = "#d4edda";
    score++; 
  } else {
    chosenLabel.style.backgroundColor = "#f8d7da"; 
    const correctLabelEl = [...allLabels].find(l => l.querySelector('input').value === correctLetter);
    if (correctLabelEl) correctLabelEl.style.backgroundColor = "#d4edda"; 
  }

  // Save answer to backend
  if (attemptSessionId > 0) {
    const chosenChoiceId = q.choiceMap[chosenLetter].choiceId;
    const success = await saveStudentAnswer(attemptSessionId, q.questionId, chosenChoiceId);
    if (!success) console.error("Failed to save question:", q.questionId);
  }

  userAnswers[q.questionId] = chosenLetter;

  setTimeout(async () => {
    index++;
    if (index >= QUESTIONS.length) {
      await finishQuiz();
    } else {
      render();
    }
  }, 1000);
});

// Finish quiz and show results
let hasFinalized = false;

async function finishQuiz() {
  if (hasFinalized) return;
  hasFinalized = true;

  const pct = Math.round((score / QUESTIONS.length) * 100);
  scoreLine.textContent = `You scored ${pct}%.`;
  resultModal.style.display = "flex";

  const newBadge = await assignBadge(userId);

  if (newBadge) {
    scoreLine.textContent += `You earned a new badge: ${newBadge}!`;
  }

  if (attemptSessionId > 0) {
    const success = await finalizeAttemptSession(attemptSessionId);
    if (!success) console.error("Failed to finalize session");

    try {
      const resetSuccess = await fetch(`/api/remove/objectives/${userId}`, {
        method: 'DELETE'
      }).then(res => res.json());
      if (!resetSuccess) console.error("Failed to reset objectives");
    } catch (err) {
      console.error("Error resetting objectives:", err);
    }

    const results = await getAttemptResults(attemptSessionId);
    console.log("Attempt results:", results);
  }
}

loadObjectives();