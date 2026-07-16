import { useEffect, useState } from "react";

import AuthPanel from "../components/AuthPanel";
import AuthField from "../components/AuthField";

import {
    graphqlRequest,
    getToken
} from "../api/chatApi";

const REGISTER_MUTATION = `
  mutation($username: String!, $password: String!) {
    register(username: $username, password: $password) {
      id
      username
    }
  }
`;

export default function RegisterApp() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        if (getToken()) {
            window.location.href = "./index.html";
        }
    }, []);

    async function register() {
        setError("");

        if (!username.trim() || !password) {
            setError("Fill every field");
            return;
        }

        try {
            const { response, payload } = await graphqlRequest(
                REGISTER_MUTATION,
                null,
                {
                    username: username.trim(),
                    password,
                },
            );

            if (!response.ok || payload.errors) {
                setError(
                    (payload.errors && payload.errors[0] && payload.errors[0].message) ||
                    "Could not register account",
                );
                return;
            }

            window.location.href = "./login.html";
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
                    <button
                        className="button button-primary"
                        onClick={register}
                    >
                        Register
                    </button>
                    <button
                        className="button button-secondary"
                        onClick={() => (window.location.href = "./login.html")}
                    >
                        Login
                    </button>
                </div>

                <p className="error">
                    {error}
                </p>

                <p className="auth-links">
                    Already have an account?
                    <a href="./login.html">
                        Login
                    </a>
                </p>
            </AuthPanel>
        </div>
    );
}