public class EvaluationPipeline {
    private final Rubric rubric;
    private final PlagiarismScorer plagiarismScorer;
    private final SubmissionGrader submissionGrader;
    private final EvaluationReportWriter reportWriter;

    public EvaluationPipeline(
        Rubric rubric,
        PlagiarismScorer plagiarismScorer,
        SubmissionGrader submissionGrader,
        EvaluationReportWriter reportWriter
    ) {
        this.rubric = rubric;
        this.plagiarismScorer = plagiarismScorer;
        this.submissionGrader = submissionGrader;
        this.reportWriter = reportWriter;
    }

    public void evaluate(Submission sub) {
        int plag = plagiarismScorer.check(sub);
        System.out.println("PlagiarismScore=" + plag);

        int code = submissionGrader.grade(sub, rubric);
        System.out.println("CodeScore=" + code);

        String reportName = reportWriter.write(sub, plag, code);
        System.out.println("Report written: " + reportName);

        int total = plag + code;
        String result = (total >= 90) ? "PASS" : "FAIL";
        System.out.println("FINAL: " + result + " (total=" + total + ")");
    }
}
