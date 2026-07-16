import { useEffect, useState } from "react";
import { useNavigate, Link } from "react-router-dom";

import { getToken, graphqlRequest } from "../api/chatApi";
import AuthField from "../components/AuthField";
import AuthPanel from "../components/AuthPanel";

const REGISTER_MUTATION = `
  mutation($username: String!, $password: String!) {
    register(username: $username, password: $password) {
      id
      username
    }
  }
`;

export default function Register() {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");

  useEffect(() => {
    if (getToken()) {
      navigate("/chat", { replace: true });
    }
  }, [navigate]);

  async function register() {
    setError("");

    if (!username.trim() || !password) {
      setError("Fill every field");
      return;
    }

    try {
      const { response, payload } = await graphqlRequest(REGISTER_MUTATION, null, {
        username: username.trim(),
        password,
      });

      if (!response.ok || payload.errors) {
        setError(
          (payload.errors && payload.errors[0] && payload.errors[0].message) ||
            "Could not register account",
        );
        return;
      }

      navigate("/chat", { replace: true });
    } catch {
      setError("Could not connect to server");
    }
  }

  return (
    <div className="page">
      <AuthPanel
        title="Create account"
        subtitle="Create the account in GraphQL and Keycloak together."
      >
        <div className="stack">
          <AuthField
            id="username"
            label="Username"
            type="text"
            value={username}
            onChange={setUsername}
            onEnter={register}
          />

          <AuthField
            id="password"
            label="Password"
            type="password"
            value={password}
            onChange={setPassword}
            onEnter={register}
          />
        </div>

        <div className="button-row">
          <button className="button button-primary" onClick={register}>
            Register
          </button>
          <button
            className="button button-secondary"
            onClick={() => navigate("/chat", { replace: true })}
          >
            Login
          </button>
        </div>

        <p className="error">{error}</p>

        <p className="auth-links">
          Already have an account?
          <Link to="/login">Login</Link>
        </p>
      </AuthPanel>
    </div>
  );
}
