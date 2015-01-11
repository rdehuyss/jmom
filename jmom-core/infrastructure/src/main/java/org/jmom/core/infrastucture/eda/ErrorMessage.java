package org.jmom.core.infrastucture.eda;

public class ErrorMessage extends Message {

    private String reason;

    private ErrorMessage() {
        super(null);
    }

    public ErrorMessage(String reason) {
        super(null);
        this.reason = reason;
    }

    public ErrorMessage(Command command, String reason) {
        super(command.getCorrelationId());
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }
}
