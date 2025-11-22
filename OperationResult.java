/**
 * Simple value object that wraps the outcome of a domain operation.
 */
public class OperationResult {
    private final boolean success;
    private final String message;

    private OperationResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    /**
     * Creates a successful result.
     *
     * @param message informational message
     * @return success result
     */
    public static OperationResult success(String message) {
        return new OperationResult(true, message);
    }

    /**
     * Creates a failed result.
     *
     * @param message failure reason
     * @return failure result
     */
    public static OperationResult failure(String message) {
        return new OperationResult(false, message);
    }

    /**
     * @return true if successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * @return contextual message
     */
    public String getMessage() {
        return message;
    }
}
