import java.nio.charset.StandardCharsets;

public class PdfExporter extends Exporter {
    @Override
    protected boolean supports(ExportRequest req) {
        return req.body.length() <= 20;
    }

    @Override
    protected String unsupportedReason() {
        return "PDF cannot handle content > 20 chars";
    }

    @Override
    protected ExportResult doExport(ExportRequest req) {
        String fakePdf = "PDF(" + req.title + "):" + req.body;
        return new ExportResult("application/pdf", fakePdf.getBytes(StandardCharsets.UTF_8));
    }
}
