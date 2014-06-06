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
package fr.ybonnel.simpleweb4j.handlers.webjars;

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Mockito.*;

public class WebjarHandlerTest {

    @Test
    public void testAlreadyHandled() throws IOException {

        WebjarHandler handler = new WebjarHandler();

        Request requestJetty = mock(Request.class);

        when(requestJetty.isHandled()).thenReturn(true);

        handler.handle(null, requestJetty, requestJetty, null);

        verify(requestJetty).isHandled();
    }

    @Test(expected = IOException.class)
    public void testIOException() throws IOException {

        WebjarHandler handler = new WebjarHandler() {
            @Override
            protected InputStream getInputStream(URL url) throws IOException {
                throw new IOException();
            }
        };

        Request requestJetty = mock(Request.class);

        when(requestJetty.isHandled()).thenReturn(false);

        when(requestJetty.getPathInfo()).thenReturn("/webjars/jquery/2.0.3/jquery.js");

        handler.handle(null, requestJetty, requestJetty, null);
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnOut() throws IOException {

        WebjarHandler handler = new WebjarHandler();

        Request requestJetty = mock(Request.class);

        HttpServletResponse response = mock(HttpServletResponse.class);

        when(requestJetty.isHandled()).thenReturn(false);

        when(requestJetty.getPathInfo()).thenReturn("/webjars/jquery/2.0.3/jquery.js");

        when(response.getOutputStream()).thenThrow(new IOException());

        handler.handle(null, requestJetty, requestJetty, response);
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnWrite() throws IOException {

        WebjarHandler handler = new WebjarHandler();

        Request requestJetty = mock(Request.class);

        HttpServletResponse response = mock(HttpServletResponse.class);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(requestJetty.isHandled()).thenReturn(false);

        when(requestJetty.getPathInfo()).thenReturn("/webjars/jquery/2.0.3/jquery.js");

        when(response.getOutputStream()).thenReturn(outputStream);

        doThrow(new IOException()).when(outputStream).write(any(), anyInt(), anyInt());

        handler.handle(null, requestJetty, requestJetty, response);
    }

    @Test(expected = IOException.class)
    public void testIOExceptionOnRead() throws IOException {

        InputStream inputStream = mock(InputStream.class);

        WebjarHandler handler = new WebjarHandler() {
            @Override protected InputStream getInputStream(URL url) throws IOException {
                return inputStream;
            }
        };

        Request requestJetty = mock(Request.class);

        HttpServletResponse response = mock(HttpServletResponse.class);

        ServletOutputStream outputStream = mock(ServletOutputStream.class);

        when(requestJetty.isHandled()).thenReturn(false);

        when(requestJetty.getPathInfo()).thenReturn("/webjars/jquery/2.0.3/jquery.js");

        when(response.getOutputStream()).thenReturn(outputStream);

        doThrow(new IOException()).when(inputStream).read(any());

        handler.handle(null, requestJetty, requestJetty, response);
    }
}
