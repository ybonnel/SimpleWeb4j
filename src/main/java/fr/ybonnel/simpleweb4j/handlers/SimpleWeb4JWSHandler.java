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

import fr.ybonnel.simpleweb4j.handlers.websocket.WebSocketWrapper;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.websocket.api.UpgradeRequest;
import org.eclipse.jetty.websocket.api.UpgradeResponse;
import org.eclipse.jetty.websocket.server.WebSocketHandler;
import org.eclipse.jetty.websocket.servlet.WebSocketCreator;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Handler for websocket.
 */
public class SimpleWeb4JWSHandler extends WebSocketHandler implements WebSocketCreator {

    /**
     * All routes.
     */
    private List<WebSocketRoute> routes = new ArrayList<>();

    /**
     * Configure the factory.
     *
     * @param factory the factory to configure.
     */
    @Override
    public void configure(WebSocketServletFactory factory) {
        factory.setCreator(this);
    }

    /**
     * Add a route to handler.
     *
     * @param route route to add.
     */
    public void addRoute(WebSocketRoute route) {
        routes.add(route);
    }


    /**
     * Handle a request.
     *
     * @param target      The target of the request - either a URI or a name.
     * @param baseRequest The original unwrapped request object.
     * @param request     The request either as the {@link Request}
     *                    object or a wrapper of that request.
     * @param response    The response as the {@link org.eclipse.jetty.server.Response}
     *                    object or a wrapper of that request.
     * @throws IOException      in case of IO error.
     * @throws ServletException in case of IO error.
     */
    @Override
    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {
        if (baseRequest.isHandled()) {
            return;
        }
        WebSocketRoute<?, ?> route = findRoute(request.getPathInfo());
        if (route != null) {
            super.handle(target, baseRequest, request, response);
        }
    }

    /**
     * Create the WebSocketWrapper.
     *
     * @param req  the request.
     * @param resp the response.
     * @return the WebSocketWrapper created.
     */
    @Override
    @SuppressWarnings("unchecked")
    public WebSocketWrapper<?, ?> createWebSocket(UpgradeRequest req, UpgradeResponse resp) {
        WebSocketRoute route = findRoute(req.getRequestURI().toASCIIString());

        if (route != null) {

            RouteParameters parameters = new RouteParameters(
                    route.getRouteParams(req.getRequestURI().toASCIIString(), getQueryParameters(req)));

            return new WebSocketWrapper(route.getAdapter().createListenner(parameters));
        }
        return null;
    }

    /**
     * Get parameters from query.
     *
     * @param request the request.
     * @return map of query parameters.
     */
    private Map<String, String> getQueryParameters(UpgradeRequest request) {
        Map<String, String> queryParameters = new HashMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            queryParameters.put(entry.getKey(), entry.getValue()[0]);
        }
        return queryParameters;
    }


    /**
     * Find a route for method and path.
     *
     * @param pathInfo path.
     * @return the route found (null if no route found).
     */
    protected WebSocketRoute<?, ?> findRoute(String pathInfo) {
        for (WebSocketRoute route : routes) {
            if (route.isThisPath(pathInfo)) {
                return route;
            }
        }
        return null;
    }
}
