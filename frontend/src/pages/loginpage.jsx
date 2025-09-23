import { useMsal } from "@azure/msal-react";

export default function LoginPage() {
  const { instance } = useMsal();

  const handleLogin = () => {
    instance.loginPopup({ scopes: ["User.Read"] });
  };

  return (
    <div>
      <h2>Welcome to Adaptive Quiz</h2>
      <button onClick={handleLogin}>Sign in with Microsoft</button>
    </div>
  );
}
