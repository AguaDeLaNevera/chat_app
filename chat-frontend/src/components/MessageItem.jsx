export default function MessageItem({ message, isOwn }) {
  return (
    <article className={`message ${isOwn ? "me" : ""}`}>
      <div className="message-user">{message.username}</div>
      <div className="message-text">{message.content}</div>
      {message.sentAt ? (
        <div className="message-time">
          {new Date(message.sentAt).toLocaleString()}
        </div>
      ) : null}
    </article>
  );
}
