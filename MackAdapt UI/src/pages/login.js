import { initMsal, hydrateAccount, ensureWebCryptoReady } from '../lib/msalClient';
import { onReady } from '../lib/dom';

const CLASSES_PATH = '/classes.html';

// Login page controller: preserves classmates' redirect + Web Crypto guard flow.
onReady(async () => {
  const msButton = document.getElementById('ms-login');
  const demoForm = document.getElementById('demo-login');
  const demoSubmit = demoForm?.querySelector('button[type="submit"]');
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');

  // Block the Microsoft flow if PKCE prerequisites are missing.
  if (!ensureWebCryptoReady((message) => alert(message))) return;

  const { msalInstance, loginRequest } = initMsal({ redirectPath: CLASSES_PATH });

  const redirectToClasses = () => {
    window.location.href = CLASSES_PATH;
  };

  // Handle redirect responses first – Microsoft Authenticator returns here.
  const redirectAccount = await hydrateAccount(msalInstance, () => redirectToClasses());
  if (redirectAccount) return;

  // Restore any cached session (e.g., returning user) without extra clicks.
  const cachedAccount = msalInstance.getAllAccounts()[0];
  if (cachedAccount) {
    msalInstance.setActiveAccount(cachedAccount);
    redirectToClasses();
    return;
  }

  // Use redirect instead of popup for better MFA/device support.
  msButton?.addEventListener('click', () => {
    msalInstance.loginRedirect(loginRequest);
  });

  // Lightweight mock login for demos/class testing.
  demoForm?.addEventListener('submit', async (event) => {
    event.preventDefault();
    const email = emailInput?.value.trim();
    const password = passwordInput?.value.trim();

    if (!email || !password) {
      alert('Enter email and password.');
      return;
    }

    toggleLoading(demoSubmit, true);
    await new Promise((resolve) => setTimeout(resolve, 600));
    toggleLoading(demoSubmit, false);
    redirectToClasses();
  });
});

// Tiny helper to show a spinner on the mock "Continue" button.
function toggleLoading(button, isLoading) {
  if (!button) return;
  button.disabled = isLoading;
  button.classList.toggle('is-loading', isLoading);
}
