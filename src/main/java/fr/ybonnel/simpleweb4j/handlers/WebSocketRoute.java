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

import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketAdapter;

/**
 * Represent a route for a websocket.
 *
 * @param <I> type of input message (client -&gt; server).
 * @param <O> type of output message (server -&gt; client).
 */
public class WebSocketRoute<I, O> extends CommonRoute {

    /**
     * WebSocket adapter of the route.
     */
    private final WebSocketAdapter<I, O> adapter;

    /**
     * Constructor of a route.
     *
     * @param routePath routePath of the route.
     * @param adapter   the adapter which create the listenner.
     */
    public WebSocketRoute(String routePath, WebSocketAdapter<I, O> adapter) {
        super(routePath);
        this.adapter = adapter;
    }

    /**
     * @return the adapter of the route.
     */
    public WebSocketAdapter<I, O> getAdapter() {
        return adapter;
    }
}
