import {useEffect, useRef, useState} from "react";
import SockJS from "sockjs-client";
import {Client} from "@stomp/stompjs";

import {getToken, graphqlRequest, parseJwt, clearToken} from "../api/chatApi";
import ChatHeader from "../components/ChatHeader";
import Composer from "../components/Composer";
import MessageList from "../components/MessageList";
import {useNavigate, Link} from "react-router-dom";

const GET_MESSAGES_QUERY = `
  query {
    messages {
      id
      userId
      username
      content
      sentAt
    }
  }
`;

const SEND_MESSAGE_QUERY = `
  mutation($content: String!) {
    sendMessage(content: $content) {
      id
      userId
      content
      sentAt
    }
  }
`;

export default function Chat() {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState("");
    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const [error, setError] = useState("");
    const messagesRef = useRef(null);

    const navigate = useNavigate();

    const remaining = 200 - text.length;

    useEffect(() => {
        const stored = getToken();
        if (!stored) {
            clearToken()
            navigate("/login", {replace: true});
            return;
        }

        try {
            setToken(stored);
            const payload = parseJwt(stored);
            setUsername(payload.preferred_username ?? "");
        } catch {
            clearToken()
            navigate("/login", {replace: true});
        }
    }, [navigate]);

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

        const controller = new AbortController();
        const client = new Client({
            webSocketFactory: () => new SockJS("/ws"),
            reconnectDelay: 0,
            debug: () => {
            },
        });

        async function loadMessages() {
            try {
                const {response, payload} = await graphqlRequest(
                    GET_MESSAGES_QUERY,
                    token,
                    {},
                    controller.signal,
                );

                if (response.status === 401) {
                    clearToken()
                    navigate("/login", {replace: true});
                    return;
                }

                if (payload.errors) {
                    setError(payload.errors[0]?.message || "Could not load messages");
                    return;
                }

                setMessages(payload.data?.messages || []);
            } catch (error) {
                if (error?.name === "AbortError") {
                    return;
                }

                setError("Could not load messages");
            }
        }

        loadMessages();

        client.onConnect = () => {
            client.subscribe("/topic/messages", (frame) => {
                const message = JSON.parse(frame.body);
                setMessages((current) => [...current, message]);
            });
        };

        client.activate();

        return () => {
            controller.abort();
            client.deactivate();
        };
    }, [token, navigate]);

    async function sendMessage() {
        try {
            const content = text.trim();
            if (!content) {
                return;
            }

            setError("");

            const {response, payload} = await graphqlRequest(
                SEND_MESSAGE_QUERY,
                token,
                {content},
            );

            if (response.status === 401) {
                clearToken();
                navigate("/login", {replace: true});
                return;
            }

            if (payload.errors) {
                setError(payload.errors[0]?.message || "Could not send message");
                return;
            }

            setText("");
        } catch {
            setError("Could not send message");
        }
    }

    return (
        <div className="page">
            <section className="panel chat-shell">
                <ChatHeader
                    username={username}
                    onLogout={() => {
                        clearToken();
                        navigate("/login", {replace: true})}
                    }
                />

                <MessageList
                    messages={messages}
                    currentUsername={username}
                    messagesRef={messagesRef}
                />

                <Composer
                    text={text}
                    remaining={remaining}
                    onChange={setText}
                    onSend={sendMessage}
                    error={error}
                />
            </section>
        </div>
    );
}
