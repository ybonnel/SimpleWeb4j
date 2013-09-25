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


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.filter.AbstractFilter;
import org.eclipse.jetty.server.Request;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * Class use to handler all json services (declared by Route or RestResource).
 */
public class JsonHandler extends AbstractSimpleHandler {


    public static final String JSON_CONTENT_TYPE = "application/json;charset=" + Charset.defaultCharset().displayName();

    /**
     * Map of routes for jsonp with callbacks.
     */
    private Map<Route, String> jsonpRoutes = new HashMap<>();

    /**
     * Gson used to serialize/deserialize json objects.
     */
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX").create();

    /**
     * Add a route with jsonp support.
     *
     * @param callbackName name of query param of callback.
     * @param route        route to add.
     */
    public void addJsonpRoute(Route route, String callbackName) {
        jsonpRoutes.put(route, callbackName);
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
     * @throws IOException in case of IO error.
     */
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (baseRequest.isHandled()) {
            return;
        }
        Route<?, ?> route = findRoute(request.getMethod(), request.getPathInfo());
        String callback = null;
        if (route == null && HttpMethod.fromValue(request.getMethod()) == HttpMethod.GET) {
            for (Map.Entry<Route, String> entry : jsonpRoutes.entrySet()) {
                if (entry.getKey().isThisPath(request.getPathInfo())) {
                    route = entry.getKey();
                    callback = entry.getValue();
                }
            }
        }

        if (route == null) {
            return;
        }

        processRoute(request, response, route, callback);
        baseRequest.setHandled(true);

    }

    /**
     * Process a route.
     *
     * @param request  The request either as the {@link Request}
     *                 object or a wrapper of that request.
     * @param response The response as the {@link org.eclipse.jetty.server.Response}
     *                 object or a wrapper of that request.
     * @param route    route to apply.
     * @param callback callback in case of jsonp.
     * @param <P>      parameter type of route.
     * @param <R>      return type of route.
     * @throws IOException in case of IO error.
     */
    private <P, R> void processRoute(HttpServletRequest request, HttpServletResponse response,
                                     Route<P, R> route, String callback) throws IOException {
        P param = getRouteParam(request, route);
        try {
            beginTransaction();
            RouteParameters parameters = new RouteParameters(
                    route.getRouteParams(request.getPathInfo(), getQueryParameters(request)));
            for (AbstractFilter filter : filters) {
                filter.handle(route, parameters);
            }
            Response<R> handlerResponse = route.handle(param, parameters);
            commitTransaction();
            writeHttpResponse(request, response, handlerResponse, callback, parameters, gson.toJson(handlerResponse.getAnswer()));
        } catch (HttpErrorException httpError) {
            commitTransaction();
            writeHttpError(response, httpError, gson.toJson(httpError.getAnswer()));
        } catch (Exception exception) {
            rollBackTransaction();
            writeInternalError(response, exception);
        }
    }

    /**
     * Parse the parameter of route (content of request body).
     *
     * @param request http request.
     * @param route   the route.
     * @param <P>     parameter type of route.
     * @param <R>     return type of route.
     * @return the parameters parsed.
     * @throws IOException in case of IO error.
     */
    private <P, R> P getRouteParam(HttpServletRequest request, Route<P, R> route) throws IOException {
        P param = null;
        if (route.getParamType() != null && route.getParamType() != Void.class) {
            param = gson.fromJson(request.getReader(), route.getParamType());
        }
        return param;
    }

    @Override
    public String getContentType() {
        return JSON_CONTENT_TYPE;
    }
}
