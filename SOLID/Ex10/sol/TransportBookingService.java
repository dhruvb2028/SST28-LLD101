public class TransportBookingService {
    private final TripDistanceService distanceService;
    private final TripDriverAllocator driverAllocator;
    private final TripPaymentGateway paymentGateway;
    private final FarePolicy farePolicy;

    public TransportBookingService(
        TripDistanceService distanceService,
        TripDriverAllocator driverAllocator,
        TripPaymentGateway paymentGateway,
        FarePolicy farePolicy
    ) {
        this.distanceService = distanceService;
        this.driverAllocator = driverAllocator;
        this.paymentGateway = paymentGateway;
        this.farePolicy = farePolicy;
    }

    public void book(TripRequest req) {
        double km = distanceService.km(req.from, req.to);
        System.out.println("DistanceKm=" + km);

        String driver = driverAllocator.allocate(req.studentId);
        System.out.println("Driver=" + driver);

        double fare = farePolicy.fareFor(km);

        String txn = paymentGateway.charge(req.studentId, fare);
        System.out.println("Payment=PAID txn=" + txn);

        BookingReceipt r = new BookingReceipt("R-501", fare);
        System.out.println("RECEIPT: " + r.id + " | fare=" + String.format("%.2f", r.fare));
    }
}
