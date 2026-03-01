import java.util.*;

public class HostelFeeCalculator {
    private final FakeBookingRepo repo;
    private final RoomPricingPolicy roomPricingPolicy;
    private final AddOnPricingPolicy addOnPricingPolicy;
    private final BookingReceiptPrinter receiptPrinter;
    private final BookingIdGenerator bookingIdGenerator;

    public HostelFeeCalculator(FakeBookingRepo repo) {
        this(repo, new DefaultRoomPricingPolicy(), new DefaultAddOnPricingPolicy(), new ConsoleBookingReceiptPrinter(), new DeterministicBookingIdGenerator());
    }

    public HostelFeeCalculator(FakeBookingRepo repo,
                               RoomPricingPolicy roomPricingPolicy,
                               AddOnPricingPolicy addOnPricingPolicy,
                               BookingReceiptPrinter receiptPrinter,
                               BookingIdGenerator bookingIdGenerator) {
        this.repo = repo;
        this.roomPricingPolicy = roomPricingPolicy;
        this.addOnPricingPolicy = addOnPricingPolicy;
        this.receiptPrinter = receiptPrinter;
        this.bookingIdGenerator = bookingIdGenerator;
    }

    public void process(BookingRequest req) {
        Money monthly = calculateMonthly(req);
        Money deposit = new Money(5000.00);

        receiptPrinter.print(req, monthly, deposit);

        String bookingId = bookingIdGenerator.nextBookingId();
        repo.save(bookingId, req, monthly, deposit);
    }

    private Money calculateMonthly(BookingRequest req) {
        Money roomBase = roomPricingPolicy.monthlyBaseFor(req.roomType);
        Money addOnTotal = addOnPricingPolicy.totalFor(req.addOns);
        return roomBase.plus(addOnTotal);
    }
}

interface RoomPricingPolicy {
    Money monthlyBaseFor(int roomType);
}

interface AddOnPricingPolicy {
    Money totalFor(List<AddOn> addOns);
}

interface BookingReceiptPrinter {
    void print(BookingRequest req, Money monthly, Money deposit);
}

interface BookingIdGenerator {
    String nextBookingId();
}

class DefaultRoomPricingPolicy implements RoomPricingPolicy {
    private final Map<Integer, Money> pricing = Map.of(
            LegacyRoomTypes.SINGLE, new Money(14000.0),
            LegacyRoomTypes.DOUBLE, new Money(15000.0),
            LegacyRoomTypes.TRIPLE, new Money(12000.0),
            LegacyRoomTypes.DELUXE, new Money(16000.0)
    );

    @Override
    public Money monthlyBaseFor(int roomType) {
        return pricing.getOrDefault(roomType, new Money(16000.0));
    }
}

class DefaultAddOnPricingPolicy implements AddOnPricingPolicy {
    private final Map<AddOn, Money> addOnRates = Map.of(
            AddOn.MESS, new Money(1000.0),
            AddOn.LAUNDRY, new Money(500.0),
            AddOn.GYM, new Money(300.0)
    );

    @Override
    public Money totalFor(List<AddOn> addOns) {
        Money total = new Money(0.0);
        for (AddOn addOn : addOns) {
            total = total.plus(addOnRates.getOrDefault(addOn, new Money(0.0)));
        }
        return total;
    }
}

class ConsoleBookingReceiptPrinter implements BookingReceiptPrinter {
    @Override
    public void print(BookingRequest req, Money monthly, Money deposit) {
        ReceiptPrinter.print(req, monthly, deposit);
    }
}

class DeterministicBookingIdGenerator implements BookingIdGenerator {
    @Override
    public String nextBookingId() {
        return "H-" + (7000 + new Random(1).nextInt(1000));
    }
}
