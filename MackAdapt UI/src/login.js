import { lookupUser } from "./backend";
import "./style.css";

let currentGoogleSub = null;
 
window.initGoogleSignIn = function () {
  const GOOGLE_CLIENT_ID =
    "762914522263-mshmosk8e89upni0c0p3upirltuqt7m6.apps.googleusercontent.com";

  if (!window.google) {
    console.error("Google GSI not loaded");
    return;
  }

  google.accounts.id.initialize({
    client_id: GOOGLE_CLIENT_ID,
    callback: handleGoogleCredentialResponse
  });

  google.accounts.id.renderButton(
    document.getElementById("g-signin"),
    {
      theme: "filled_blue",
      size: "large",
      width: 260
    }
  );
};

// Global handler for Google Sign-In
window.handleGoogleCredentialResponse = async function(response) {
  try {
    const jwt = response.credential;
    const decoded = JSON.parse(atob(jwt.split('.')[1]));
    const email = decoded.email;
    const sub = decoded.sub;

    if (!email || !sub) throw new Error("Invalid Google credential.");
    currentGoogleSub = sub;

    const userId = await lookupUser(email);

    if (userId > 0) {
      sessionStorage.setItem("googleToken", currentGoogleSub);
      window.location.href = "/classes.html";
      return;
    }

    if (!email.endsWith("@merrimack.edu")) {
      const domainError = document.getElementById("domainError");
      domainError.textContent = "Sign-in failed. Please use your Merrimack (@merrimack.edu) Google account.";
      domainError.style.display = "block";
      return;
    }

    if (userId === -1) {
      document.getElementById("modalBg").style.display = "flex";
      document.getElementById("regEmail").value = email;
      return;
    }

    alert("Unexpected error during login.");
  } catch (err) {
    console.error("Google login error:", err);
    alert("Login failed. Please try again.");
  }
};

// Modal handling
function toggleRoleFields() {
  const role = document.getElementById("regRole").value;
  document.getElementById("majorField").style.display = role === "student" ? "block" : "none";
  document.getElementById("subjectField").style.display = role === "instructor" ? "block" : "none";
}

async function submitRegistration() {
  const email = document.getElementById("regEmail").value;
  const first = document.getElementById("regFirstName").value.trim();
  const last = document.getElementById("regLastName").value.trim();
  const role = document.getElementById("regRole").value;
  const major = document.getElementById("majorField").value.trim();
  const subject = document.getElementById("subjectField").value.trim();

  if (!first || !last || !role) {
    alert("Please fill out all required fields.");
    return;
  }

  const payload = {
    googleSub: currentGoogleSub,
    email,
    isInstructor: role === "instructor",
    major,
    schoolSubject: subject,
    firstName: first,
    lastName: last
  };

  try {
    const response = await fetch("api/users/register", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(payload)
    });

    const newUser = await response.json();

    if (!newUser || !newUser.userId) {
      alert("Registration failed.");
      return;
    }

    sessionStorage.setItem("googleToken", currentGoogleSub);
    window.location.href = "/classes.html";
  } catch (err) {
    console.error(err);
    alert("Server error during registration.");
  }
}

// Attach modal event listeners
document.getElementById("createAccountBtn").addEventListener("click", submitRegistration);
document.getElementById("regRole").addEventListener("change", toggleRoleFields);
