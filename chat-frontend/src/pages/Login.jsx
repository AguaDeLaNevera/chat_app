import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

import { getToken, keycloakPasswordGrant, setToken} from "../api/chatApi";
import AuthField from "../components/AuthField";
import AuthPanel from "../components/AuthPanel";

export default function Login() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (getToken()) {
      navigate("/chat", { replace: true });
    }
  }, [navigate]);

  async function login() {
    setError("");

    if (!username.trim() || !password) {
      setError("Fill every field");
      return;
    }

    try {
      const { response, payload } = await keycloakPasswordGrant(
        username.trim(),
        password,
      );

      if (!response.ok) {
        setError(payload.error_description || "Wrong username or password");
        return;
      }

      setToken(payload.access_token);
      navigate("/chat", { replace: true });
    } catch {
      setError("Cannot connect to server");
    }
  }

  return (
    <div className="page">
      <AuthPanel
        title="Login"
        subtitle="Use your Keycloak account to enter the chat."
      >
        <div className="stack">
          <AuthField
            id="username"
            label="Username"
            type="text"
            value={username}
            onChange={setUsername}
            onEnter={login}
          />
          <AuthField
            id="password"
            label="Password"
            type="password"
            value={password}
            onChange={setPassword}
            onEnter={login}
          />
        </div>
        <div className="button-row">
          <button className="button button-primary" onClick={login}>
            Login
          </button>
          <button
            className="button button-secondary"
            onClick={() => navigate("/register")}
          >
            Register
          </button>
        </div>
        <p className="error">{error}</p>
        <p className="auth-links">
          Need an account?
          <Link to="/register">Register</Link>
        </p>
      </AuthPanel>
    </div>
  );
}
