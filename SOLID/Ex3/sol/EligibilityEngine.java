import java.util.*;

public class EligibilityEngine {
    private final FakeEligibilityStore store;
    private final List<EligibilityRule> rules;

    public EligibilityEngine(FakeEligibilityStore store) {
        this(store, new RuleInput());
    }

    public EligibilityEngine(FakeEligibilityStore store, RuleInput input) {
        this.store = store;
        this.rules = List.of(
                new NoDisciplinaryFlagRule(),
                new MinCgrRule(input.minCgr),
                new MinAttendanceRule(input.minAttendance),
                new MinCreditsRule(input.minCredits)
        );
    }

    public void runAndPrint(StudentProfile s) {
        ReportPrinter p = new ReportPrinter();
        EligibilityEngineResult r = evaluate(s);
        p.print(s, r);
        store.save(s.rollNo, r.status);
    }

    public EligibilityEngineResult evaluate(StudentProfile s) {
        List<String> reasons = new ArrayList<>();
        String status = "ELIGIBLE";

        for (EligibilityRule rule : rules) {
            Optional<String> failure = rule.reasonIfNotEligible(s);
            if (failure.isPresent()) {
                status = "NOT_ELIGIBLE";
                reasons.add(failure.get());
                break;
            }
        }

        return new EligibilityEngineResult(status, reasons);
    }
}

interface EligibilityRule {
    Optional<String> reasonIfNotEligible(StudentProfile student);
}

class NoDisciplinaryFlagRule implements EligibilityRule {
    @Override
    public Optional<String> reasonIfNotEligible(StudentProfile student) {
        if (student.disciplinaryFlag != LegacyFlags.NONE) {
            return Optional.of("disciplinary flag present");
        }
        return Optional.empty();
    }
}

class MinCgrRule implements EligibilityRule {
    private final double minCgr;

    public MinCgrRule(double minCgr) {
        this.minCgr = minCgr;
    }

    @Override
    public Optional<String> reasonIfNotEligible(StudentProfile student) {
        if (student.cgr < minCgr) {
            return Optional.of("CGR below " + String.format("%.1f", minCgr));
        }
        return Optional.empty();
    }
}

class MinAttendanceRule implements EligibilityRule {
    private final int minAttendance;

    public MinAttendanceRule(int minAttendance) {
        this.minAttendance = minAttendance;
    }

    @Override
    public Optional<String> reasonIfNotEligible(StudentProfile student) {
        if (student.attendancePct < minAttendance) {
            return Optional.of("attendance below " + minAttendance);
        }
        return Optional.empty();
    }
}

class MinCreditsRule implements EligibilityRule {
    private final int minCredits;

    public MinCreditsRule(int minCredits) {
        this.minCredits = minCredits;
    }

    @Override
    public Optional<String> reasonIfNotEligible(StudentProfile student) {
        if (student.earnedCredits < minCredits) {
            return Optional.of("credits below " + minCredits);
        }
        return Optional.empty();
    }
}

class EligibilityEngineResult {
    public final String status;
    public final List<String> reasons;
    public EligibilityEngineResult(String status, List<String> reasons) {
        this.status = status;
        this.reasons = reasons;
    }
}
