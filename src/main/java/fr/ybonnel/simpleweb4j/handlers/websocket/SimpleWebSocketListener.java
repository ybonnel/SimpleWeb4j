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

/**
 * Very simple implementation of
 * {@link fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketListener}
 * with nothing on each event.
 * This help you if you don't want to implement unnecessary method.
 *
 * @param <I> type of input message (client -&gt; server).
 * @param <O> type of output message (server -&gt; client).
 */
public class SimpleWebSocketListener<I, O> implements WebSocketListener<I, O> {

    /**
     * type of input message (client -> server).
     */
    private final Class<I> inputType;

    /**
     * The current session.
     */
    private WebSocketSession<O> currentSession;

    /**
     * @param inputType type of input message (client -&gt; server).
     */
    public SimpleWebSocketListener(Class<I> inputType) {
        this.inputType = inputType;
    }

    /**
     * Method call on close event.
     *
     * @param statusCode status code.
     * @param reason     reason of close.
     */
    @Override
    public void onClose(int statusCode, String reason) {
    }

    /**
     * Method call on error event.
     *
     * @param t exception of the error.
     */
    @Override
    public void onError(Throwable t) {
    }

    /**
     * Method call on connect event.
     *
     * @param session the WebSocket currentSession.
     */
    @Override
    public void onConnect(WebSocketSession<O> session) {
        this.currentSession = session;
    }

    /**
     * Method call on message event.
     *
     * @param message message received.
     */
    @Override
    public void onMessage(I message) {
    }

    /**
     * @return type of input message (client -&gt; server).
     */
    @Override
    public Class<I> getInputType() {
        return inputType;
    }

    /**
     *
     * @return current session.
     */
    public WebSocketSession<O> getCurrentSession() {
        return currentSession;
    }
}
