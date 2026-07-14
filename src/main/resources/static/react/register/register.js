const { graphqlRequest, getToken } = window.ChatApi;
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

function RegisterApp() {
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
        `
          mutation($username: String!, $password: String!) {
            register(username: $username, password: $password) {
              id
              username
            }
          }
        `,
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

  return e(
    "div",
    { className: "page" },
    e(
      AuthPanel,
      {
        title: "Create account",
        subtitle: "Create the account in GraphQL and Keycloak together.",
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
          onEnter: register,
        }),
        e(AuthField, {
          id: "password",
          label: "Password",
          type: "password",
          value: password,
          onChange: setPassword,
          onEnter: register,
        }),
      ),
      e(
        "div",
        { className: "button-row" },
        e(
          "button",
          { className: "button button-primary", onClick: register },
          "Register",
        ),
        e(
          "button",
          {
            className: "button button-secondary",
            onClick: () => (window.location.href = "./login.html"),
          },
          "Login",
        ),
      ),
      e("p", { className: "error" }, error),
      e(
        "p",
        { className: "auth-links" },
        "Already have an account? ",
        e("a", { href: "./login.html" }, "Login"),
      ),
    ),
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(
  e(RegisterApp, null),
);
