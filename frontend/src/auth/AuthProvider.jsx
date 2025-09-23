import { MsalProvider } from "@azure/msal-react";
import { msalInstance } from "./msalConfig";

export default function AuthProvider({ children }) {
  return <MsalProvider instance={msalInstance}>{children}</MsalProvider>;
}
