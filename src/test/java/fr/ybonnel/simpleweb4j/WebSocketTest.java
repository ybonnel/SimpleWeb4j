package fr.ybonnel.simpleweb4j;

import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketListener;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.resetDefaultValues;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPort;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.start;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.websocket;
import static org.junit.Assert.assertEquals;

public class WebSocketTest {


    private Random random = new Random();

    private List<String> messagesReceived;
    private int port;

    @Before
    public void startServer() {
        resetDefaultValues();
        port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        messagesReceived = new ArrayList<>();

        websocket("/websocket/:name", (routeParams) ->
                WebSocketListener.<String, String>newBuilder(String.class)
                        .onConnect((session) -> {
                            try {
                                session.sendMessage(
                                        "Hello " + routeParams.getParam("name") + routeParams.getParam("suffixe"));
                            } catch (IOException ignore) {

                            }
                        })
                        .onMessage((session, message) -> {
                            messagesReceived.add(message);
                            try {
                                session.getSession().disconnect();
                            } catch (IOException ignore) {
                            }
                        })
                        .onClose((session, cause) -> {
                            System.out.println(cause.getStatusCode() + " : " + cause.getReason());
                        })
                        .onError((session, throwable) -> {
                        })
                        .build());

        setPort(port);

        start(false);
    }

    @After
    public void stopServer() throws Exception {
        SimpleWeb4j.stop();
    }

    @Test
    public void testWebSocket() throws Exception {
        WebSocketClient client = new WebSocketClient();

        client.start();

        List<String> clientReceived = new ArrayList<>();

        Session session = client.connect(
                new org.eclipse.jetty.websocket.api.WebSocketAdapter() {
                    @Override public void onWebSocketText(String message) {
                        clientReceived.add(message);
                    }
                },
                new URI("ws://localhost:" + port + "/websocket/world?suffixe=!"))
                .get();

        long timeBeforeWait = System.nanoTime();

        while (clientReceived.isEmpty()
                && (System.nanoTime() - timeBeforeWait < TimeUnit.SECONDS.toNanos(1))) {
            Thread.sleep(50);
        }

        session.getRemote().sendString("\"Hello Server\"");

        timeBeforeWait = System.nanoTime();

        while (messagesReceived.isEmpty()
                && (System.nanoTime() - timeBeforeWait < TimeUnit.SECONDS.toNanos(1))) {
            Thread.sleep(50);
        }

        session.close();

        assertEquals(1, clientReceived.size());
        assertEquals("\"Hello world!\"", clientReceived.get(0));


        assertEquals(1, messagesReceived.size());
        assertEquals("Hello Server", messagesReceived.get(0));


    }

}
