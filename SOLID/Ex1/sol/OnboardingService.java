import java.util.*;

public class OnboardingService {
    private final StudentDb db;
    private final StudentInputParser parser;
    private final StudentValidator validator;
    private final IdGenerator idGenerator;
    private final StudentPrinter printer;

    public OnboardingService(StudentDb db) {
        this.db = db;
        this.parser = new StudentInputParser();
        this.validator = new StudentValidator();
        this.idGenerator = new IdGenerator();
        this.printer = new StudentPrinter();
    }

    public void registerFromRawInput(String raw) {
        printer.printInput(raw);
        StudentInput input = parser.parse(raw);
        var errors = validator.validate(input);
        if (!errors.isEmpty()) {
            printer.printErrors(errors);
            return;
        }
        String id = idGenerator.nextStudentId(db.count());
        StudentRecord rec = new StudentRecord(id, input.name, input.email, input.phone, input.program);
        db.save(rec);
        printer.printSuccess(rec, db.count());
    }
}

// --- SRP Components ---

class StudentInput {
    public final String name, email, phone, program;
    public StudentInput(String name, String email, String phone, String program) {
        this.name = name; this.email = email; this.phone = phone; this.program = program;
    }
}

class StudentInputParser {
    public StudentInput parse(String raw) {
        Map<String,String> kv = new java.util.LinkedHashMap<>();
        String[] parts = raw.split(";");
        for (String p : parts) {
            String[] t = p.split("=", 2);
            if (t.length == 2) kv.put(t[0].trim(), t[1].trim());
        }
        return new StudentInput(
            kv.getOrDefault("name", ""),
            kv.getOrDefault("email", ""),
            kv.getOrDefault("phone", ""),
            kv.getOrDefault("program", "")
        );
    }
}

class StudentValidator {
    public java.util.List<String> validate(StudentInput input) {
        java.util.List<String> errors = new java.util.ArrayList<>();
        if (input.name.isBlank()) errors.add("name is required");
        if (input.email.isBlank() || !input.email.contains("@")) errors.add("email is invalid");
        if (input.phone.isBlank() || !input.phone.chars().allMatch(Character::isDigit)) errors.add("phone is invalid");
        if (!(input.program.equals("CSE") || input.program.equals("AI") || input.program.equals("SWE")))
            errors.add("program is invalid");
        return errors;
    }
}

interface StudentDb {
    void save(StudentRecord r);
    int count();
    java.util.List<StudentRecord> all();
}

class IdGenerator {
    public String nextStudentId(int currentCount) {
        int next = currentCount + 1;
        String num = String.format("%04d", next);
        return "SST-2026-" + num;
    }
}

class StudentPrinter {
    public void printInput(String raw) {
        System.out.println("INPUT: " + raw);
    }
    public void printErrors(java.util.List<String> errors) {
        System.out.println("ERROR: cannot register");
        for (String e : errors) System.out.println("- " + e);
    }
    public void printSuccess(StudentRecord rec, int total) {
        System.out.println("OK: created student " + rec.id);
        System.out.println("Saved. Total students: " + total);
        System.out.println("CONFIRMATION:");
        System.out.println(rec);
    }
}
