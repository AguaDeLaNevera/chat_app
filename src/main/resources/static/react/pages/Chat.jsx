import { useEffect, useRef, useState } from "react";

import ChatHeader from "../components/ChatHeader";
import Composer from "../components/Composer";
import MessageList from "../components/MessageList";
import { redirectToLogin } from "../utils/navigation";

import {
    getToken,
    graphqlRequest,
    parseJwt,
} from "../api/chatApi";

export default function Chat() {
    const [token, setToken] = useState(null);
    const [username, setUsername] = useState("");
    const [messages, setMessages] = useState([]);
    const [text, setText] = useState("");
    const [error, setError] = useState("");
    const messagesRef = useRef(null);

    const remaining = 200 - text.length;

    useEffect(() => {
        const stored = getToken();
        if (!stored) {
            redirectToLogin()
            return;
        }
        try {
            setToken(stored);
            const payload = parseJwt(stored);
            setUsername(payload.preferred_username ?? "");
        } catch {
            redirectToLogin()
        }
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

        const controller = new AbortController();

        async function loadMessages() {
            try {
                const {response, payload} = await graphqlRequest(
                    GET_MESSAGES_QUERY,
                    token,
                    {},
                    controller.signal,
                );

                if (response.status === 401) {
                    redirectToLogin()
                    return;
                }

                if (payload.errors) {
                    setError(payload.errors[0]?.message || "Could not load messages");
                    return;
                }

                setMessages(payload.data?.messages || []);
            } catch (error) {
                if (error.name === "AbortError") {
                    return;
                }
                setError("Could not load messages")
            }

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
            controller.abort();
            try {
                stomp.disconnect();
            } catch {
                // no-op
            }
        };
    }, [token]);

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
                redirectToLogin()
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
                    onLogout={redirectToLogin}
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