public class DriverAllocator implements TripDriverAllocator {
    public String allocate(String studentId) {
        // fake deterministic driver
        return "DRV-17";
    }
}
