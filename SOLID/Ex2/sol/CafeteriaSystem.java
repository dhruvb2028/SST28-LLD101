import java.util.*;

public class CafeteriaSystem {
    private final Map<String, MenuItem> menu = new LinkedHashMap<>();
    private final InvoiceStore store;
    private final TaxPolicy taxPolicy;
    private final DiscountPolicy discountPolicy;
    private final InvoiceRenderer renderer;
    private int invoiceSeq = 1000;

    public CafeteriaSystem() {
        this(new InMemoryInvoiceStore(), new DefaultTaxPolicy(), new DefaultDiscountPolicy(), new DefaultInvoiceRenderer());
    }

    public CafeteriaSystem(InvoiceStore store, TaxPolicy taxPolicy, DiscountPolicy discountPolicy, InvoiceRenderer renderer) {
        this.store = store;
        this.taxPolicy = taxPolicy;
        this.discountPolicy = discountPolicy;
        this.renderer = renderer;
    }

    public void addToMenu(MenuItem i) { menu.put(i.id, i); }

    public void checkout(String customerType, List<OrderLine> lines) {
        String invId = "INV-" + (++invoiceSeq);
        List<InvoiceLine> invoiceLines = new ArrayList<>();
        for (OrderLine l : lines) {
            MenuItem item = menu.get(l.itemId);
            invoiceLines.add(new InvoiceLine(item.name, l.qty, item.price * l.qty));
        }

        BillSummary summary = BillCalculator.calculate(customerType, invoiceLines, taxPolicy, discountPolicy);
        String printable = renderer.render(invId, invoiceLines, summary);
        System.out.print(printable);

        store.save(invId, printable);
        System.out.println("Saved invoice: " + invId + " (lines=" + store.countLines(invId) + ")");
    }
}

class InvoiceLine {
    public final String itemName;
    public final int qty;
    public final double lineTotal;

    public InvoiceLine(String itemName, int qty, double lineTotal) {
        this.itemName = itemName;
        this.qty = qty;
        this.lineTotal = lineTotal;
    }
}

class BillSummary {
    public final double subtotal;
    public final double taxPercent;
    public final double tax;
    public final double discount;
    public final double total;

    public BillSummary(double subtotal, double taxPercent, double tax, double discount, double total) {
        this.subtotal = subtotal;
        this.taxPercent = taxPercent;
        this.tax = tax;
        this.discount = discount;
        this.total = total;
    }
}

class BillCalculator {
    public static BillSummary calculate(String customerType, List<InvoiceLine> lines, TaxPolicy taxPolicy, DiscountPolicy discountPolicy) {
        double subtotal = 0.0;
        for (InvoiceLine line : lines) subtotal += line.lineTotal;

        double taxPercent = taxPolicy.taxPercent(customerType);
        double tax = subtotal * (taxPercent / 100.0);
        double discount = discountPolicy.discountAmount(customerType, subtotal, lines.size());
        double total = subtotal + tax - discount;
        return new BillSummary(subtotal, taxPercent, tax, discount, total);
    }
}

interface TaxPolicy {
    double taxPercent(String customerType);
}

interface DiscountPolicy {
    double discountAmount(String customerType, double subtotal, int distinctLines);
}

interface InvoiceRenderer {
    String render(String invoiceId, List<InvoiceLine> lines, BillSummary summary);
}

interface InvoiceStore {
    void save(String name, String content);
    int countLines(String name);
}

class DefaultTaxPolicy implements TaxPolicy {
    @Override
    public double taxPercent(String customerType) {
        return TaxRules.taxPercent(customerType);
    }
}

class DefaultDiscountPolicy implements DiscountPolicy {
    @Override
    public double discountAmount(String customerType, double subtotal, int distinctLines) {
        return DiscountRules.discountAmount(customerType, subtotal, distinctLines);
    }
}

class DefaultInvoiceRenderer implements InvoiceRenderer {
    @Override
    public String render(String invoiceId, List<InvoiceLine> lines, BillSummary summary) {
        StringBuilder out = new StringBuilder();
        out.append("Invoice# ").append(invoiceId).append("\n");
        for (InvoiceLine line : lines) {
            out.append(String.format("- %s x%d = %.2f\n", line.itemName, line.qty, line.lineTotal));
        }
        out.append(String.format("Subtotal: %.2f\n", summary.subtotal));
        out.append(String.format("Tax(%.0f%%): %.2f\n", summary.taxPercent, summary.tax));
        out.append(String.format("Discount: -%.2f\n", summary.discount));
        out.append(String.format("TOTAL: %.2f\n", summary.total));
        return InvoiceFormatter.identityFormat(out.toString());
    }
}

class InMemoryInvoiceStore implements InvoiceStore {
    private final FileStore delegate = new FileStore();

    @Override
    public void save(String name, String content) {
        delegate.save(name, content);
    }

    @Override
    public int countLines(String name) {
        return delegate.countLines(name);
    }
}
