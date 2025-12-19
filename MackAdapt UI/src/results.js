import { getClassEnrollees, getAttemptResults, getLatestSessionId } from "./backend";
import "./style.css";
    const params = new URLSearchParams(window.location.search);
    const classId = params.get("classId");

    const studentSelect = document.getElementById('studentSelect');
    const resultsContainer = document.getElementById('resultsContainer');
    const resultsTableBody = document.querySelector('#resultsTable tbody');
    const downloadBtn = document.getElementById('downloadBtn');
    const downloadClassBtn = document.getElementById('downloadClassBtn'); //adding a new button to download the whole class

    async function downloadEntireClassCSV(enrollees) {
        let csvContent =
            "Student Name,Question ID,Question Text,Chosen Letter,Correct Letter,Points Earned\n";

        for (const student of enrollees) {
            const sessionId = await getLatestSessionId(student.studentId, classId);
            if (!sessionId) continue;

            const attempt = await getAttemptResults(sessionId);
            if (!attempt || !attempt.answers) continue;

            attempt.answers.forEach(ans => {
                csvContent += `"${student.firstName} ${student.lastName}",` +
                    `${ans.questionId},` +
                    `"${ans.questionText}",` +
                    `${ans.chosenLetter},` +
                    `${ans.correctLetter ?? "-"},` +
                    `${ans.pointsEarned}\n`;
            });
        }

        const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);
        link.download = `class_${classId}_results.csv`;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    async function renderAttemptResults(studentId) {
        //gets students latest session id, must be called bc we need session id to get results, not student id
        const sessionId = await getLatestSessionId(studentId, classId);


        if (!sessionId) {
            //student has never completed a quiz
            resultsContainer.classList.add('hidden');
            downloadBtn.classList.add('hidden');
            resultsTableBody.innerHTML = '';
            return;
        }

        //fetch attempt results using sessionId
        const attempt = await getAttemptResults(sessionId);

        if (!attempt) {
            resultsContainer.classList.add('hidden');
            downloadBtn.classList.add('hidden');
            return;
        }

        resultsContainer.classList.remove('hidden');
        downloadBtn.classList.remove('hidden');
        downloadBtn.onclick = () => downloadCSV(attempt);
        resultsTableBody.innerHTML = '';

        const answers = attempt.answers ?? [];

        answers.forEach(ans => {
            const tr = document.createElement('tr');
            tr.className = ans.pointsEarned > 0 ? 'correct' : 'incorrect';
            tr.innerHTML = `
                <td>${ans.questionId}</td>
                <td>${ans.questionText}</td>
                <td>${ans.chosenLetter}</td>
                <td>${ans.correctLetter ?? '-'}</td>
                <td>${ans.pointsEarned}</td>
            `;
            resultsTableBody.appendChild(tr);
        });
    }

    function downloadCSV(attempt) {
        const answers = attempt.answers ?? [];
        let csvContent = "Question ID,Question Text,Chosen Letter,Correct Letter,Points Earned\n";

        answers.forEach(ans => {
            csvContent += `${ans.questionId},"${ans.questionText}",${ans.chosenLetter},${ans.correctLetter ?? "-"},${ans.pointsEarned}\n`;
        });

        const blob = new Blob([csvContent], { type: "text/csv;charset=utf-8;" });
        const link = document.createElement("a");
        link.href = URL.createObjectURL(blob);

        const filename = `attempt_results_session_${attempt.sessionId}.csv`;
        link.download = filename;

        document.body.appendChild(link);
        link.click();
        document.body.removeChild(link);
    }

    async function init() {
        const enrollees = await getClassEnrollees(classId);

    enrollees.forEach(student => {
        const option = document.createElement('option');
        option.value = student.studentId;
        option.textContent = `${student.firstName} ${student.lastName}`;
        studentSelect.appendChild(option);
    });

    studentSelect.addEventListener('change', async () => {
        const studentId = studentSelect.value;
        if (!studentId) return;
        await renderAttemptResults(studentId);
    });

    downloadClassBtn.onclick = () => downloadEntireClassCSV(enrollees);
    }

    init();