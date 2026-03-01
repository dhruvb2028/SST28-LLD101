public abstract class Exporter {
    public final ExportResult export(ExportRequest req) {
        ExportRequest safeRequest = normalize(req);
        if (!supports(safeRequest)) {
            throw new IllegalArgumentException(unsupportedReason());
        }
        ExportResult result = doExport(safeRequest);
        if (result == null || result.bytes == null || result.contentType == null) {
            throw new IllegalStateException("Exporter contract violation: result must be non-null");
        }
        return result;
    }

    protected ExportRequest normalize(ExportRequest req) {
        if (req == null) return new ExportRequest("", "");
        String title = req.title == null ? "" : req.title;
        String body = req.body == null ? "" : req.body;
        return new ExportRequest(title, body);
    }

    protected boolean supports(ExportRequest req) {
        return true;
    }

    protected String unsupportedReason() {
        return "unsupported export request";
    }

    protected abstract ExportResult doExport(ExportRequest req);
}
