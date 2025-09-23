import React from "react";
import ReactDOM from "react-dom/client";
import App from "./app";
import AuthProvider from "./auth/AuthProvider"; 
import "./styles/main.css";

ReactDOM.createRoot(document.getElementById("root")).render(
  <React.StrictMode>
    <AuthProvider>
      <App />
    </AuthProvider>
  </React.StrictMode>
);
