import { initMsal, attachPopupLogin, hydrateAccount } from '../lib/msalClient';
import { onReady } from '../lib/dom';

// Landing-page controller: wires MSAL login button + AOS animation kick-off.
onReady(async () => {
  // Activate scroll animations if the CDN script loaded successfully.
  if (window.AOS?.init) window.AOS.init();

  const loginButton = document.getElementById('ms-login-header');
  const welcomeText = document.getElementById('welcome-text');

  // Helper updates both the header button and hero copy once we know the user.
  function updateGreetings(account) {
    if (loginButton) loginButton.textContent = `Hello, ${account.name}`;
    if (welcomeText) welcomeText.textContent = `Welcome, ${account.name} 👋`;
  }

  const { msalInstance, loginRequest } = initMsal();

  // Keep the click-to-login popup for the marketing header button.
  attachPopupLogin(loginButton, msalInstance, loginRequest, updateGreetings);
  // Also hydrate on page load in case there is an existing session.
  await hydrateAccount(msalInstance, updateGreetings);
});
