import { onReady } from '../lib/dom';
import { api, withFallback } from '../lib/api';
import { requireAuth } from '../lib/session';

onReady(async () => {
  const searchInput = document.getElementById('search');
  const addBtn = document.getElementById('addBtn');
  const results = document.getElementById('results');
  const enrolled = document.getElementById('enrolled');
  const params = new URLSearchParams(location.search);
  const classId = Number(params.get('class')) || null;
  const header = document.querySelector('main.wrap h2');
  const activeUser = requireAuth({ role: 'instructor' });

  if (!classId) {
    results.innerHTML = '<p class="empty">Missing class id.</p>';
    return;
  }

  if (header) header.textContent = `Enroll Students — Class #${classId}`;
  renderRoster(await fetchRoster());

  addBtn?.addEventListener('click', async () => {
    const email = searchInput.value.trim();
    if (!email) {
      alert('Enter a student email.');
      return;
    }
    toggleDisabled(addBtn, true);
    try {
      await api.enrollStudent({ classId, email });
      searchInput.value = '';
      renderRoster(await fetchRoster());
    } catch (err) {
      alert(err.message || 'Unable to enroll student.');
    } finally {
      toggleDisabled(addBtn, false);
    }
  });

  document.addEventListener('click', async (event) => {
    const email = event.target?.getAttribute?.('data-email');
    if (!email) return;
    searchInput.value = email;
  });

  async function fetchRoster() {
    return withFallback(api.getClassEnrollees(classId), []);
  }

  function renderRoster(roster) {
    if (!roster.length) {
      enrolled.innerHTML = '<p class="empty">No students enrolled yet.</p>';
      return;
    }
    enrolled.innerHTML = roster
      .map(
        (student) => `
      <div class="user">
        <div>
          <strong>${student.firstName} ${student.lastName}</strong><br/>
          <small>${student.email || ''}</small>
        </div>
        <button type="button" data-email="${student.email}">Copy email</button>
      </div>`,
      )
      .join('');
    results.innerHTML = '<p class="muted">Enter a Merrimack email to enroll.</p>';
  }
});

function toggleDisabled(button, isDisabled) {
  if (!button) return;
  button.disabled = isDisabled;
  button.classList.toggle('is-loading', isDisabled);
}
