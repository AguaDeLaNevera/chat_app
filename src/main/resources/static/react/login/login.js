import React from "react";

const { getToken, keycloakPasswordGrant, setToken } = window.ChatApi;
const { useEffect, useState } = React;
const e = React.createElement;

function AuthPanel({ title, subtitle, children }) {
  return e(
    "section",
    { className: "panel auth-shell" },
    e(
      "div",
      { className: "content auth-card" },
      e("h1", { className: "auth-title" }, title),
      e("p", { className: "auth-copy" }, subtitle),
      children,
    ),
  );
}

function AuthField({ id, label, type, value, onChange, onEnter }) {
  return e(
    "div",
    { className: "field" },
    e("label", { htmlFor: id }, label),
    e("input", {
      id,
      type,
      value,
      onChange: (event) => onChange(event.target.value),
      onKeyDown: (event) => {
        if (event.key === "Enter") {
          event.preventDefault();
          onEnter();
        }
      },
    }),
  );
}

function LoginApp() {
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

  return e(
    "div",
    { className: "page" },
    e(
      AuthPanel,
      {
        title: "Login",
        subtitle: "Use your Keycloak account to enter the chat.",
      },
      e(
        "div",
        { className: "stack" },
        e(AuthField, {
          id: "username",
          label: "Username",
          type: "text",
          value: username,
          onChange: setUsername,
          onEnter: login,
        }),
        e(AuthField, {
          id: "password",
          label: "Password",
          type: "password",
          value: password,
          onChange: setPassword,
          onEnter: login,
        }),
      ),
      e(
        "div",
        { className: "button-row" },
        e(
          "button",
          { className: "button button-primary", onClick: login },
          "Login",
        ),
        e(
          "button",
          {
            className: "button button-secondary",
            onClick: () => (window.location.href = "./register.html"),
          },
          "Register",
        ),
      ),
      e("p", { className: "error" }, error),
      e(
        "p",
        { className: "auth-links" },
        "Need an account? ",
        e("a", { href: "./register.html" }, "Register"),
      ),
    ),
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(
  e(LoginApp, null),
);
