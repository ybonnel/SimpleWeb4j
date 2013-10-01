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

import com.google.gson.Gson;
import fr.ybonnel.simpleweb4j.entities.SimpleEntity;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.samples.computers.Company;
import org.eclipse.jetty.server.Request;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setEntitiesClasses;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class SimpleWeb4jUnitTest {

    @Spy
    private SimpleWeb4jHandler handler;

    @Before
    public void setup() {
        setEntitiesClasses();
    }

    @Test
    public void testHandleAlreadyHandle() throws IOException, ServletException {
        Request request = makeMockedRequest(true, HttpMethod.GET, "path");

        handler.handle(null, request, request, null);

        verify(handler, never()).processRoute(any(HttpServletRequest.class), any(HttpServletResponse.class), any(Route.class), anyString());
    }

    @Test
    public void testHandleJsonContentType() throws IOException, ServletException {
        final Company expectedCompany = new Company("Github");
        handler.addRoute(HttpMethod.GET, new Route<Void, Company>("path", null, ContentType.JSON) {
            @Override
            public Response<Company> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>(expectedCompany);
            }
        });

        Request request = makeMockedRequest(false, HttpMethod.GET, "path");

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        handler.handle("target", request, request, response);

        verify(response).setStatus(200);
        verify(response).setContentType(ContentType.JSON.getValue());
        verify(writer).print(new Gson().toJson(expectedCompany));
        verify(writer).close();
    }

    @Test
    public void testHandlePlainTextContentType() throws IOException, ServletException {
        final Company expectedCompany = new Company("Github");
        handler.addRoute(HttpMethod.GET, new Route<Void, Company>("path", null, ContentType.PLAIN_TEXT) {
            @Override
            public Response<Company> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>(expectedCompany);
            }
        });

        Request request = makeMockedRequest(false, HttpMethod.GET, "path");

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        handler.handle("target", request, request, response);

        verify(response).setStatus(200);
        verify(response).setContentType(ContentType.PLAIN_TEXT.getValue());
        verify(writer).print(expectedCompany.toString());
        verify(writer).close();
    }

    private Request makeMockedRequest(boolean handled, HttpMethod httpMethod, String path) {
        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(handled);
        when(request.getMethod()).thenReturn(httpMethod.name());
        when(request.getPathInfo()).thenReturn(path);
        when(request.getParameterNames()).thenReturn(Collections.<String>emptyEnumeration());
        return request;
    }

    @Test
    public void testHttpError() throws IOException, ServletException {
        handler.addRoute(HttpMethod.GET, new Route<Void, Void>("path", null) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                throw new HttpErrorException(417, "I'm a tea pot");
            }
        });

        Request request = makeMockedRequest(false, HttpMethod.GET, "path");

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        handler.handle("target", request, request, response);

        verify(response).setStatus(417);
        verify(response).setContentType(ContentType.JSON.getValue());
        verify(writer).print("\"I\\u0027m a tea pot\"");
        verify(writer).close();
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
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        handler.handle("target", request, request, response);

        verify(response).setStatus(500);
        verify(writer).close();
    }

    @Test
    public void testFatalErrorWithEntities() throws IOException, ServletException {
        setEntitiesClasses(SimpleEntity.class);
        testFatalError();
        assertNull(SimpleEntityManager.getCurrentSession());
    }

    @Test
    public void testLimitOfFindRoute() {
        SimpleWeb4jHandler simpleWeb4jHandler = new SimpleWeb4jHandler();

        assertNull(simpleWeb4jHandler.findRoute("GET", "/test"));

        simpleWeb4jHandler.addRoute(HttpMethod.POST, new Route<Void, Void>("/test", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return null;
            }
        });

        assertNull(simpleWeb4jHandler.findRoute("GET", "/test"));

        assertNull(simpleWeb4jHandler.findRoute("POST", "/tutu"));

        assertNotNull(simpleWeb4jHandler.findRoute("POST", "/test"));
    }

    @Test
    public void testLimitOfHandle() throws IOException {
        Request request = mock(Request.class);
        when(request.isHandled()).thenReturn(false);
        when(request.getMethod()).thenReturn("POST");
        when(request.getPathInfo()).thenReturn("path");

        HttpServletResponse response = mock(HttpServletResponse.class);
        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        handler.handle("target", request, request, response);

        verify(response, times(0)).setStatus(500);
        verify(writer, times(0)).close();
    }

}
