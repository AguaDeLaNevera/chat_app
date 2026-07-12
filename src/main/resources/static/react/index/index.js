const { clearToken, getToken, graphqlRequest, parseJwt } = window.ChatApi;
const { useEffect, useMemo, useRef, useState } = React;
const e = React.createElement;

function MessageItem({ message, isOwn }) {
  return e(
    "article",
    { className: `message ${isOwn ? "me" : ""}` },
    e("div", { className: "message-user" }, message.username),
    e("div", { className: "message-text" }, message.content),
    message.sentAt
      ? e(
          "div",
          { className: "message-time" },
          new Date(message.sentAt).toLocaleString(),
        )
      : null,
  );
}

function MessageList({ messages, currentUsername, messagesRef }) {
  return e(
    "main",
    { className: "messages", ref: messagesRef },
    ...messages.map((message) =>
      e(MessageItem, {
        key: `${message.id}-${message.sentAt || ""}`,
        message,
        isOwn: message.username === currentUsername,
      }),
    ),
  );
}

function Composer({ text, remaining, onChange, onSend, error }) {
  return e(
    "footer",
    { className: "composer" },
    e(
      "div",
      { className: "field" },
      e("label", { htmlFor: "message-input" }, "Message"),
      e("textarea", {
        id: "message-input",
        value: text,
        maxLength: 200,
        placeholder: "Write a message...",
        onChange: (event) => onChange(event.target.value),
        onKeyDown: (event) => {
          if (event.key === "Enter" && !event.shiftKey) {
            event.preventDefault();
            onSend();
          }
        },
      }),
      error ? e("p", { className: "error" }, error) : null,
    ),
    e(
      "div",
      { className: "composer-actions" },
      e(
        "button",
        { className: "button button-primary", onClick: onSend },
        "Send",
      ),
      e("div", { className: "counter" }, `${Math.max(0, remaining)} / 200`),
    ),
  );
}

function ChatHeader({ username, onLogout }) {
  return e(
    "header",
    { className: "panel-header chat-toolbar" },
    e(
      "div",
      { className: "chat-meta" },
      e("strong", null, "Group Chat"),
      e("span", null, username ? `Signed in as ${username}` : "Signed in"),
    ),
    e(
      "div",
      { className: "button-row" },
      e(
        "button",
        { className: "button button-danger", onClick: onLogout },
        "Logout",
      ),
    ),
  );
}

function ChatApp() {
  const [token, setToken] = useState(null);
  const [username, setUsername] = useState("");
  const [messages, setMessages] = useState([]);
  const [text, setText] = useState("");
  const [error, setError] = useState("");
  const messagesRef = useRef(null);

  const remaining = useMemo(() => 200 - text.length, [text]);

  useEffect(() => {
    const stored = getToken();
    if (!stored) {
      window.location.href = "./login.html";
      return;
    }

    setToken(stored);
    const payload = parseJwt(stored);
    setUsername(payload.preferred_username || "");
  }, []);

  useEffect(() => {
    const node = messagesRef.current;
    if (node) {
      node.scrollTop = node.scrollHeight;
    }
  }, [messages]);

  useEffect(() => {
    if (!token) {
      return;
    }

    let active = true;

    async function loadMessages() {
      const { response, payload } = await graphqlRequest(
        `
          query {
            messages {
              id
              userId
              username
              content
            }
          }
        `,
        token,
      );

      if (!active) {
        return;
      }

      if (response.status === 401) {
        clearToken();
        window.location.href = "./login.html";
        return;
      }

      if (payload.errors) {
        setError(payload.errors[0]?.message || "Could not load messages");
        return;
      }

      setMessages(payload.data?.messages || []);
      setTimeout(() => {
        const node = messagesRef.current;
        if (node) {
          node.scrollTop = node.scrollHeight;
        }
      }, 0);
    }

    loadMessages();

    const socket = new SockJS("/ws");
    const stomp = Stomp.over(socket);
    stomp.debug = null;

    stomp.connect({}, () => {
      stomp.subscribe("/topic/messages", (frame) => {
        const message = JSON.parse(frame.body);
        setMessages((current) => [...current, message]);
      });
    });

    return () => {
      active = false;
      try {
        stomp.disconnect();
      } catch {
        // no-op
      }
    };
  }, [token]);

  async function sendMessage() {
    const content = text.trim();
    if (!content) {
      return;
    }

    setText("");
    setError("");

    const { response, payload } = await graphqlRequest(
      `
        mutation($content: String!) {
          sendMessage(content: $content) {
            id
            userId
            content
            sentAt
          }
        }
      `,
      token,
      { content },
    );

    if (response.status === 401) {
      clearToken();
      window.location.href = "./login.html";
      return;
    }

    if (payload.errors) {
      setError(payload.errors[0]?.message || "Could not send message");
    }
  }

  function logout() {
    clearToken();
    window.location.href = "./login.html";
  }

  return e(
    "div",
    { className: "page" },
    e(
      "section",
      { className: "panel chat-shell" },
      e(ChatHeader, {
        username,
        onLogout: logout,
      }),
      e(MessageList, {
        messages,
        currentUsername: username,
        messagesRef,
      }),
      e(Composer, {
        text,
        remaining,
        onChange: setText,
        onSend: sendMessage,
        error,
      }),
    ),
  );
}

ReactDOM.createRoot(document.getElementById("root")).render(
  e(ChatApp, null),
);
