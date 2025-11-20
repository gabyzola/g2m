//import backend.js to get the jsons 
import { getInstructorClasses, getStudentClasses } from "./api/backend.js";

//get student emails for dropdown
//enroll student -> "Add" button

document.addEventListener("DOMContentLoaded", () => {
  const loadClassesBtn = document.getElementById("loadClasses");

  loadClassesBtn.addEventListener("click", async () => {
    //added a button for testing, this reads in the inputted instructor id
    const instructorId = document.getElementById("instructorId").value;

    //fetches the classes from backend.js, stores them in classes
    const classes = await getInstructorClasses(instructorId);

    const list = document.getElementById("classList");
    list.innerHTML = ""; //clears prev resultsa

    //error chekcS
    if (!classes) {
      list.innerHTML = "<li>Error loading classes</li>";
      return;
    }

    //iterates through each class that was returned
    classes.forEach(c => {
      const li = document.createElement("li"); //this cerates list element
      const link = document.createElement("a"); //this makes it a link

      //this sets the link to the actual class page where classId specifies which information needs to go on the page
      link.href = `/class-module.html?classId=${c.classId}`;
      link.textContent = `${c.className} (ID: ${c.classId})`;

      li.appendChild(link); //add links ands list
      list.appendChild(li);
    });
  });

  const loadStudentBtn = document.getElementById("loadStudentClasses");
  loadStudentBtn.addEventListener("click", async () => {
    const studentId = document.getElementById("studentId").value;
    const classes = await getStudentClasses(studentId);

    const list = document.getElementById("studentClassList");
    list.innerHTML = "";

    if (!classes) {
      list.innerHTML = "<li>Error loading classes</li>";
      return;
    }

    classes.forEach(c => {
      const li = document.createElement("li");
      const link = document.createElement("a");
      link.href = `/class-module.html?classId=${c.classId}`;
      link.textContent = `${c.className} (ID: ${c.classId})`;
      li.appendChild(link);
      list.appendChild(li);
    });
  });

  /*
  //add create new class button
      const enrollBtn = document.createElement("button");
      enrollBtn.textContent = "Create Class";
      enrollBtn.id = "enrollStudentBtn";
      enrollBtn.style.marginBottom = "1rem";
      quizzesContainer.prepend(enrollBtn);

      enrollBtn.addEventListener("click", () => {
        //redirect to enrollment page with classId in query string
        window.location.href = `class-create.html?classId=${classId}`;
      });
      */
});
