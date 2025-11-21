//comments for gio:
//this is just responsible for contacting api mappings in springboot, main.js handles the actual buttons and populating fields
//returns json formatted db items

//register user
//calls QuizController: registerUser

//create class
//calls QuizController: createClass
export async function createClass(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/create`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });

    if (!res.ok) throw new Error("Failed to create class");

    const data = await res.json();
    return data.quizId;
  } catch (err) {
    console.error("Error creating class:", err);
    return -1;
  }
}


//calls QuizController: viewInstructorClasses
export async function getInstructorClasses(instructorId) {
  try {

    //this calls api endpoint (get instructr classes based on id)
    const res = await fetch(`/api/instructors/${instructorId}/classes`);

    //failure check
    if (!res.ok) throw new Error("Failed request");

    //return content in json format
    return await res.json();
  } catch (err) {
    console.error("Error fetching instructor classes:", err);
    return null;
  }
}

//enroll student
export async function enrollStudent(classId, email) {
  try {
    const res = await fetch(`/api/instructors/classes/enroll`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ classId, email })
    });

    return await res.json(); 
  } catch (err) {
    console.error("Error enrolling student:", err);
    return false;
  }
}

//view class enrollees
export async function getClassEnrollees(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/enrollees`);
    if (!res.ok) throw new Error("Failed request");
    return await res.json();
  } catch (err) {
    console.error("Error fetching class enrollees:", err);
    return null;
  }
}

//calls QuizController: viewStudentClasses
export async function getStudentClasses(studentId) {
  try {

    //this calls api endpoint (get instructr classes based on id)
    const res = await fetch(`/api/students/${studentId}/classes`);

    //failure check
    if (!res.ok) throw new Error("Failed request");

    //return content in json format
    return await res.json();
  } catch (err) {
    console.error("Error fetching student classes:", err);
    return null;
  }
}

//upload rading
export async function uploadReading(classId, instructorId, readingName) {
  try {
    const res = await fetch(`/api/classes/${classId}/readingupload`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ classId, instructorId, readingName })
    });

    if (!res.ok) throw new Error("Failed to upload reading");

    return await res.json(); 
  } catch (err) {
    console.error("Error uploading reading:", err);
    return null;
  }
}

//add reading objective
//assign objective to a reading
export async function addReadingObjective(readingId, classId, objectiveName) { //readingId, requestbody
  try {
    const res = await fetch(`/api/readings/${readingId}/objectives`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ classId, objectiveName })
    });

    if (!res.ok) throw new Error("Failed to add reading objective");

    return await res.json();
  } catch (err) {
    console.error("Error adding reading objective:", err);
    return null;
  }
}


//get class readings
export async function getClassReadings(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/readings`);
    if (!res.ok) throw new Error("Failed request");
    return await res.json();
  } catch (err) {
    console.error("Error fetching class readings:", err);
    return null;
  }
}

//can create quiz check
export async function canCreateQuiz(classId) {
  try {
    const res = await fetch(`/api/canCreate/${classId}`);
    
    if (!res.ok) throw new Error("Failed request");

    const data = await res.json();
    return data.canCreate;
  } catch (err) {
    console.error("Error checking if quiz can be created:", err);
    return false; 
  }
}

//create quiz
export async function createQuiz(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/quizzcreation`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });

    if (!res.ok) throw new Error("Failed to create quiz");

    const data = await res.json();
    return data.quizId;
  } catch (err) {
    console.error("Error creating quiz:", err);
    return -1;
  }
}

//add quiz reading
export async function addQuizReading(quizId, readingId) { //gets readingId from request body
  try {
    const res = await fetch(`/api/quizzes/${quizId}/readings`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ readingId })
    });

    if (!res.ok) throw new Error("Failed to add reading");

    return await res.json();
  } catch (err) {
    console.error("Error linking reading:", err);
    return null;
  }
}

//add question
export async function addQuestion(quizId, questionData) {
  try {
    const res = await fetch(`/api/quizzes/${quizId}/questions`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify(questionData)
    });

    return await res.json();
  } catch (err) {
    console.error("Error adding question:", err);
    return { status: "error" };
  }
}

//update quiz name
export async function updateQuizName(quizId, newName) {
  try {
    const res = await fetch(`/api/quizzes/${quizId}/name`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ quizName: newName })
    });

    if (!res.ok) throw new Error("Failed to update quiz name");

    return await res.json();
  } catch (err) {
    console.error("Error updating quiz name:", err);
    return { status: "error" };
  }
}

//get class quizzes
export async function getClassQuizzes(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/quizzes`);
    if (!res.ok) throw new Error("Failed request");
    return await res.json();
  } catch (err) {
    console.error("Error fetching class quizzes:", err);
    return null;
  }
}

//display objectives unique to one reading
export async function getReadingObjectives(readingId) {
  try {
    const res = await fetch(`/api/quizzes/${readingId}/objectives`);
    if (!res.ok) throw new Error("Failed request");
    return await res.json();
  } catch (err) {
    console.error("Error fetching class enrollees:", err);
    return null;
  }
}

//view objectives by quiz for students to select from
export async function getObjectivesByQuiz(quizId) {
  try {
    const res = await fetch(`/api/quizzes/${quizId}/objectives`);

    if (!res.ok) throw new Error("Failed to fetch objectives");

    return await res.json(); 
  } catch (err) {
    console.error("Error getting objectives:", err);
    return [];
  }
}



//student selects objective at beginning of quiz
export async function selectObjectiveForQuiz(quizId, studentId, objectiveId) {
  try {
    const res = await fetch(`/api/quizzes/${quizId}/objectives`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({
        studentId,
        objectiveId
      })
    });

    if (!res.ok) throw new Error("Failed to select objective");

    return await res.json(); 
  } catch (err) {
    console.error("Error selecting objective:", err);
    return { status: "error" };
  }
}

//view student objectives

//loads all questions (not just student)
//Calls QuizController: 
export async function getQuizQuestions(quizId) {
  try {
    const res = await fetch(`/api/quizzes/${quizId}/questions`);

    if (!res.ok) throw new Error("Failed to fetch quiz questions");

    return await res.json(); 
  } catch (err) {
    console.error("Error fetching quiz questions:", err);
    return [];
  }
}

//calls quiz controller: viewStudentQuestions
export async function viewStudentQuestions() {
    try {
      const res = await fetch(`/api/quizzes/student/${studentId}/${quizId}/questions?numQuestions=5`);
      if (!res.ok) throw new Error("Failed to load");

      QUESTIONS = await res.json();
      index = 0;
      score = 0;
      render();
    } catch (err) {
      console.error(err);
      alert("Could not load quiz questions.");
    }
}


//assign badge after quiz is done
export async function assignBadge(studentId) {
  try {
    const res = await fetch(`/api/students/${studentId}/badgeAssign`, {
      method: "POST",
      headers: { "Content-Type": "application/json" }
    });

    if (!res.ok) throw new Error("Failed to assign badge");

    return await res.json(); 
  } catch (err) {
    console.error("Error assigning badge:", err);
    return false;
  }
}

//view student badges

//view all badges to be earned

//remove enrollee on enrollment page
export async function removeStudent(classId, studentId) {
  try {
    const res = await fetch(`/api/classes/${classId}/enrollees/${studentId}`, {
      method: "DELETE"
    });

    if (!res.ok) throw new Error("Failed to remove student");
    return await res.json(); 
  } catch (err) {
    console.error("Error removing student:", err);
    return false;
  }
}

//search students (used for email in class enrollment)
export async function searchStudents(query) {
  try {
    const res = await fetch(`/api/students/search?type=email&query=${encodeURIComponent(query)}`);

    if (!res.ok) throw new Error("Failed search");

    return await res.json();
  } catch (err) {
    console.error("Search error:", err);
    return [];
  }
}





