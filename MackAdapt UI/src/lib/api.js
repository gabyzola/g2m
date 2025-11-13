const API_BASE_URL = import.meta.env.VITE_API_BASE_URL?.replace(/\/$/, '') || '';

async function request(path, { method = 'GET', body, headers = {} } = {}) {
  if (!API_BASE_URL) {
    throw new Error('Missing VITE_API_BASE_URL. Copy .env.example and update the value.');
  }

  const options = {
    method,
    headers: {
      'Content-Type': 'application/json',
      ...headers,
    },
    body: body ? JSON.stringify(body) : undefined,
  };

  const response = await fetch(`${API_BASE_URL}${path}`, options);
  if (!response.ok) {
    const message = await response.text();
    throw new Error(message || `Request failed with ${response.status}`);
  }

  if (response.status === 204) return null;
  const text = await response.text();
  return text ? JSON.parse(text) : null;
}

export const api = {
  registerUser(payload) {
    return request('/users/register', { method: 'POST', body: payload });
  },
  createClass(payload) {
    return request('/classes/create', { method: 'POST', body: payload });
  },
  getInstructorClasses(instructorId) {
    return request(`/instructors/${instructorId}/classes`);
  },
  getStudentClasses(studentId) {
    return request(`/students/${studentId}/classes`);
  },
  getClassEnrollees(classId) {
    return request(`/classes/${classId}/enrollees`);
  },
  enrollStudent(data) {
    return request('/instructors/classes/enroll', { method: 'POST', body: data });
  },
  getClassQuizzes(classId) {
    return request(`/classes/${classId}/quizzes`);
  },
  uploadReading(classId, payload) {
    return request(`/classes/${classId}/readings`, { method: 'POST', body: payload });
  },
};

export function withFallback(promise, fallback) {
  return promise.catch((err) => {
    console.warn('[api]', err.message);
    return fallback;
  });
}
