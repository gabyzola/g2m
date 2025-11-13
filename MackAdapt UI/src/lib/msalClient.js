import { PublicClientApplication } from '@azure/msal-browser';

// Base MSAL configuration shared by every page so we stay consistent with tenant + cache settings.
const BASE_CONFIG = {
  auth: {
    clientId: 'f1eb948d-5297-47ce-a4c4-419d1f699607',
    authority: 'https://login.microsoftonline.com/57e1236c-0fa3-4739-9edf-fd0721c8744d/',
  },
  cache: {
    cacheLocation: 'localStorage',
    storeAuthStateInCookie: false,
  },
};

// Reusable scope definition for all Microsoft logins (basic profile info + email).
const LOGIN_REQUEST = {
  scopes: ['openid', 'profile', 'email', 'User.Read'],
};

/**
 * Guard that makes sure Web Crypto is available (PKCE depends on it).
 * @param {(message: string) => void} [onMissing] optional callback (e.g., alert) for user-friendly messaging
 * @returns {boolean} whether Web Crypto APIs are ready
 */
export function ensureWebCryptoReady(onMissing) {
  const ready = Boolean(window.crypto?.subtle?.digest);
  if (!ready) {
    const message = 'Browser crypto is not available. Microsoft login cannot proceed.';
    console.error(message);
    onMissing?.(message);
  }
  return ready;
}

/**
 * Initialize a MSAL client that can be reused across a page.
 * @param {{redirectPath?: string}} options allows pages to override the default redirect URI
 */
export function initMsal(options = {}) {
  const { redirectPath } = options;

  // Default redirect is the current path so page reloads after auth look seamless.
  const redirectUri = redirectPath
    ? new URL(redirectPath, window.location.origin).toString()
    : window.location.origin + window.location.pathname;

  const instance = new PublicClientApplication({
    ...BASE_CONFIG,
    auth: {
      ...BASE_CONFIG.auth,
      redirectUri,
    },
  });

  return { msalInstance: instance, loginRequest: LOGIN_REQUEST };
}

/**
 * Attempt to load the active account and invoke callbacks when available.
 * @param {import('@azure/msal-browser').PublicClientApplication} msalInstance
 * @param {(account: import('@azure/msal-browser').AccountInfo) => void} onAuthenticated
 * @param {() => void} [onMissingAccount]
 */
export async function hydrateAccount(msalInstance, onAuthenticated, onMissingAccount) {
  try {
    // First check for an auth response via redirect, then fall back to cached accounts.
    const response = await msalInstance.handleRedirectPromise();
    const account =
      response?.account ||
      msalInstance.getActiveAccount() ||
      msalInstance.getAllAccounts()[0];

    if (account) {
      msalInstance.setActiveAccount(account);
      await onAuthenticated?.(account);
      return account;
    }

    onMissingAccount?.();
    return null;
  } catch (err) {
    console.error('MSAL redirect handling failed', err);
    onMissingAccount?.();
    return null;
  }
}

/**
 * Wire a login button to the MSAL popup flow.
 * @param {HTMLElement | null} target
 * @param {import('@azure/msal-browser').PublicClientApplication} msalInstance
 * @param {import('@azure/msal-browser').PopupRequest} loginRequest
 * @param {(account: import('@azure/msal-browser').AccountInfo) => void} [onAuthenticated]
 */
export function attachPopupLogin(target, msalInstance, loginRequest, onAuthenticated) {
  if (!target) return;

  target.addEventListener('click', async () => {
    try {
      const response = await msalInstance.loginPopup(loginRequest);
      msalInstance.setActiveAccount(response.account);
      onAuthenticated?.(response.account);
    } catch (err) {
      console.error('MSAL popup login failed', err);
      alert('Login failed. Check console for details.');
    }
  });
}
