/*
 * Copyright 2013- Yan Bonnel
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.ybonnel.simpleweb4j.handlers;

import fr.ybonnel.simpleweb4j.entities.SimpleEntity;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setEntitiesClasses;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class JsonHandlerUnitTest {

    private JsonHandler handler;

    @Before
    public void setup() {
        handler = new JsonHandler();
        setEntitiesClasses();
    }

    @Test
    public void testHandleAlreadyHandle() throws IOException, ServletException {
        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(true);
        handler.handle(null, request, null, 0);
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

        HttpServletResponse response = mock(HttpServletResponse.class);
        ServletOutputStream outputStream = mock(ServletOutputStream.class);
        when(response.getOutputStream()).thenReturn(outputStream);

        handler.handle("target", request, response, 0);

        verify(response).setStatus(417);
        verify(response).setContentType("application/json");
        verify(outputStream).print("\"I\\u0027m a tea pot\"");
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

        handler.handle("target", request, response, 0);

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
    public void testHandler() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        HttpServletRequest request = mock(HttpServletRequest.class);

        Method setCurrentConnection = HttpConnection.class.getDeclaredMethod("setCurrentConnection", HttpConnection.class);
        setCurrentConnection.setAccessible(true);

        HttpConnection connection = mock(HttpConnection.class);
        setCurrentConnection.invoke(null, connection);

        Request requestJetty = mock(Request.class);

        when(connection.getRequest()).thenReturn(requestJetty);
        when(requestJetty.isHandled()).thenReturn(true);

        handler.handle(null, request, null, 0);

        verify(connection).getRequest();
        verify(requestJetty).isHandled();
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
