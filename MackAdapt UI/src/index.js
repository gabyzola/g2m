
import AOS from "aos";
import "aos/dist/aos.css"; // import AOS styles
import "./style.css";

//init AOS
AOS.init();

//oauth
const GOOGLE_CLIENT_ID = "762914522263-mshmosk8e89upni0c0p3upirltuqt7m6.apps.googleusercontent.com";

export function initGoogle() {
  google.accounts.id.initialize({
    client_id: GOOGLE_CLIENT_ID,
    callback: handleGoogleResponse,
  });
  google.accounts.id.prompt();
}

export function handleGoogleResponse(response) {
  const credential = response.credential;
  //redirects user after login
  window.location.href = "/classes.html";
}

window.onload = initGoogle;
