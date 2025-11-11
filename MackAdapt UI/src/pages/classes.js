import { initMsal, attachPopupLogin, hydrateAccount } from '../lib/msalClient';
import { onReady } from '../lib/dom';

// Classes page controller: requires auth and mirrors the shared header state.
onReady(async () => {
  const loginButton = document.getElementById('ms-login-header');
  const welcomeText = document.getElementById('welcome-text');
  const emailEl = document.getElementById('user-email');

  // Update all UI fields that display the signed-in user.
  function applyAccount(account) {
    if (loginButton) loginButton.textContent = `Hello, ${account.name}`;
    if (welcomeText) welcomeText.textContent = `Welcome, ${account.name} 👋`;
    if (emailEl) emailEl.textContent = account.username;
  }

  const { msalInstance, loginRequest } = initMsal();

  // Allow instructors to switch accounts without leaving the page.
  attachPopupLogin(loginButton, msalInstance, loginRequest, applyAccount);

  // Require an authenticated session; otherwise send them back to login.
  await hydrateAccount(msalInstance, applyAccount, () => {
    window.location.href = 'login.html';
  });
});
