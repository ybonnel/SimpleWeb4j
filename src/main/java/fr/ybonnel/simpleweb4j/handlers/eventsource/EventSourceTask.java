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

import com.google.gson.Gson;
import fr.ybonnel.simpleweb4j.handlers.Response;
import org.eclipse.jetty.continuation.Continuation;

import java.io.IOException;

/**
 * Task for write of event-source Data.
 */
public class EventSourceTask implements Runnable {
    /**
     * gson object for json transforms.
     */
    private final Gson gson;
    /**
     * response of route.
     */
    private final Response<Stream> handlerResponse;
    /**
     * continuation object for async responses.
     */
    private final Continuation continuation;

    /**
     * Constructor.
     *
     * @param gson            gson object for json transforms.
     * @param handlerResponse response of route.
     * @param continuation    continuation object for async responses.
     */
    public EventSourceTask(Gson gson, Response<Stream> handlerResponse, Continuation continuation) {
        this.gson = gson;
        this.handlerResponse = handlerResponse;
        this.continuation = continuation;
    }

    /**
     * Write datas for event-source.
     */
    @Override
    public void run() {
        synchronized (handlerResponse) {
            try {
                Object data = handlerResponse.getAnswer().next();
                continuation.getServletResponse().getOutputStream().print("data: ");
                continuation.getServletResponse().getOutputStream().print(gson.toJson(data));
                continuation.getServletResponse().getOutputStream().print("\n\n");
                continuation.getServletResponse().getOutputStream().flush();
                continuation.getServletResponse().flushBuffer();
            } catch (IOException ioException) {
                continuation.complete();
                throw new RuntimeException("Clone event-source", ioException);
            }
        }
    }

}
