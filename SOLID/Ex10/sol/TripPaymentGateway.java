public interface TripPaymentGateway {
    String charge(String studentId, double amount);
}
