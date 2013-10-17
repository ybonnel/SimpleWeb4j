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
package fr.ybonnel.simpleweb4j.handlers.eventsource;

import fr.ybonnel.simpleweb4j.handlers.ContentType;
import org.eclipse.jetty.continuation.Continuation;
import org.junit.Test;

import javax.servlet.ServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ReactiveEventSourceTaskTest {


    @Test(expected = EndOfStreamException.class)
    public void endOfStreamTest() throws IOException, EndOfStreamException {


        ReactiveEventSourceTask reactiveEventSourceTask = new ReactiveEventSourceTask(ContentType.PLAIN_TEXT, getContinuation());
        reactiveEventSourceTask.next(null);
    }

    private Continuation getContinuation() throws IOException {
        Continuation continuation = mock(Continuation.class);
        ServletResponse servletResponse = mock(ServletResponse.class);
        when(continuation.getServletResponse()).thenReturn(servletResponse);
        when(servletResponse.getWriter()).thenThrow(new IOException());
        return continuation;
    }

    @Test
    public void closeWithException() throws IOException {

        ReactiveEventSourceTask reactiveEventSourceTask = new ReactiveEventSourceTask(ContentType.PLAIN_TEXT, getContinuation());
        reactiveEventSourceTask.close();
    }

}
