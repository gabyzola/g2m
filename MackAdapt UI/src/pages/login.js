import { initMsal, hydrateAccount, ensureWebCryptoReady } from '../lib/msalClient';
import { onReady } from '../lib/dom';
import { api } from '../lib/api';
import { setActiveUser } from '../lib/session';

const CLASSES_PATH = '/classes.html';
const PENDING_PROFILE_KEY = 'mackadapt-pending-profile';

// Login controller: collects profile details, registers via API, and stores session context.
onReady(async () => {
  const msButton = document.getElementById('ms-login');
  const demoForm = document.getElementById('demo-login');
  const demoSubmit = demoForm?.querySelector('button[type="submit"]');
  const emailInput = document.getElementById('email');
  const passwordInput = document.getElementById('password');
  const firstNameInput = document.getElementById('firstName');
  const lastNameInput = document.getElementById('lastName');
  const majorInput = document.getElementById('major');
  const subjectInput = document.getElementById('subject');
  const majorField = document.getElementById('majorField');
  const subjectField = document.getElementById('subjectField');

  const roleInputs = Array.from(document.querySelectorAll('input[name="role"]'));
  const updateRoleFields = () => {
    const role = getSelectedRole();
    const isInstructor = role === 'instructor';
    majorField.style.display = isInstructor ? 'none' : 'block';
    subjectField.style.display = isInstructor ? 'block' : 'none';
    majorInput.required = !isInstructor;
    subjectInput.required = isInstructor;
  };
  roleInputs.forEach((input) => input.addEventListener('change', updateRoleFields));
  updateRoleFields();

  // Block the Microsoft flow if PKCE prerequisites are missing.
  if (!ensureWebCryptoReady((message) => alert(message))) return;

  const { msalInstance, loginRequest } = initMsal({ redirectPath: CLASSES_PATH });

  const redirectToClasses = () => {
    window.location.href = CLASSES_PATH;
  };

  // Handle redirect responses first – Microsoft Authenticator returns here.
  const redirectAccount = await hydrateAccount(msalInstance, async (account) => {
    const pending = consumePendingProfile() || {};
    await registerAndStore({
      account,
      email: account?.username,
      firstName: pending.firstName,
      lastName: pending.lastName,
      role: pending.role || 'student',
      major: pending.major,
      subject: pending.subject,
    });
    redirectToClasses();
  });
  if (redirectAccount) return;

  // Restore any cached session (e.g., returning user) without extra clicks.
  const cachedAccount = msalInstance.getAllAccounts()[0];
  if (cachedAccount) {
    await registerAndStore({
      account: cachedAccount,
      email: cachedAccount.username,
      role: 'student',
    });
    redirectToClasses();
    return;
  }

  // Use redirect instead of popup for better MFA/device support.
  msButton?.addEventListener('click', () => {
    const profile = collectProfileFromForm({
      firstNameInput,
      lastNameInput,
      majorInput,
      subjectInput,
    });
    if (!profile) return;
    sessionStorage.setItem(PENDING_PROFILE_KEY, JSON.stringify(profile));
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
    try {
      const profile = collectProfileFromForm({
        firstNameInput,
        lastNameInput,
        majorInput,
        subjectInput,
        fallbackEmail: email,
      });
      if (!profile) return;
      await registerAndStore({
        email,
        role: profile.role,
        firstName: profile.firstName,
        lastName: profile.lastName,
        major: profile.major,
        subject: profile.subject,
      });
      await new Promise((resolve) => setTimeout(resolve, 400));
      redirectToClasses();
    } catch (err) {
      console.error(err);
      alert(err.message || 'Unable to complete registration.');
    } finally {
      toggleLoading(demoSubmit, false);
    }
  });
});

function getSelectedRole() {
  return document.querySelector('input[name="role"]:checked')?.value || 'student';
}

function consumePendingProfile() {
  const raw = sessionStorage.getItem(PENDING_PROFILE_KEY);
  if (!raw) return null;
  sessionStorage.removeItem(PENDING_PROFILE_KEY);
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

function collectProfileFromForm({ firstNameInput, lastNameInput, majorInput, subjectInput, fallbackEmail }) {
  const firstName = firstNameInput?.value.trim();
  const lastName = lastNameInput?.value.trim();
  const role = getSelectedRole();
  const major = majorInput?.value.trim();
  const subject = subjectInput?.value.trim();

  if (!firstName || !lastName) {
    alert('Please provide your name before continuing.');
    return null;
  }

  if (role === 'student' && !major) {
    alert('Please enter your major.');
    return null;
  }

  if (role === 'instructor' && !subject) {
    alert('Please enter your subject.');
    return null;
  }

  return {
    firstName,
    lastName,
    role,
    major: role === 'student' ? major : '',
    subject: role === 'instructor' ? subject : '',
    email: fallbackEmail,
  };
}

async function registerAndStore({ account, email, role = 'student', firstName, lastName, major, subject }) {
  const resolvedEmail = email || account?.username;
  if (!resolvedEmail) throw new Error('Unable to determine email for registration.');

  const payload = {
    username: account?.name || resolvedEmail.split('@')[0],
    email: resolvedEmail,
    isInstructor: role === 'instructor',
    major: role === 'student' ? (major || 'Undeclared') : '',
    schoolSubject: role === 'instructor' ? (subject || 'General Studies') : '',
    firstName: firstName || account?.name?.split(' ')[0] || '',
    lastName: lastName || account?.name?.split(' ').slice(1).join(' ') || '',
  };

  await api.registerUser(payload);
  setActiveUser({
    email: payload.email,
    role: payload.isInstructor ? 'instructor' : 'student',
    firstName: payload.firstName,
    lastName: payload.lastName,
    major: payload.major,
    subject: payload.schoolSubject,
    instructorId: payload.isInstructor ? import.meta.env.VITE_DEV_INSTRUCTOR_ID || null : null,
    studentId: payload.isInstructor ? null : import.meta.env.VITE_DEV_STUDENT_ID || null,
  });
}

// Tiny helper to show a spinner on the mock "Continue" button.
function toggleLoading(button, isLoading) {
  if (!button) return;
  button.disabled = isLoading;
  button.classList.toggle('is-loading', isLoading);
}
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
