public enum ApplicationStatus {
    PENDING("Pending"),
    SUCCESSFUL("Successful"),
    UNSUCCESSFUL("Unsuccessful"),
    CONFIRMED("Confirmed"),
    WITHDRAWAL_REQUESTED("Withdrawal Requested"),
    WITHDRAWN("Withdrawn"),
    WITHDRAWAL_REJECTED("Withdrawal Rejected");

    private final String label;

    ApplicationStatus(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static ApplicationStatus fromLabel(String label) {
        for (ApplicationStatus status : values()) {
            if (status.label.equalsIgnoreCase(label)) {
                return status;
            }
        }
        throw new IllegalArgumentException("Unknown application status: " + label);
    }
}
