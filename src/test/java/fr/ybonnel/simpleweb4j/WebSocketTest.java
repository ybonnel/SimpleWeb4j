package fr.ybonnel.simpleweb4j;

import fr.ybonnel.simpleweb4j.handlers.websocket.SimpleWebSocketListener;
import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketSession;
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

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
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

        websocket("/websocket/:name", (routeParams) -> new SimpleWebSocketListener<String, String>(String.class){
            @Override
            public void onConnect(WebSocketSession<String> session) {
                super.onConnect(session);
                try {
                    session.sendMessage("Hello " + routeParams.getParam("name") + routeParams.getParam("suffixe"));
                } catch (IOException ignore) {

                }
            }

            @Override public void onMessage(String message) {
                super.onMessage(message);

                messagesReceived.add(message);
                try {
                    getCurrentSession().getSession().disconnect();
                } catch (IOException ignore) {}
            }
        });

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
