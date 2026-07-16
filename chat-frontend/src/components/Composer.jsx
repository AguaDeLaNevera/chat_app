export default function Composer({ text, remaining, onChange, onSend, error }) {
  return (
    <footer className="composer">
      <div className="field">
        <label htmlFor="message-input">Message</label>
        <textarea
          id="message-input"
          value={text}
          maxLength={200}
          placeholder="Write a message..."
          onChange={(event) => onChange(event.target.value)}
          onKeyDown={(event) => {
            if (event.key === "Enter" && !event.shiftKey) {
              event.preventDefault();
              onSend();
            }
          }}
        />
        {error ? <p className="error">{error}</p> : null}
      </div>
      <div className="composer-actions">
        <button className="button button-primary" onClick={onSend}>
          Send
        </button>
        <div className="counter">{`${Math.max(0, remaining)} / 200`}</div>
      </div>
    </footer>
  );
}
