import { useEffect, useState } from "react";
import {
    getToken,
    keycloakPasswordGrant,
    setToken,
} from "../api/chatApi";

import AuthField from "../components/AuthField";
import AuthPanel from "../components/AuthPanel";

export default function Login() {
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [error, setError] = useState("");

    useEffect(() => {
        if (getToken()) {
            window.location.href = "./index.html";
        }
    }, []);

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
            window.location.href = "./index.html";
        } catch {
            setError("Cannot connect to server");
        }
    }

    return(
        <div className="page">
            <AuthPanel
                title="Login"
                subtitle="Use your Keycloak account to enter the chat.">
                <div className="stack">
                    <AuthField
                        id="username"
                        label="Username"
                        type="text"
                        value={username}
                        onChange={setUsername}
                        onEnter={login}>
                    </AuthField>
                    <AuthField
                        id="password"
                        label="Password"
                        type="password"
                        value={password}
                        onChange={setPassword}
                        onEnter={login}>
                    </AuthField>
                </div>
                <div className="button-row">
                    <button
                        className="button button-primary"
                        onClick={login}
                    >
                        Login
                    </button>
                    <button
                        className="button button-secondary"
                        onClick={() => (window.location.href = "./register.html")}
                    >
                        Register
                    </button>
                </div>
                <p className="error"
                >
                    {error}
                </p>
                <p className="auth-links"
                >
                    Need an account?
                    <a href="./register.html"
                    >
                        Register
                    </a>
                </p>
            </AuthPanel>
        </div>
    )
}