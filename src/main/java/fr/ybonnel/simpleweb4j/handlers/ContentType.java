package fr.ybonnel.simpleweb4j.handlers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.charset.Charset;

/**
 * Content type of output.
 */
public enum ContentType {
    /**
     * Use it for a json output (default Content type).
     */
    JSON("application/json;charset=" + Charset.defaultCharset().displayName()) {
        @Override
        public String convertObject(Object object) {
            return GSON.toJson(object);
        }
    },
    /**
     * Plain text (use toString method to do the output).
     */
    PLAIN_TEXT("text/plain;charset=" + Charset.defaultCharset().displayName()) {
        @Override
        public String convertObject(Object object) {
            return object.toString();
        }
    };

    /**
     * Current charset.
     */
    public static final String CURRENT_CHARSET = Charset.defaultCharset().displayName();

    /**
     * Gson used to serialize/deserialize json objects.
     */
    public static final Gson GSON = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX").create();

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

    /**
     * Method use for conversion of return object with right content type.
     * @param object object to convert.
     * @return object converted into String.
     */
    public abstract String convertObject(Object object);
}