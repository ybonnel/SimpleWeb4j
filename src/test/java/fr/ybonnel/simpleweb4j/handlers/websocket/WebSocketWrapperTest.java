package fr.ybonnel.simpleweb4j.handlers.websocket;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class WebSocketWrapperTest {

    Throwable actual;
    Throwable expected = new IOException();

    @Test
    public void testOnError() {
        actual = null;

        WebSocketListener<String, String> listener =
                WebSocketListener.<String, String>newBuilder(String.class)
                        .onError((session, t) -> actual = t)
                        .build();


        new WebSocketWrapper<>(listener).onError(expected);

        assertTrue(actual == expected);
    }

    @Test
    public void testWithNoHandler() {
        WebSocketListener<String, String> listener =
                WebSocketListener.<String, String>newBuilder(String.class).build();

        WebSocketWrapper<String, String> wrapper = new WebSocketWrapper<>(listener);
        wrapper.onError(expected);
        wrapper.onClose(404, "not found");
        wrapper.onConnect(null);
        wrapper.onMessage("message");

    }

}
