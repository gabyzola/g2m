import { 
        searchStudents, 
        getClassEnrollees, 
        enrollStudent, 
        removeStudent 
      } from "./src/api/backend.js";
      import "./style.css";

      const params = new URLSearchParams(window.location.search);
      const classId = params.get("classId"); //make sure enrollment is happening for the correct class

      if (!classId) {
        alert("No class selected.");
      }

      // Add Back button link dynamically
    const backButton = document.createElement('a');
    backButton.textContent = "← Back to Class Module";
    backButton.href = `/class-module.html?classId=${classId}`;
    backButton.style.cssText = "display:inline-block; padding:.5rem 1rem; background:#ccd; border-radius:6px; text-decoration:none; color:#000; font-weight:600; margin-bottom:1rem;";
    document.querySelector("main.wrap").prepend(backButton);


      //html element ids
      const results = document.getElementById("results");
      const enrolled = document.getElementById("enrolled");
      const search = document.getElementById("search");
      const addBtn = document.getElementById("addBtn");

      async function loadEnrolled() {
        const list = await getClassEnrollees(classId);//gets who is already enrolled and list it

        if (!list || list.length === 0) {
          enrolled.innerHTML = `<p class="empty">No students enrolled yet.</p>`;
          return;
        }

        enrolled.innerHTML = list.map(s => `
          <div class="user">
            <div>
              <strong>Student #${s.studentId}</strong><br/>
              <small>${s.email}</small>
            </div>
            <button type="button" data-remove-id="${s.studentId}">Remove</button>
          </div>
        `).join('');
      }


      async function doSearch() {
        const query = search.value.trim();
        if (!query) {
          results.innerHTML = `<p class="empty">Start typing to search...</p>`;
          return;
        }

        const matches = await searchStudents(query);

        if (!matches || matches.length === 0) {
          results.innerHTML = `<p class="empty">No matching students.</p>`;
          return;
        }

        results.innerHTML = matches.map(s => `
          <div class="user">
            <div>
              <strong>Student #${s.studentId}</strong><br/>
              <small>${s.email}</small><br/>
              <small>Major: ${s.major}</small>
            </div>
            <button type="button" data-add-email="${s.email}">Add</button>
          </div>
        `).join('');
      }


      search.addEventListener("input", doSearch);

      addBtn.addEventListener("click", async () => {
        const query = search.value.trim();
        const matches = await searchStudents(query);

        if (matches[0]) {
          await enrollStudent(classId, matches[0].email);
          await loadEnrolled();
          await doSearch();
        }
      });


      document.addEventListener("click", async (e) => {
        const add = e.target.closest("[data-add-email]");
        if (add) {
          const email = add.getAttribute("data-add-email");
          await enrollStudent(classId, email);
          await loadEnrolled();
          await doSearch();
        }

        const remove = e.target.closest("[data-remove-id]");
        if (remove) {
          const studentId = remove.getAttribute("data-remove-id");
          await removeStudent(classId, studentId); //remove student from class
          await loadEnrolled();
          await doSearch();
        }
      });

        loadEnrolled();