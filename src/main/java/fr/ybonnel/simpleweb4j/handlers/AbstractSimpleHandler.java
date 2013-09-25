package fr.ybonnel.simpleweb4j.handlers;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.filter.AbstractFilter;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.*;

public abstract class AbstractSimpleHandler extends AbstractHandler {

    /**
     * List of filters.
     */
    protected List<AbstractFilter> filters = new ArrayList<>();

    /**
     * Map of routes by HttpMethod.
     */
    protected Map<HttpMethod, List<Route>> routes = new HashMap<>();

    public abstract String getContentType();

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
     * Write http response with exception details.
     *
     * @param response  http response.
     * @param exception exception.
     * @throws java.io.IOException in case of IO error.
     */
    protected void writeInternalError(HttpServletResponse response, Exception exception) throws IOException {
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        StringWriter writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        exception.printStackTrace(printWriter);
        response.getOutputStream().print(writer.toString());
        response.getOutputStream().close();
    }

    /**
     * Close current transaction if exists.
     *
     * @param rollback true if you want a rollback, false if you want a commit.
     */
    protected void closeTransaction(boolean rollback) {
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
     * Rollback current transaction if exists.
     */
    protected void rollBackTransaction() {
        closeTransaction(true);
    }

    /**
     * Commit current transaction if exists.
     */
    protected void commitTransaction() {
        closeTransaction(false);
    }

    /**
     * Open a new transaction if there is entities.
     */
    protected void beginTransaction() {
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
     * Get parameters from query.
     *
     * @param request the request.
     * @return map of query parameters.
     */
    protected Map<String, String> getQueryParameters(HttpServletRequest request) {
        Map<String, String> queryParameters = new HashMap<>();
        Enumeration<String> parametersName = request.getParameterNames();
        while (parametersName.hasMoreElements()) {
            String name = parametersName.nextElement();
            queryParameters.put(name, request.getParameter(name));
        }
        return queryParameters;
    }

    /**
     * Write http response with HttpError details.
     *
     * @param response http response.
     * @param httpError http error.
     * @param content
     * @throws java.io.IOException in case of IO error.
     */
    protected void writeHttpError(HttpServletResponse response, HttpErrorException httpError, String content) throws IOException {
        response.setStatus(httpError.getStatus());
        if (httpError.getAnswer() != null) {
            response.setContentType(getContentType());
            response.getOutputStream().print(content);
            response.getOutputStream().close();
        }
    }

    /**
     * Write http response.
     *
     *
     * @param request         http request.
     * @param response        http response.
     * @param handlerResponse response of route handler.
     * @param callback        callback in case of jsonp.
     * @param parameters      parameters in the routePath.
     * @param content
     * @throws java.io.IOException in case of IO error.
     */
    protected <R> void writeHttpResponse(HttpServletRequest request, HttpServletResponse response, Response<R> handlerResponse,
                                         String callback, RouteParameters parameters, String content) throws IOException {
        if (handlerResponse.getStatus() != null) {
            response.setStatus(handlerResponse.getStatus());
        } else if (handlerResponse.getAnswer() == null) {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatusWithNoContent());
        } else {
            response.setStatus(HttpMethod.fromValue(request.getMethod()).getDefaultStatus());
        }
        if (handlerResponse.getAnswer() != null) {
            response.setContentType(getContentType());
            if (callback != null) {
                response.getOutputStream().print(parameters.getParam(callback));
                response.getOutputStream().print('(');
            }
            response.getOutputStream().print(content);
            if (callback != null) {
                response.getOutputStream().print(");");
            }
            response.getOutputStream().close();
        }
    }
}
