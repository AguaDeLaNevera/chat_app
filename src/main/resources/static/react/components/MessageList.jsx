export default function MessageList({messages, currentUsername, messagesRef}) {
    return(
        <main className="messages" ref={messagesRef}>
            {messages.map((message) =>
                <MessageItem
                    key={`${message.id}-${message.sentAt || ""}`}
                    message={message}
                    isOwn={message.username === currentUsername}
                />
            )}
        </main>
    )
}