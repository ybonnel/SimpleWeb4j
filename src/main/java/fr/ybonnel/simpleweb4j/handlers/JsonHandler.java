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
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.router.ControllerRoute;
import fr.ybonnel.simpleweb4j.util.StringUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
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
     * Map of routes for jsonp with callbacks.
     */
    private Map<Route, String> jsonpRoutes = new HashMap<>();

    /**
     * List of filters.
     */
    private List<AbstractFilter> filters = new ArrayList<>();

    /**
     * Gson used to serialize/deserialize json objects.
     */
    private Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXX").create();

    /**
     *
     * @param inputStream
     * @throws IOException
     * @throws NoSuchMethodException
     * @throws ClassNotFoundException
     */
    public void loadRoutes(InputStream inputStream) throws IOException, NoSuchMethodException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        String line = null;
        while ((line = reader.readLine()) != null){
            HttpMethod httpMethod = null;
            Class<?> paramType = null;
            String routePath = null;
            String controller = null;

            // Read Http Method
            line = line.trim();
            int firstBlankCharacterIndex = StringUtils.getIndexOfBlank(line);
            httpMethod = HttpMethod.valueOf(line.substring(0, firstBlankCharacterIndex).toUpperCase());

            // Read Parameter type
            line = line.substring(firstBlankCharacterIndex).trim();
            firstBlankCharacterIndex = StringUtils.getIndexOfBlank(line);
            paramType = Class.forName(line.substring(0, firstBlankCharacterIndex));

            // Read route routePath
            line = line.substring(firstBlankCharacterIndex).trim();
            firstBlankCharacterIndex = StringUtils.getIndexOfBlank(line);
            routePath = line.substring(0, firstBlankCharacterIndex);

            // Read controller method
            line = line.substring(firstBlankCharacterIndex).trim();
            controller = line;

            // Create and add route.
            ControllerRoute<?,?> route = new ControllerRoute(routePath, paramType, controller);
            addRoute(httpMethod, route);
        }

    }
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
     * Add a route with jsonp support.
     * @param callbackName name of query param of callback.
     * @param route route to add.
     */
    public void addJsonpRoute(Route route, String callbackName) {
        jsonpRoutes.put(route, callbackName);
    }

    /**
     * Add a filter.
     * Filters are called in added order.
     * @param filter filter to add.
     */
    public void addFilter(AbstractFilter filter) {
        filters.add(filter);
    }

    /**
     * Reset filters to default (for test uses).
     */
    public void resetFilters() {
        filters.clear();
    }

    /**
     * Get parameters from query.
     * @param request the request.
     * @return map of query parameters.
     */
    private Map<String, String> getQueryParameters(HttpServletRequest request) {
        Map<String, String> queryParameters = new HashMap<>();
        Enumeration<String> parametersName = request.getParameterNames();
        while (parametersName.hasMoreElements()) {
            String name = parametersName.nextElement();
            queryParameters.put(name, request.getParameter(name));
        }
        return queryParameters;
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
     * @param request The request either as the {@link Request}
     * object or a wrapper of that request.
     * @param response The response as the {@link org.eclipse.jetty.server.Response}
     * object or a wrapper of that request.
     * @param route route to apply.
     * @param callback callback in case of jsonp.
     * @param <P> parameter type of route.
     * @param <R> return type of route.
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
            writeHttpResponse(request, response, handlerResponse, callback, parameters);
        } catch (HttpErrorException httpError) {
            commitTransaction();
            writeHttpError(response, httpError);
        } catch (Exception exception) {
            rollBackTransaction();
            writeInternalError(response, exception);
        }
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
            response.setContentType("application/json;charset=" + Charset.defaultCharset().displayName());
            response.getOutputStream().print(gson.toJson(httpError.getAnswer()));
            response.getOutputStream().close();
        }
    }

    /**
     * Write http response.
     *
     * @param request http request.
     * @param response http response.
     * @param handlerResponse response of route handler.
     * @param callback callback in case of jsonp.
     * @param parameters parameters in the routePath.
     * @param <R> return type of route.
     * @throws IOException in case of IO error.
     */
    private <R> void writeHttpResponse(HttpServletRequest request, HttpServletResponse response, Response<R> handlerResponse,
                                       String callback, RouteParameters parameters) throws IOException {
        if (handlerResponse.getStatus() != null) {
            response.setStatus(handlerResponse.getStatus());
        } else if (handlerResponse.getAnswer() == null) {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatusWithNoContent());
        } else {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatus());
        }
        if (handlerResponse.getAnswer() != null) {
            response.setContentType("application/json;charset=" + Charset.defaultCharset().displayName());
            if (callback != null) {
                response.getOutputStream().print(parameters.getParam(callback));
                response.getOutputStream().print('(');
            }
            response.getOutputStream().print(gson.toJson(handlerResponse.getAnswer()));
            if (callback != null) {
                response.getOutputStream().print(");");
            }
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
     * @param <P> parameter type of route.
     * @param <R> return type of route.
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

    /**
     * Find a route for method and path.
     * @param httpMethod http method.
     * @param pathInfo path.
     * @return the route found (null if no route found).
     */
    protected Route<?, ?> findRoute(String httpMethod, String pathInfo) {
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
