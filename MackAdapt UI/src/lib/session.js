const STORAGE_KEY = 'mackadapt-active-user';

function readStorage() {
  try {
    return JSON.parse(sessionStorage.getItem(STORAGE_KEY) || 'null');
  } catch {
    return null;
  }
}

function writeStorage(value) {
  if (!value) {
    sessionStorage.removeItem(STORAGE_KEY);
  } else {
    sessionStorage.setItem(STORAGE_KEY, JSON.stringify(value));
  }
}

export function getActiveUser() {
  return readStorage();
}

export function setActiveUser(user) {
  writeStorage(user);
}

export function clearSession() {
  writeStorage(null);
}

export function requireAuth({ role } = {}) {
  const user = getActiveUser();
  if (!user) {
    window.location.href = '/login.html';
    throw new Error('User not authenticated');
  }

  if (role && user.role !== role) {
    console.warn(`Expected role "${role}" but session is "${user.role}"`);
  }

  return user;
}
