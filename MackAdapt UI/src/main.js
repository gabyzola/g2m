import { getInstructorClasses } from "./api/backend.js";

document.addEventListener("DOMContentLoaded", () => {
  const loadClassesBtn = document.getElementById("loadClasses");

  loadClassesBtn.addEventListener("click", async () => {
    const instructorId = document.getElementById("instructorId").value;

    const classes = await getInstructorClasses(instructorId);

    const list = document.getElementById("classList");
    list.innerHTML = "";

    if (!classes) {
      list.innerHTML = "<li>Error loading classes</li>";
      return;
    }

    classes.forEach(c => {
      const li = document.createElement("li");
      li.textContent = `${c.className} (ID: ${c.classId})`;
      list.appendChild(li);
    });
  });
});

/*<script>
    const msalConfig = {
      auth: {
        clientId: "f1eb948d-5297-47ce-a4c4-419d1f699607",
        authority: "https://login.microsoftonline.com/57e1236c-0fa3-4739-9edf-fd0721c8744d/",
        redirectUri: window.location.origin + window.location.pathname
      },
      cache: { cacheLocation: "localStorage", storeAuthStateInCookie: false }
    };

    const msalInstance = new msal.PublicClientApplication(msalConfig);
    const loginRequest = { scopes: ["openid","profile","email","User.Read"] };
    const loginBtn = document.getElementById("ms-login-header");
    

    // Handle header login button click
    loginBtn.addEventListener("click", async () => {
      try {
        const response = await msalInstance.loginPopup(loginRequest);
        msalInstance.setActiveAccount(response.account);
        loginBtn.textContent = `Hello, ${response.account.name}`;
        document.getElementById("welcome-text").textContent = `Welcome, ${response.account.name} 👋`;
        document.getElementById("user-email").textContent = response.account.username;
      } catch (err) {
        console.error("Login failed:", err);
        alert("Login failed. See console for details.");
      }
    });

    // Handle redirect response or existing session
    window.addEventListener("DOMContentLoaded", () => {
      msalInstance.handleRedirectPromise()
        .then(resp => {
          const account = resp?.account || msalInstance.getActiveAccount();
          if (account) {
            msalInstance.setActiveAccount(account);
            loginBtn.textContent = `Hello, ${account.name}`;
            document.getElementById("welcome-text").textContent = `Welcome, ${account.name} 👋`;
            document.getElementById("user-email").textContent = account.username;
          } else {
            // Not signed in → redirect to login
            window.location.href = "login.html";
          }
        })
        .catch(console.error);
    });
  </script> */