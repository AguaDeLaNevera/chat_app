export default function AuthPanel({ title, subtitle, children }) {
    return(
        <section className="panel auth-shell">
            <div className="content auth-card">
                <h1 className="auth-title">{title}</h1>
                <p className="auth-copy">{subtitle}</p>
                {children}
            </div>
        </section>
    )
}