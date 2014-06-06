package fr.ybonnel.simpleweb4j.handlers.websocket;

/**
 * Represent the cause of close for websocket.
 */
public class CloseCause {
    /**
     * status code.
     */
    private final int statusCode;
    /**
     * Reason.
     */
    private final String reason;

    /**
     * Constructor.
     * @param statusCode status code.
     * @param reason reason.
     */
    CloseCause(int statusCode, String reason) {
        this.statusCode = statusCode;
        this.reason = reason;
    }

    /**
     * @return status code.
     */
    public int getStatusCode() {
        return statusCode;
    }

    /**
     * @return reason.
     */
    public String getReason() {
        return reason;
    }
}
