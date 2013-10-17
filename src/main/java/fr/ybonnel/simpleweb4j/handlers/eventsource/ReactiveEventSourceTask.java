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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Task for write of event-source Data in reactive way.
 */
public class ReactiveEventSourceTask implements ReactiveHandler {

    /**
     * logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ReactiveEventSourceTask.class);

    /**
     * Content type of response.
     */
    private final ContentType contentType;
    /**
     * continuation object for async responses.
     */
    private final Continuation continuation;

    /**
     * Constructor.
     *
     * @param contentType     Content type of response.
     * @param continuation    continuation object for async responses.
     */
    public ReactiveEventSourceTask(ContentType contentType, Continuation continuation) {
        this.contentType = contentType;
        this.continuation = continuation;
    }

    /**
     * Method to call to send next object to the stream.
     * @param object object to send.
     * @throws EndOfStreamException exception used to inform of end of stream.
     */
    @Override
    public void next(Object object) throws EndOfStreamException {
        try {
            continuation.getServletResponse().getWriter().print("data: ");
            continuation.getServletResponse().getWriter().print(contentType.convertObject(object));
            continuation.getServletResponse().getWriter().print("\n\n");
            continuation.getServletResponse().getWriter().flush();
            continuation.getServletResponse().flushBuffer();
        } catch (IOException e) {
            continuation.complete();
            throw new EndOfStreamException(e);
        }
    }

    /**
     * Method call to close the stream if you have no more data to send.
     */
    @Override
    public void close() {
        try {
            continuation.getServletResponse().getWriter().close();
        } catch (IOException ignore) {
            LOGGER.warn("Error during close of response", ignore);
        }
        continuation.complete();
    }
}
