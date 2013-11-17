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

import fr.ybonnel.simpleweb4j.exception.FatalSimpleWeb4jException;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.eventsource.EventSourceTask;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveEventSourceTask;
import fr.ybonnel.simpleweb4j.handlers.eventsource.ReactiveStream;
import fr.ybonnel.simpleweb4j.handlers.eventsource.Stream;
import fr.ybonnel.simpleweb4j.handlers.filter.AbstractFilter;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import org.eclipse.jetty.continuation.Continuation;
import org.eclipse.jetty.continuation.ContinuationSupport;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.eclipse.jetty.util.MultiPartInputStreamParser;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPOutputStream;

/**
 * Class use to handler all json services (declared by Route or RestResource).
 */
public class SimpleWeb4jHandler extends AbstractHandler {

    /**
     * Size of thread pool for EventSource.
     */
    private static final int EVENT_SOURCE_POOL_SIZE = 10;

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
     * Thread pool for event-source.
     */
    private ScheduledExecutorService executorServiceForEventSource = Executors.newScheduledThreadPool(EVENT_SOURCE_POOL_SIZE);

    /**
     * Add a route.
     *
     * @param httpMethod http method of the route.
     * @param route      route to add.
     */
    public void addRoute(HttpMethod httpMethod, Route route) {
        if (!routes.containsKey(httpMethod)) {
            routes.put(httpMethod, new ArrayList<Route>());
        }
        routes.get(httpMethod).add(route);
    }

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
     * Add a filter.
     * Filters are called in added order.
     *
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
     *
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
        if (request.getContentType() != null && request.getContentType().startsWith("multipart/form-data")) {
            baseRequest.setAttribute(Request.__MULTIPART_CONFIG_ELEMENT, MultiPartInputStreamParser.__DEFAULT_MULTIPART_CONFIG);
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
    <P, R> void processRoute(HttpServletRequest request, HttpServletResponse response,
                             Route<P, R> route, String callback) throws IOException {
        P param = route.getRouteParam(request);
        try {
            beginTransaction();
            RouteParameters parameters = new RouteParameters(
                    route.getRouteParams(request.getPathInfo(), getQueryParameters(request)));
            for (AbstractFilter filter : filters) {
                filter.handle(route, parameters);
            }
            Response<R> handlerResponse = route.handle(param, parameters);
            commitTransaction();
            writeHttpResponse(request, response, handlerResponse, callback, parameters, route.getContentType());
        } catch (HttpErrorException httpError) {
            commitTransaction();
            writeHttpError(response, httpError, route.getContentType());
        } catch (Exception exception) {
            rollBackTransaction();
            writeInternalError(response, exception);
        }
    }

    /**
     * Write http response with exception details.
     *
     * @param response  http response.
     * @param exception exception.
     * @throws IOException in case of IO error.
     */
    private void writeInternalError(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);
        response.getWriter().print(writer.toString());
        response.getWriter().close();
    }

    /**
     * Write http response with HttpError details.
     *
     * @param response    http response.
     * @param httpError   http error.
     * @param contentType content type of response.
     * @throws IOException in case of IO error.
     */
    private void writeHttpError(HttpServletResponse response, HttpErrorException httpError, ContentType contentType) throws IOException {
        response.setStatus(httpError.getStatus());
        if (httpError.getAnswer() != null) {
            response.setContentType(contentType.getValue());
            response.getWriter().print(contentType.convertObject(httpError.getAnswer()));
            response.getWriter().close();
        }
    }

    /**
     * Write http response.
     *
     * @param request         http request.
     * @param response        http response.
     * @param handlerResponse response of route handler.
     * @param callback        callback in case of jsonp.
     * @param parameters      parameters in the routePath.
     * @param contentType     content type of response.
     * @param <R>             return type of route.
     * @throws IOException in case of IO error.
     */
    private <R> void writeHttpResponse(HttpServletRequest request, HttpServletResponse response, Response<R> handlerResponse,
                                       String callback, RouteParameters parameters, ContentType contentType) throws IOException {
        if (handlerResponse.getStatus() != null) {
            response.setStatus(handlerResponse.getStatus());
        } else if (handlerResponse.getAnswer() == null) {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatusWithNoContent());
        } else {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatus());
        }
        if (handlerResponse.getAnswer() != null) {
            if (handlerResponse.isStream()) {
                writeHttpResponseForEventSource(request, response, contentType, handlerResponse);
            } else {
                writeHttpResponse(response, handlerResponse, callback, parameters, contentType, request.getHeaders("Accept-Encoding"));
            }
        }
    }

    /**
     * Does the answer support gzip?
     *
     * @param acceptEncodings all Accept-Encoding headers received.
     * @return true is gzip is supported.
     */
    private boolean supportGzip(Enumeration<String> acceptEncodings) {
        if (acceptEncodings != null) {
            for (String acceptEncoding : Collections.list(acceptEncodings)) {
                for (String encoding : acceptEncoding.split(",")) {
                    if (encoding.equals("gzip")) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Write http response.
     *
     * @param response        http response.
     * @param handlerResponse response of route handler.
     * @param callback        callback in case of jsonp.
     * @param parameters      parameters in the routePath.
     * @param contentType     content type of response.
     * @param acceptEncodings value of header accept-encoding.
     * @param <R>             return type of route.
     * @throws IOException in case of IO error.
     */
    private <R> void writeHttpResponse(HttpServletResponse response,
                                       Response<R> handlerResponse,
                                       String callback,
                                       RouteParameters parameters, ContentType contentType,
                                       Enumeration<String> acceptEncodings) throws IOException {
        response.setContentType(contentType.getValue());


        PrintWriter writer;
        if (supportGzip(acceptEncodings)) {
            response.addHeader("Content-Encoding", "gzip");
            writer = new PrintWriter(new OutputStreamWriter(
                    new GZIPOutputStream(response.getOutputStream()),
                    ContentType.CURRENT_CHARSET));
        } else {
            writer = response.getWriter();
        }

        if (callback != null) {
            writer.print(parameters.getParam(callback));
            writer.print('(');
        }

        writer.print(contentType.convertObject(handlerResponse.getAnswer()));

        if (callback != null) {
            writer.print(");");
        }
        writer.close();
    }

    /**
     * Content type of event-stream.
     */
    private static final String EVENT_STREAM_CONTENT_TYPE = "text/event-stream;charset=" + Charset.defaultCharset().displayName();

    /**
     * Write http response for EventSource case.
     *
     * @param request         http request.
     * @param response        http response.
     * @param contentType     content type of response.
     * @param handlerResponse response of route handler.
     * @throws IOException in case of IO error.
     */
    @SuppressWarnings("unchecked")
    protected void writeHttpResponseForEventSource(HttpServletRequest request, HttpServletResponse response,
                                                 ContentType contentType,
                                                 final Response<?> handlerResponse) throws IOException {
        response.setContentType(EVENT_STREAM_CONTENT_TYPE);
        response.addHeader("Connection", "close");
        response.flushBuffer();
        final Continuation continuation = ContinuationSupport.getContinuation(request);
        // Infinite timeout because the continuation is never resumed,
        // but only completed on close
        continuation.setTimeout(0L);
        continuation.suspend(response);

        if (handlerResponse.getAnswer() instanceof Stream) {
            Response<Stream> streamResponse = (Response<Stream>) handlerResponse;
            executorServiceForEventSource.scheduleAtFixedRate(new EventSourceTask(contentType, streamResponse, continuation),
                    0, streamResponse.getAnswer().timeBeforeNextEvent(), TimeUnit.MILLISECONDS);
        } else if (handlerResponse.getAnswer() instanceof ReactiveStream) {
            ((Response<ReactiveStream>) handlerResponse).getAnswer().setReactiveHandler(
                    new ReactiveEventSourceTask(contentType, continuation));
        } else {
            throw new FatalSimpleWeb4jException("Your answer is an unknown stream");
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
     *
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
     * Find a route for method and path.
     *
     * @param httpMethod http method.
     * @param pathInfo   path.
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
