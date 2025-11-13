import { onReady } from '../lib/dom';
import { api, withFallback } from '../lib/api';
import { requireAuth } from '../lib/session';

const MOCK_CLASSES = [
  { classId: 101, className: 'PSYC 101 — Intro Psychology' },
  { classId: 202, className: 'PSYC 202 — Cognitive Science' },
  { classId: 303, className: 'STAT 303 — Statistics' },
];

onReady(async () => {
  const user = requireAuth({ role: 'student' });
  const studentId = user.studentId || import.meta.env.VITE_DEV_STUDENT_ID;
  const classSelect = document.getElementById('classSelect');
  const classPills = document.getElementById('classPills');
  const kHigh = document.getElementById('kpiHigh');
  const kAvg = document.getElementById('kpiAvg');
  const kStreak = document.getElementById('kpiStreak');
  const objectivesChips = document.getElementById('objectivesChips');
  const tableWrap = document.getElementById('tableWrap');
  const tabs = document.querySelectorAll('.tab');

  const classes = await loadClasses(studentId);
  if (!classes.length) {
    tableWrap.innerHTML = '<p class="muted">No classes found for your account.</p>';
    return;
  }

  let activeClass = classes[0].classId;
  let activeTab = 'quizzes';

  renderClassOptions();
  await update(activeClass);

  classSelect?.addEventListener('change', async (event) => {
    activeClass = Number(event.target.value);
    await update(activeClass);
  });

  classPills?.addEventListener('click', async (event) => {
    const btn = event.target.closest('.pill');
    if (!btn) return;
    activeClass = Number(btn.dataset.id);
    classSelect.value = activeClass;
    await update(activeClass);
  });

  tabs.forEach((tab) =>
    tab.addEventListener('click', () => {
      tabs.forEach((t) => t.classList.remove('active'));
      tab.classList.add('active');
      activeTab = tab.dataset.tab;
      updateTable(lastState);
    }),
  );

  let lastState = null;

  function renderClassOptions() {
    const options = classes
      .map((cls) => `<option value="${cls.classId}">${cls.className}</option>`)
      .join('');
    if (classSelect) classSelect.innerHTML = options;

    if (classPills) {
      classPills.innerHTML = classes
        .map(
          (cls) => `
        <button class="pill ${cls.classId === activeClass ? 'active' : ''}" data-id="${cls.classId}">
          ${cls.className}
        </button>`,
        )
        .join('');
    }
  }

  async function update(classId) {
    const quizzes = await withFallback(api.getClassQuizzes(classId), []);
    const state = buildState(quizzes);
    lastState = state;
    kHigh.textContent = `${state.high}%`;
    kAvg.textContent = `${state.avg}%`;
    kStreak.textContent = `${state.streak} quizzes`;
    objectivesChips.innerHTML = state.objectives.length
      ? state.objectives.map((obj) => `<span class="chip">${obj}</span>`).join(' ')
      : '<span class="chip">No objectives yet</span>';
    document.querySelectorAll('.pill').forEach((pill) =>
      pill.classList.toggle('active', Number(pill.dataset.id) === classId),
    );
    updateTable(state);
  }

  function updateTable(state) {
    if (!state) return;
    const rows = (activeTab === 'quizzes' ? state.quizzes : state.tests)
      .map(
        (quiz) => `
        <tr>
          <td><strong>${quiz.quizName || quiz.id}</strong></td>
          <td>${quiz.dueDate || quiz.date || '-'}</td>
          <td>${quiz.score ?? '—'}</td>
          <td>${quiz.objectives?.map((o) => `<span class="chip">${o}</span>`).join(' ') || '—'}</td>
        </tr>`,
      )
      .join('');

    tableWrap.innerHTML = `
      <table>
        <thead><tr><th>Quiz</th><th>Date</th><th>Score</th><th>Objectives</th></tr></thead>
        <tbody>${rows || '<tr><td colspan="4">No quizzes yet.</td></tr>'}</tbody>
      </table>`;
  }
});

async function loadClasses(studentId) {
  if (!studentId) return MOCK_CLASSES;
  const classes = await withFallback(api.getStudentClasses(studentId), []);
  return classes.length ? classes : MOCK_CLASSES;
}

function buildState(quizzes) {
  if (!Array.isArray(quizzes) || !quizzes.length) {
    return {
      quizzes: [],
      tests: [],
      high: 0,
      avg: 0,
      streak: 0,
      objectives: [],
    };
  }

  const scores = quizzes
    .map((quiz) => Number(quiz.scorePercent ?? quiz.score ?? 0))
    .filter((val) => !Number.isNaN(val));
  const high = scores.length ? Math.max(...scores) : 0;
  const avg = scores.length ? Math.round(scores.reduce((sum, val) => sum + val, 0) / scores.length) : 0;

  return {
    quizzes: quizzes.map((quiz) => ({
      quizName: quiz.quizName || quiz.name,
      date: quiz.dueDate || quiz.availableDate,
      score: quiz.scorePercent ? `${quiz.scorePercent}%` : '—',
      objectives: quiz.objectives || [],
    })),
    tests: [],
    high,
    avg,
    streak: scores.filter((val) => val >= 80).length,
    objectives: Array.from(
      new Set(
        quizzes
          .flatMap((quiz) => quiz.objectives || [])
          .map((obj) => (typeof obj === 'string' ? obj : obj.objectiveName || obj.learningObjective)),
      ),
    ).filter(Boolean),
  };
}
