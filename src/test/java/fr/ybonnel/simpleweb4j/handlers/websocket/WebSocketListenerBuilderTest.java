package fr.ybonnel.simpleweb4j.handlers.websocket;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class WebSocketListenerBuilderTest {
    private String actual;
    private CloseCause actualClose;

    @Test
    public void testOnMessage() {

        actual = null;

        WebSocketListener.<String, String>newBuilder(String.class)
                .onMessage((session, texte) -> actual = texte)
                .build()
                .onMessage("hello");

        assertEquals("hello", actual);

        actual = null;

        WebSocketListener.<String, String>newBuilder(String.class)
                .onMessage(texte -> actual = texte)
                .build()
                .onMessage("hello2");

        assertEquals("hello2", actual);
    }

    @Test
    public void testOnClose() {

        actualClose = null;

        WebSocketListener.<String, String>newBuilder(String.class)
                .onClose((session, closeCause) -> actualClose = closeCause)
                .build()
                .onClose(404, "not found");

        assertNotNull(actualClose);
        assertEquals(404, actualClose.getStatusCode());
        assertEquals("not found", actualClose.getReason());

        actual = null;

        WebSocketListener.<String, String>newBuilder(String.class)
                .onClose(session -> actual = "hello")
                .build()
                .onClose(404, "not found");

        assertEquals("hello", actual);
    }

}
