//comments for gio:
//this is just responsible for contacting api mappings in springboot, main.js handles the actual buttons and populating fields
//returns json formatted db items

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

//CHECK ON THIS
export async function searchStudents(query) {
  try {
    // backend only supports type=email or name or id
    const res = await fetch(`/api/students/search?type=email&query=${encodeURIComponent(query)}`);

    if (!res.ok) throw new Error("Failed search");

    return await res.json();
  } catch (err) {
    console.error("Search error:", err);
    return [];
  }
}
