import { initMsal, hydrateAccount } from '../lib/msalClient';
import { onReady } from '../lib/dom';

// Instructor dashboard controller: block access until MSAL says we have an account.
onReady(async () => {
  const welcomeText = document.getElementById('welcome-text');
  const emailEl = document.getElementById('user-email');

  const { msalInstance, loginRequest } = initMsal();

  await hydrateAccount(
    msalInstance,
    (account) => {
      if (welcomeText) welcomeText.textContent = `Welcome, ${account.name} 👋`;
      if (emailEl) emailEl.textContent = account.username;
    },
    // Force a redirect login flow if MSAL cannot find credentials.
    () => msalInstance.loginRedirect(loginRequest),
  );
});
