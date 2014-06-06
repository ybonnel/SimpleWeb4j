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

        SimpleWebSocketListener<String, String> listener = new SimpleWebSocketListener<String, String>(String.class){
            @Override public void onError(Throwable t) {
                super.onError(t);
                actual = t;
            }
        };

        new WebSocketWrapper<>(listener).onError(expected);

        assertTrue(actual == expected);
    }

}
