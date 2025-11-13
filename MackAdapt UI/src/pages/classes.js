import { onReady } from '../lib/dom';
import { api, withFallback } from '../lib/api';
import { requireAuth } from '../lib/session';

// Classes page controller: loads instructor classes and handles quick create.
onReady(async () => {
  const cards = document.getElementById('classCards');
  const createBtn = document.getElementById('create-class-btn');
  const user = requireAuth();
  const instructorId = user.instructorId || import.meta.env.VITE_DEV_INSTRUCTOR_ID;
  const instructorEmail = user.email;

  if (!instructorId) {
    cards.innerHTML =
      '<p class="muted">Link your instructor account to see classes. Set VITE_DEV_INSTRUCTOR_ID while backend catches up.</p>';
    createBtn.disabled = true;
    return;
  }

  let classes = await loadClasses(instructorId);
  renderClasses(classes);

  createBtn?.addEventListener('click', async () => {
    const className = prompt('Enter class name (e.g., PSYC 110)');
    if (!className) return;
    const classId = Number(prompt('Enter numeric course code (e.g., 110)'));
    if (!classId || Number.isNaN(classId)) {
      alert('Course code must be numeric.');
      return;
    }
    try {
      await api.createClass({ classId, className, instructorEmail });
      classes = await loadClasses(instructorId);
      renderClasses(classes);
    } catch (err) {
      alert(err.message || 'Unable to create class.');
    }
  });

  function renderClasses(list) {
    if (!list.length) {
      cards.innerHTML = '<p class="muted">No classes yet. Create one to get started.</p>';
      return;
    }
    cards.innerHTML = list
      .map(
        (cls) => `
        <article class="class-card">
          <h3>${cls.className}</h3>
          <p>Instructor: ${cls.instructorFirstName || ''} ${cls.instructorLastName || ''}</p>
          <div style="display:flex;gap:.5rem;flex-wrap:wrap">
            <a class="button" href="class-enroll.html?class=${cls.classId}">Enroll Students</a>
            <a class="button" href="class-enroll.html?class=${cls.classId}#roster">View Roster</a>
          </div>
        </article>`,
      )
      .join('');
  }
});

async function loadClasses(instructorId) {
  const list = await withFallback(api.getInstructorClasses(instructorId), []);
  return list.length ? list : [];
}
