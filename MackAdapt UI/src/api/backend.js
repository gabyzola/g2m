//register user
//calls QuizController: registerUser

//create class
//tested:
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

//lookup user by email
//tested: 
export async function lookupUser(email) {
  try {
    console.log("[DEBUG] lookupUser called with email:", email);

    const res = await fetch(`/api/lookup`, { 
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email })
    });

    console.log("[DEBUG] fetch completed. Status:", res.status, "StatusText:", res.statusText);

    if (!res.ok) {
      // Try to get server error message
      let text;
      try { text = await res.text(); } catch {}
      console.error("[DEBUG] Server returned non-OK response:", text);
      throw new Error(`Failed to lookup user, status ${res.status}`);
    }

    const resData = await res.json();
    console.log("[DEBUG] Response JSON:", resData);

    if (typeof resData.userId === "undefined") {
      console.error("[DEBUG] userId missing in response!");
      return -1;
    }

    return resData.userId;
  } catch (err) {
    console.error("[DEBUG] Error looking up user:", err);
    return -1;
  }
}


//lookup user by sub
//tested:
export async function lookupUserBySub(googleSub) {
  try {
    const res = await fetch("/api/lookup/sub", {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ googleSub })
    });
 
    if (!res.ok) throw new Error("Failed to lookup user by sub");

    const resData = await res.json(); // parse JSON
    return resData;                    // return the whole object {userId: ..., maybe more}
  } catch (err) {
    console.error("Error looking up user by sub:", err);
    return null;
  }
}

//gets instructor classes
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
//tested:
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
    const res = await fetch(`/api/students/${studentId}/classes`);
    if (!res.ok) throw new Error("Failed request");

    //return content in json format
    return await res.json();
  } catch (err) {
    console.error("Error fetching student classes:", err);
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

//determines if user is able to create a quiz
export async function canCreateQuiz(classId, userId) {
  try {
    const res = await fetch(`/api/canCreate/${classId}?userId=${userId}`);
    if (!res.ok) throw new Error("Failed request");

    const data = await res.json();
    return data.canCreate;
  } catch (err) {
    console.error("Error checking if quiz can be created:", err);
    return false; 
  }
}

//gets whether they are student or isntructor
export async function isUserInstructor(userId) {
  try {
    const res = await fetch(`/api/role/${userId}`);
    if (!res.ok) throw new Error("Failed to fetch user role");

    const data = await res.json();
    return data.isInstructor;
  } catch (err) {
    console.error("Error fetching user role:", err);
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

//link reading to quiz
export async function addReadingToQuiz(quizId, readingId) {
  const response = await fetch(`/api/quiz/${quizId}/addReading`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ readingId }),
  });

  return response.json();
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

//get student questions based on chosen objective
export async function getStudentQuestions(studentId, quizId) {
  try {
    const res = await fetch(
      `/api/quizzes/student/${studentId}/${quizId}/questions`
    );

    if (!res.ok) throw new Error("Failed to fetch student quiz questions");

    return await res.json();  //returns List<QuizQuestion>
  } catch (err) {
    console.error("Error fetching student questions:", err);
    return [];
  }
}

//get all quiz questions regardless of objectives
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

//populates ui with class name
export async function getClassName(classId) {
  try {
    const res = await fetch(`/api/classes/${classId}/name`);

    if (!res.ok) throw new Error("Failed request");

    return await res.json();
  } catch (err) {
    console.error("Error fetching class name:", err);
    return null;
  }
}

export async function getQuizObjectives(quizId) {
  const response = await fetch(`/api/quizzes/${quizId}/objectives`, {
    method: "GET",
    headers: { "Content-Type": "application/json" },
  });

  return response.json();
}

// Begin session
export async function startAttemptSession(studentId, quizId, objectiveId) {
  try {
    const res = await fetch(`/api/attempt/start`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ studentId, quizId, objectiveId }),
    });

    if (!res.ok) throw new Error("Failed to start attempt session");

    const data = await res.json(); 
    return data.sessionId;          
  } catch (err) {
    console.error("Error starting attempt session:", err);
    return -1;
  }
}

// Save each answer
export async function saveStudentAnswer(sessionId, questionId, chosenChoiceId) {
  try {
    const res = await fetch(`/api/attempt/answer`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ sessionId, questionId, chosenChoiceId }),
    });

    if (!res.ok) throw new Error("Failed to save student answer");

    const data = await res.json(); 
    return data.success ?? data;    
  } catch (err) {
    console.error("Error saving student answer:", err);
    return false;
  }
}

// End session
export async function finalizeAttemptSession(sessionId) {
  try {
    const res = await fetch(`/api/attempt/finish/${sessionId}`, {
      method: "POST",
    });

    if (!res.ok) throw new Error("Failed to finalize attempt session");

    const data = await res.json(); 
    return data.success ?? data;    //ensures boolean
  } catch (err) {
    console.error("Error finalizing attempt session:", err);
    return false;
  }
}

// Get attempt results
export async function getAttemptResults(sessionId) {
  try {
    const res = await fetch(`/api/attempt/${sessionId}/results`);

    if (!res.ok) throw new Error("Failed to fetch attempt results");

    return await res.json(); // returns result object
  } catch (err) {
    console.error("Error fetching attempt results:", err);
    return {};
  }
}

//get the latest sessionId for a student
export async function getLatestSessionId(studentId) {
  try {
    const res = await fetch(`/api/attempt/latest/${studentId}`);
    if (!res.ok) throw new Error("Failed request");

    const sessionId = await res.json();
    return sessionId; 
  } catch (err) {
    console.error("Error fetching latest sessionId:", err);
    return null;
  }
}

export async function assignBadge(studentId) {
  try {
    const res = await fetch(`/api/students/${studentId}/badgeAssign`, {
      method: "POST"
    });
    if (!res.ok) throw new Error("Failed badge assign");
    return await res.text(); 
  } catch (err) {
    console.error("Error assigning badge:", err);
    return null;
  }
}


