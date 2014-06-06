package fr.ybonnel.simpleweb4j.handlers;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.junit.Test;

import javax.servlet.ServletException;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SimpleWeb4JWSHandlerTest {

    @Test
    public void testHandled() throws IOException, ServletException {
        SimpleWeb4JWSHandler handler = new SimpleWeb4JWSHandler();

        Request request = mock(Request.class);

        when(request.isHandled()).thenReturn(true);

        handler.handle(null, request, request, null);

        verify(request).isHandled();
    }

    @Test
    public void testCreateWebSocket() throws URISyntaxException {
        SimpleWeb4JWSHandler handler = new SimpleWeb4JWSHandler();

        UpgradeRequest request = mock(UpgradeRequest.class);

        when(request.getRequestURI()).thenReturn(new URI("/toto"));

        assertNull(handler.createWebSocket(request, null));

        verify(request).getRequestURI();
    }
}
