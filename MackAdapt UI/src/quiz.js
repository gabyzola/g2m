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
} from "./backend";
import "./style.css";

async function init() {
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

    setTimeout(() => { box.style.display = "none"; }, 4000);
  }

  const params = new URLSearchParams(window.location.search);
  const classId = params.get("classId");
  const quizId = params.get("quizId"); 
  const googleToken = sessionStorage.getItem("googleToken");

  console.log("debug: Google token from sessionStorage:", googleToken);

  let userId = null;
  let email = null;

  if (!googleToken) {
    document.getElementById("main").innerHTML = "<p>Error: Not logged in.</p>";
    return;
  }

  // await must be inside async function
  const user = await lookupUserBySub(googleToken);
  console.log("[DEBUG] User object returned from backend:", user);

  if (!user || user.userId <= 0) {
    document.getElementById("main").innerHTML = "<p>Error: Failed to identify user.</p>";
    return;
  }

  userId = user.userId;
  email = user.email;

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

  async function startSession(objectiveId = null) {
    attemptSessionId = await startAttemptSession(userId, quizId, objectiveId || 0);
    if (attemptSessionId <= 0) showMessage("We couldn't start your quiz session. Please try again.", "error");
    else console.log("Attempt session started:", attemptSessionId);
  }

  async function loadObjectives() {
    const objectives = await getQuizObjectives(quizId);

    if (!objectives || objectives.length === 0) {
      objectiveCard.style.display = "none";
      questionCard.style.display = "block";
      await startSession(); 
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

  submitObjectiveBtn.addEventListener("click", async () => {
    const selected = document.querySelector("input[name='objective']:checked");
    if (!selected) return showMessage("Please select at least one learning objective to continue.", "warning");

    const objectiveId = selected.value;
    const result = await selectObjectiveForQuiz(quizId, userId, objectiveId);

    if (result.status !== "success") return showMessage("One or more objectives could not be saved. Please try again.", "error");

    objectiveCard.style.display = "none";
    questionCard.style.display = "block";

    await startSession(objectiveId);
    loadQuestions();
  });

  async function loadQuestions() {
    const raw = await getStudentQuestions(userId, quizId);

    if (!raw || raw.length === 0) {
      qStem.textContent = "No questions available.";
      nextBtn.style.display = "none";
      return;
    }

    QUESTIONS = raw.map(q => {
      const choiceMap = {};    
      const choicesMap = {};
      ["A","B","C","D"].forEach((label, i) => {
        choicesMap[label] = q.choices[i].choiceText;
        choiceMap[label] = q.choices[i];
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

  nextBtn.addEventListener("click", async () => {
    const selected = document.querySelector('input[name="ans"]:checked');
    if (!selected) return showMessage("Please select an answer before continuing.", "warning");

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

    if (attemptSessionId > 0) {
      const chosenChoiceId = q.choiceMap[chosenLetter].choiceId;
      const success = await saveStudentAnswer(attemptSessionId, q.questionId, chosenChoiceId);
      if (!success) console.error("Failed to save question:", q.questionId);
    }

    userAnswers[q.questionId] = chosenLetter;

    setTimeout(async () => {
      index++;
      if (index >= QUESTIONS.length) await finishQuiz();
      else render();
    }, 1000);
  });

  let hasFinalized = false;
  async function finishQuiz() {
    if (hasFinalized) return;
    hasFinalized = true;

    const pct = Math.round((score / QUESTIONS.length) * 100);
    scoreLine.textContent = `You scored ${pct}%.`;
    resultModal.style.display = "flex";

    const newBadge = await assignBadge(userId);
    if (newBadge) scoreLine.textContent += ` You earned a new badge: ${newBadge}!`;

    if (attemptSessionId > 0) {
      const success = await finalizeAttemptSession(attemptSessionId);
      if (!success) console.error("Failed to finalize session");

      try {
        const resetSuccess = await fetch(`/api/remove/objectives/${userId}`, { method: 'DELETE' }).then(r => r.json());
        if (!resetSuccess) console.error("Failed to reset objectives");
      } catch (err) { console.error(err); }

      const results = await getAttemptResults(attemptSessionId);
      console.log("Attempt results:", results);
    }
  }

  await loadObjectives();
}

//call init to avoid top-level await
init();
