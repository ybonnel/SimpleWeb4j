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
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketError;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.StringReader;

/**
 * Wrapper on a websocket connection.
 * This class exists to avoir use of specific annotations in api.
 *
 * @param <I> input type of message (client -&gt; server).
 * @param <O> output type of message (server -&gt; server).
 */
@WebSocket
public class WebSocketWrapper<I, O> {

    /**
     * Listener wrapped.
     */
    private final WebSocketListener<I, O> listener;

    /**
     * @param listener listener to wrap.
     */
    public WebSocketWrapper(WebSocketListener<I, O> listener) {
        this.listener = listener;
    }

    /**
     * Method call on message event.
     *
     * @param message message received.
     */
    @OnWebSocketMessage
    public void onMessage(String message) {
        listener.onMessage(
                ContentType.GSON.fromJson(
                        new StringReader(message),
                        listener.getInputType()));

    }

    /**
     * Method call on close event.
     *
     * @param statusCode status code.
     * @param reason     reason of close.
     */
    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        listener.onClose(statusCode, reason);
    }

    /**
     * Method call on error event.
     *
     * @param t exception of the error.
     */
    @OnWebSocketError
    public void onError(Throwable t) {
        listener.onError(t);
    }

    /**
     * Method call on connect event.
     *
     * @param session the WebSocket session.
     */
    @OnWebSocketConnect
    public void onConnect(Session session) {
        listener.onConnect(new WebSocketSession<>(session));
    }

}
