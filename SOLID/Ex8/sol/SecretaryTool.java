public class SecretaryTool implements ClubAdminTools, MinutesAdmin {
    private final MinutesBook book;
    public SecretaryTool(MinutesBook book) { this.book = book; }

    @Override public void addMinutes(String text) { book.add(text); }
}
