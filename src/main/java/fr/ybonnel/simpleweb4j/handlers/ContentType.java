package fr.ybonnel.simpleweb4j.handlers;

import java.nio.charset.Charset;

/**
 * Content type of output.
 */
public enum ContentType {
    /**
     * Use it for a json output (default Content type).
     */
    JSON("application/json;charset=" + Charset.defaultCharset().displayName()),
    /**
     * Plain text (use toString method to do the output).
     */
    PLAIN_TEXT("text/plain;charset=" + Charset.defaultCharset().displayName()),
    EVENT_STREAM("text/event-stream;charset=" + Charset.defaultCharset().displayName()),;

    /**
     * Value of content type.
     */
    private final String value;

    /**
     * Constructor.
     * @param s value of content type.
     */
    private ContentType(String s) {
        this.value = s;
    }

    /**
     * @return value of content type.
     */
    public String getValue() {
        return value;
    }
}