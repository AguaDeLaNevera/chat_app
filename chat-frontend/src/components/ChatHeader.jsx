export default function ChatHeader({ username, onLogout }) {
  return (
    <header className="panel-header chat-toolbar">
      <div className="chat-meta">
        <strong>Group Chat</strong>
        <span>{username ? `Signed in as ${username}` : "Signed in"}</span>
      </div>
      <div className="button-row">
        <button className="button button-danger" onClick={onLogout}>
          Logout
        </button>
      </div>
    </header>
  );
}
