import { onReady } from '../lib/dom';
import { requireAuth } from '../lib/session';

// Instructor dashboard controller: ensure session exists and show greeting.
onReady(() => {
  const user = requireAuth({ role: 'instructor' });
  const welcomeText = document.getElementById('welcome-text');
  const emailEl = document.getElementById('user-email');

  if (welcomeText) welcomeText.textContent = `Welcome, ${user.firstName || 'Instructor'} 👋`;
  if (emailEl) emailEl.textContent = user.email;
});
