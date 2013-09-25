package fr.ybonnel.simpleweb4j.handlers;

import fr.ybonnel.simpleweb4j.entities.SimpleEntity;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setEntitiesClasses;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TextHandlerUnitTest {

    @Spy
    private TextHandler handler;

    @Before
    public void setup() {
        setEntitiesClasses();
    }

    @Test
    public void testHandleAlreadyHandle() throws IOException, ServletException {
        //given
        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(true);
        when(request.getMethod()).thenReturn(HttpMethod.GET.toString());

        //when
        handler.handle(null, request, request, null);

        verify(handler, never()).processRoute(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Route.class));
    }

    @Test
    public void testHttpError() throws IOException, ServletException {
        handler.addRoute(HttpMethod.GET, new Route<Void, Void>("path", null) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                throw new HttpErrorException(417, "I'm a tea pot");
            }
        });

        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(false);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("path");
        when(request.getParameterNames()).thenReturn(Collections.<String>emptyEnumeration());

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        handler.handle("target", request, request, response);

        verify(response).setStatus(417);
        verify(response).setContentType(TextHandler.PLAIN_TEXT_CONTENT_TYPE);
        verify(outputStream).print("I'm a tea pot");
        verify(outputStream).close();
    }

    @Test
    public void testHttpErrorWithEntities() throws IOException, ServletException {
        setEntitiesClasses(SimpleEntity.class);
        testHttpError();
        assertNull(SimpleEntityManager.getCurrentSession());
    }

    @Test
    public void testFatalError() throws IOException, ServletException {
        handler.addRoute(HttpMethod.GET, new Route<Void, Void>("path", null) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                throw new NullPointerException();
            }
        });

        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(false);
        when(request.getMethod()).thenReturn("GET");
        when(request.getPathInfo()).thenReturn("path");

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        handler.handle("target", request, request, response);

        verify(response).setStatus(500);
        verify(outputStream).close();
    }

    @Test
    public void testFatalErrorWithEntities() throws IOException, ServletException {
        setEntitiesClasses(SimpleEntity.class);
        testFatalError();
        assertNull(SimpleEntityManager.getCurrentSession());
    }

    @Test
    public void testLimitOfFindRoute() {
        JsonHandler jsonHandler = new JsonHandler();

        assertNull(jsonHandler.findRoute("GET", "/test"));

        jsonHandler.addRoute(HttpMethod.POST, new Route<Void, Void>("/test", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return null;
            }
        });

        assertNull(jsonHandler.findRoute("GET", "/test"));

        assertNull(jsonHandler.findRoute("POST", "/tutu"));

        assertNotNull(jsonHandler.findRoute("POST", "/test"));
    }



}
