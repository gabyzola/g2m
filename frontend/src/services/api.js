import axios from "axios";

// Create axios instance
const api = axios.create({
  baseURL: "http://localhost:5000/api", // backend API base
});

// Attach Microsoft SSO token to requests
api.interceptors.request.use(async (config) => {
  const account = window.msalInstance.getAllAccounts()[0];
  if (account) {
    const response = await window.msalInstance.acquireTokenSilent({
      scopes: ["api://your-app-id/.default"], // replace with your API scope
      account,
    });
    config.headers.Authorization = `Bearer ${response.accessToken}`;
  }
  return config;
});

export default api;
