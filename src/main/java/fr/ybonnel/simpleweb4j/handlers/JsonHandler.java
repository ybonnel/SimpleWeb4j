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
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

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

    /**
     * Handle a request.
     * @param target The target of the request - either a URI or a name.
     * @param request The request either as the {@link Request}
     * object or a wrapper of that request. The {@link HttpConnection#getCurrentConnection()}
     * method can be used access the Request object if required.
     * @param response The response as the {@link org.mortbay.jetty.Response}
     * object or a wrapper of that request. The {@link HttpConnection#getCurrentConnection()}
     * method can be used access the Response object if required.
     * @param dispatch The dispatch mode: {@link #REQUEST}, {@link #FORWARD}, {@link #INCLUDE}, {@link #ERROR}
     * @throws IOException in case of I/O error.
     */
    @SuppressWarnings("unchecked")
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException {
        Request baseRequest = request instanceof Request ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        if (baseRequest.isHandled()) {
            return;
        }
        Route route = findRoute(request.getMethod(), request.getPathInfo());
        if (route == null) {
            return;
        }

        Object param = null;
        if (route.getParamType() != null && route.getParamType() != Void.class) {
            param = gson.fromJson(request.getReader(), route.getParamType());
        }
        if (SimpleEntityManager.hasEntities()) {
            SimpleEntityManager.openSession().beginTransaction();
        }
        try {
            Response<?> handlerResponse = route.handle(param, new RouteParameters(route.getRouteParams(request.getPathInfo())));
            if (SimpleEntityManager.hasEntities()) {
                SimpleEntityManager.getCurrentSession().getTransaction().commit();
                SimpleEntityManager.closeSession();
            }
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
        } catch (HttpErrorException httpError) {
            response.setStatus(httpError.getStatus());
            if (httpError.getAnswer() != null) {
                response.setContentType("application/json");
                response.getOutputStream().print(gson.toJson(httpError.getAnswer()));
                response.getOutputStream().close();
            }
        } catch (Exception exception) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exception.printStackTrace(printWriter);
            response.getOutputStream().print(writer.toString());
            response.getOutputStream().close();
        } finally {
            if (SimpleEntityManager.hasEntities() && SimpleEntityManager.getCurrentSession() != null) {
                SimpleEntityManager.getCurrentSession().getTransaction().rollback();
                SimpleEntityManager.closeSession();
            }
        }
        baseRequest.setHandled(true);

    }

    /**
     * Find a route for method and path.
     * @param httpMethod http method.
     * @param pathInfo path.
     * @return the route found (null if no route found).
     */
    private Route findRoute(String httpMethod, String pathInfo) {
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
