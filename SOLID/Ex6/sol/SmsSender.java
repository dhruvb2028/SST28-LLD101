public class SmsSender extends NotificationSender {
    public SmsSender(AuditLog audit) { super(audit); }

    @Override
    protected void doSend(Notification n) {
        System.out.println("SMS -> to=" + n.phone + " body=" + n.body);
    }

    @Override
    protected String auditLabel() {
        return "sms sent";
    }
}
