package fr.ybonnel.simpleweb4j.handlers;

import com.google.common.annotations.VisibleForTesting;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.filter.AbstractFilter;
import org.eclipse.jetty.server.Request;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;

public class TextHandler extends AbstractSimpleHandler {

    public static final String PLAIN_TEXT_CONTENT_TYPE = "text/plain;charset=" + Charset.defaultCharset().displayName();

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        Route<?, ?> route = findRoute(request.getMethod(), request.getPathInfo());
        if (baseRequest.isHandled() || route == null) {
            return;
        }
        processRoute(request, response, route);
        baseRequest.setHandled(true);

    }

    @VisibleForTesting
    <P, R> void processRoute(HttpServletRequest request, HttpServletResponse response, Route<P, R> route) throws IOException {
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
            writeHttpResponse(request, response, handlerResponse, null, parameters, handlerResponse.getAnswer().toString());
        } catch (HttpErrorException httpError) {
            commitTransaction();
            writeHttpError(response, httpError, httpError.getAnswer().toString());
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
            return (P) request.getReader();
            //param = gson.fromJson(request.getReader(), route.getParamType());
        }
        return param;
    }

    @Override
    public String getContentType() {
        return PLAIN_TEXT_CONTENT_TYPE;
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

