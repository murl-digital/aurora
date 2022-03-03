package digital.murl.aurora;

public class Result {
    public final Outcome outcome;
    public final String message;

    public Result(Outcome outcome, String message) {
        this.outcome = outcome;
        this.message = message;
    }

    public enum Outcome {
        SUCCESS,
        ERROR,
        INVALID_ARGS,
        NOT_FOUND
    }
}
