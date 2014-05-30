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

import fr.ybonnel.simpleweb4j.handlers.RouteParameters;

/**
 * Adapter to handler WebSocket creation request.
 *
 * @param <I> input type (client -&gt; server).
 * @param <O> output type (server -&gt; client).
 */
@FunctionalInterface
public interface WebSocketAdapter<I, O> {

    /**
     * This method must instantiate a {@link fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketListener}
     * in order to listen all events on a WebSocket.
     *
     * @param routeParameters parameters in the routePath.
     * @return the listener instantiated.
     */
    WebSocketListener<I, O> createListenner(RouteParameters routeParameters);
}
