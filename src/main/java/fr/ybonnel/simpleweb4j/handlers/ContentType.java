package fr.ybonnel.simpleweb4j.handlers;

import java.nio.charset.Charset;

public enum ContentType {
    JSON("application/json;charset=" + Charset.defaultCharset().displayName()), PLAIN_TEXT("text/plain;charset=" + Charset.defaultCharset().displayName());

    private final String value;

    ContentType(String s) {
        this.value = s;
    }



    public String getValue() {
        return value;
    }
}
