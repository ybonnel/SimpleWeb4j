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
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class use to handler all json services (declared by Route or RestResource).
 */
public class JsonHandler extends AbstractHandler {

    /**
     * Map of routes by HttpMethod.
     */
    private Map<HttpMethod, List<Route>> routes = new HashMap<>();

    /**
     * Gson used to serialize/deserialize json objects.
     */
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX").create();

    /**
     * Add a route.
     * @param httpMethod http method of the route.
     * @param route route to add.
     */
    public void addRoute(HttpMethod httpMethod, Route route) {
        if (!routes.containsKey(httpMethod)) {
            routes.put(httpMethod, new ArrayList<Route>());
        }
        routes.get(httpMethod).add(route);
    }

    /** Handle a request.
     * @param target The target of the request - either a URI or a name.
     * @param baseRequest The original unwrapped request object.
     * @param request The request either as the {@link Request}
     * object or a wrapper of that request.
     * @param response The response as the {@link org.eclipse.jetty.server.Response}
     * object or a wrapper of that request.
     * @throws IOException in case of IO error.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (baseRequest.isHandled()) {
            return;
        }
        Route route = findRoute(request.getMethod(), request.getPathInfo());
        if (route == null) {
            return;
        }

        Object param = getRouteParam(request, route);
        try {
            beginTransaction();
            Response<?> handlerResponse = route.handle(param, new RouteParameters(route.getRouteParams(request.getPathInfo())));
            commitTransaction();
            writeHttpResponse(request, response, handlerResponse);
        } catch (HttpErrorException httpError) {
            commitTransaction();
            writeHttpError(response, httpError);
        } catch (Exception exception) {
            rollBackTransaction();
            writeInternalError(response, exception);
        }
        baseRequest.setHandled(true);

    }


    /**
     * Write http response with exception details.
     * @param response http response.
     * @param exception exception.
     * @throws IOException in case of IO error.
     */
    private void writeInternalError(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);
        response.getOutputStream().print(writer.toString());
        response.getOutputStream().close();
    }

    /**
     * Write http response with HttpError details.
     * @param response http response.
     * @param httpError http error.
     * @throws IOException in case of IO error.
     */
    private void writeHttpError(HttpServletResponse response, HttpErrorException httpError) throws IOException {
        response.setStatus(httpError.getStatus());
        if (httpError.getAnswer() != null) {
            response.setContentType("application/json");
            response.getOutputStream().print(gson.toJson(httpError.getAnswer()));
            response.getOutputStream().close();
        }
    }

    /**
     * Write http response.
     * @param request http request.
     * @param response http response.
     * @param handlerResponse response of route handler.
     * @throws IOException in case of IO error.
     */
    private void writeHttpResponse(HttpServletRequest request, HttpServletResponse response, Response<?> handlerResponse) throws IOException {
        if (handlerResponse.getStatus() != null) {
            response.setStatus(handlerResponse.getStatus());
        } else if (handlerResponse.getAnswer() == null) {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatusWithNoContent());
        } else {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatus());
        }
        if (handlerResponse.getAnswer() != null) {
            response.setContentType("application/json");
            response.getOutputStream().print(gson.toJson(handlerResponse.getAnswer()));
            response.getOutputStream().close();
        }
    }

    /**
     * Rollback current transaction if exists.
     */
    private void rollBackTransaction() {
        closeTransaction(true);
    }

    /**
     * Close current transaction if exists.
     * @param rollback true if you want a rollback, false if you want a commit.
     */
    private void closeTransaction(boolean rollback) {
        if (SimpleEntityManager.getCurrentSession() != null) {
            if (rollback) {
                SimpleEntityManager.getCurrentSession().getTransaction().rollback();
            } else {
                SimpleEntityManager.getCurrentSession().getTransaction().commit();
            }
            SimpleEntityManager.closeSession();
        }
    }

    /**
     * Commit current transaction if exists.
     */
    private void commitTransaction() {
        closeTransaction(false);
    }

    /**
     * Open a new transaction if there is entities.
     */
    private void beginTransaction() {
        if (SimpleEntityManager.hasEntities()) {
            SimpleEntityManager.openSession().beginTransaction();
        }
    }

    /**
     * Parse the parameter of route (content of request body).
     * @param request http request.
     * @param route the route.
     * @return the parameters parsed.
     * @throws IOException in case of IO error.
     */
    private Object getRouteParam(HttpServletRequest request, Route route) throws IOException {
        Object param = null;
        if (route.getParamType() != null && route.getParamType() != Void.class) {
            param = gson.fromJson(request.getReader(), route.getParamType());
        }
        return param;
    }

    /**
     * Find a route for method and path.
     * @param httpMethod http method.
     * @param pathInfo path.
     * @return the route found (null if no route found).
     */
    protected Route findRoute(String httpMethod, String pathInfo) {
        if (!routes.containsKey(HttpMethod.fromValue(httpMethod))) {
            return null;
        }
        for (Route route : routes.get(HttpMethod.fromValue(httpMethod))) {
            if (route.isThisPath(pathInfo)) {
                return route;
            }
        }
        return null;
    }
}
