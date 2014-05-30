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
package fr.ybonnel.simpleweb4j.handlers.websocket;

import fr.ybonnel.simpleweb4j.handlers.ContentType;
import org.eclipse.jetty.websocket.api.Session;

import java.io.IOException;

/**
 * Wrapper of {@link org.eclipse.jetty.websocket.api.Session}.
 * <p>
 * This wrapper contains the method {@link #sendMessage(Object)} in addition of classical Session.
 *
 * @param <T> type of output message (server -&gt; client).
 */
public class WebSocketSession<T> {

    /**
     * The original session.
     */
    private final Session session;

    /**
     * @param session the original session.
     */
    public WebSocketSession(Session session) {
        this.session = session;
    }

    /**
     * Send a message to client.
     *
     * @param message message to send.
     * @throws IOException in case of write error.
     */
    public void sendMessage(T message) throws IOException {
        session.getRemote().sendString(ContentType.GSON.toJson(message));
    }

    /**
     * @return jetty session.
     */
    public Session getSession() {
        return session;
    }
}
