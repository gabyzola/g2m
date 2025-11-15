export async function getInstructorClasses(instructorId) {
  try {
    const res = await fetch(`/api/instructors/${instructorId}/classes`);

    if (!res.ok) throw new Error("Failed request");

    return await res.json();
  } catch (err) {
    console.error("Error fetching instructor classes:", err);
    return null;
  }
}
