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

import org.junit.Test;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class LessCompilerHandlerTest {

    @Test
    public void stupidTestOnPrivateConstructor() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        for (Class<?> clazz : LessCompilerHandler.class.getDeclaredClasses()) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }
    }

    @Test
    public void testHandler() throws IOException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        LessCompilerHandler handler = new LessCompilerHandler();

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
}
