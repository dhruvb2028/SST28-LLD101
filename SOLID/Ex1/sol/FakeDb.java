import java.util.*;

public class FakeDb implements StudentDb {
    private final java.util.List<StudentRecord> rows = new java.util.ArrayList<>();

    @Override
    public void save(StudentRecord r) { rows.add(r); }
    @Override
    public int count() { return rows.size(); }
    @Override
    public java.util.List<StudentRecord> all() { return java.util.Collections.unmodifiableList(rows); }
}
