public abstract class NotificationSender {
    protected final AuditLog audit;
    protected NotificationSender(AuditLog audit) { this.audit = audit; }

    public final void send(Notification n) {
        Notification normalized = normalize(n);
        validate(normalized);
        doSend(normalized);
        audit.add(auditLabel());
    }

    private Notification normalize(Notification n) {
        if (n == null) return new Notification("", "", "", "");
        String subject = n.subject == null ? "" : n.subject;
        String body = n.body == null ? "" : n.body;
        String email = n.email == null ? "" : n.email;
        String phone = n.phone == null ? "" : n.phone;
        return new Notification(subject, body, email, phone);
    }

    protected void validate(Notification n) {
    }

    protected abstract void doSend(Notification n);

    protected abstract String auditLabel();
}
