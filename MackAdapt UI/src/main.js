import { getInstructorClasses, getStudentClasses, isUserInstructor } from "./api/backend.js";

document.addEventListener("DOMContentLoaded", async () => {
  //get userid from url
  const params = new URLSearchParams(window.location.search);
  const userId = params.get("userId");
  console.log("[DEBUG] userId from URL:", userId);

  if (!userId) {
    document.getElementById("classesHeading").textContent = "No user logged in.";
    return;
  }

  const classList = document.getElementById("classList");
  classList.innerHTML = "";

  //check what the id is instructor or user
  const instructor = await isUserInstructor(userId);
  console.log("[DEBUG] is instructor?", instructor);

  let classes = [];
  const heading = document.getElementById("classesHeading");

  if (instructor) {
    console.log("[DEBUG] Loading instructor classes");
    heading.textContent = "Your Instructor Classes";
    classes = await getInstructorClasses(userId);
  } else {
    console.log("[DEBUG] Loading student classes");
    heading.textContent = "Your Classes";
    classes = await getStudentClasses(userId);
  }

  if (!classes || classes.length === 0) {
    classList.innerHTML = "<li>No classes found.</li>";
    return;
  }

  classes.forEach(c => {
    const li = document.createElement("li");
    const link = document.createElement("a");

    link.href = `/class-module.html?classId=${c.classId}&userId=${userId}`;
    link.textContent = `${c.className} (ID: ${c.classId})`;

    li.appendChild(link);
    classList.appendChild(li);
  });
});
