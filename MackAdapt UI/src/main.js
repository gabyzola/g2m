import { getInstructorClasses, getStudentClasses, isUserInstructor, lookupUserBySub } from "./api/backend.js";

document.addEventListener("DOMContentLoaded", async () => {

  const googleToken = sessionStorage.getItem("googleToken");
  console.log("[DEBUG] Google token from sessionStorage:", googleToken);

  if (!googleToken) {
    document.getElementById("classesHeading").textContent = "No user logged in.";
    return;
  }

  // fetch current user info from backend
  const user = await lookupUserBySub(googleToken);
  console.log("[DEBUG] User object returned from backend:", user);
  if (!user || !user.userId) {
    document.getElementById("classesHeading").textContent = "Failed to identify user.";
    return;
  }

  const userId = user.userId;
  const email = user.email;
  

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
    const createBtn = document.getElementById("createClassBtn");
    createBtn.style.display = "inline-block";
    createBtn.addEventListener("click", () => {
      document.getElementById("createClassModal").style.display = "flex";
    });

    // Modal cancel
    document.getElementById("cancelCreateClass").addEventListener("click", () => {
      document.getElementById("createClassModal").style.display = "none";
    });

    // Modal submit
    document.getElementById("submitCreateClass").addEventListener("click", async () => {
      const className = document.getElementById("newClassName").value.trim();
      if (!className) {
        alert("Please enter a class name.");
        return;
      }

      const classId = document.getElementById("newClassId").value.trim();
      if (!classId) {
        alert("Please enter a class Id.");
        return;
      }

      // Prepare payload
      const payload = {
        classId, // backend can auto-generate or adjust as needed
        className,
        instructorEmail: email
      };

      try {
        const res = await fetch("/api/classes/create", {
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify(payload)
        });

        if (!res.ok) throw new Error("Failed to create class");

        const success = await res.json();
        if (success) {
          window.location.href = "class-enroll.html?classId=${classId}"; // redirect after creation
        } else {
          alert("Failed to create class");
        }
      } catch (err) {
        console.error("Error creating class:", err);
        alert("Server error while creating class.");
      }
    });
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

    link.href = `/class-module.html?classId=${c.classId}`;
    link.textContent = `${c.className} (ID: ${c.classId})`;

    li.appendChild(link);
    classList.appendChild(li);
  });
});
