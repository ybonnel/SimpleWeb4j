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

import org.eclipse.jetty.server.Request;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.mockito.Mockito.*;

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

        Request requestJetty = mock(Request.class);

        when(requestJetty.isHandled()).thenReturn(true);

        handler.handle(null, requestJetty, requestJetty, null);

        verify(requestJetty).isHandled();
    }
}
