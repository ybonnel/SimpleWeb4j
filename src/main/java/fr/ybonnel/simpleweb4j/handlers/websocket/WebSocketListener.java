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
 * This interface represents all event on WebSocket.
 *
 * @param <I> input type (client -&gt; server).
 * @param <O> output type (server -&gt; client).
 */
public interface WebSocketListener<I, O> {

    /**
     * Method call on close event.
     *
     * @param statusCode status code.
     * @param reason     reason of close.
     */
    void onClose(int statusCode, String reason);

    /**
     * Method call on error event.
     *
     * @param t exception of the error.
     */
    void onError(Throwable t);

    /**
     * Method call on connect event.
     *
     * @param session the WebSocket session.
     */
    void onConnect(WebSocketSession<O> session);

    /**
     * Method call on message event.
     *
     * @param message message received.
     */
    void onMessage(I message);

    /**
     * @return type of input message (client -&gt; server).
     */
    Class<I> getInputType();

    /**
     * Create a builder on WebSocketListenner.
     * @param inputType type of input message (client -&gt; server).
     * @param <I> type of input message (client -&gt; server).
     * @param <O> type of output message (server -&gt; client).
     * @return the builder.
     */
    static <I, O> WebSocketListenerBuilder<I, O> newBuilder(Class<I> inputType) {
        return new WebSocketListenerBuilder<>(inputType);
    }

}
