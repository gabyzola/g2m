import { 
      removeUser,
      getStudentTotalPoints, 
      lookupUserBySub
    } from "./backend";
    import "./style.css"; // Import CSS for Vite to bundle

    let userId = null;

    function openDeleteModal() {
      document.getElementById('deleteModal').style.display = 'flex';
    }
    function closeDeleteModal() {
      document.getElementById('deleteModal').style.display = 'none';
    }
    function mockDeleteAccount() {
      alert("Account would be deleted.");
      closeDeleteModal();
    }

    //defines all badges and point thresholds
    const badges = [
      { name: "Rookie", img: "/Rookie.png", points: 0, description: "Complete your first quiz" },
      { name: "Steady Learner", img: "/Rookie (1).png", points: 100, description: "Reach 100 total points" },
      { name: "Knowledge Collector", img: "/Rookie (2).png", points: 500, description: "Reach 500 total points" },
      { name: "Quiz Master", img: "/Rookie (3).png", points: 700, description: "Reach 700 total points" },
      { name: "Legend", img: "/Rookie (4).png", points: 1000, description: "Reach 1000 total points" }
    ];

    async function renderBadges(userId) {
      const points = await getStudentTotalPoints(userId);
      const badgeGrid = document.querySelector(".badge-grid");
      badgeGrid.innerHTML = ""; 

      badges.forEach(badge => {
        const earned = points >= badge.points;
        const div = document.createElement("div");
        div.classList.add("badge-item");
        if (!earned) div.style.filter = "grayscale(80%) opacity(0.5)"; //grayed if not eligable

        div.innerHTML = `
          <img src="${badge.img}" alt="${badge.name}" />
          <div>${badge.description}</div>
        `;
        badgeGrid.appendChild(div);
      });
    }

    // Initialize dashboard
    async function initDashboard() {
      const googleToken = sessionStorage.getItem("googleToken");
      if (!googleToken) return;

      const user = await lookupUserBySub(googleToken);
      if (!user) return;

      userId = user.userId; 
      renderBadges(userId);
    }

    initDashboard();