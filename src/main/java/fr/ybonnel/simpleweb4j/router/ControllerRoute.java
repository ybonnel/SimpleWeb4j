package fr.ybonnel.simpleweb4j.router;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 */
public class ControllerRoute<P, R> extends Route<P, R> {

    /** Controller method. */
    private Method controllerMethod;

    /**
     * Constructor of a route.
     *
     * @param routePath routePath of the route.
     * @param paramType class of the object in request's body.
     */
    public ControllerRoute(String routePath, Class<P> paramType, String controller) throws ClassNotFoundException, NoSuchMethodException {
        super(routePath, paramType);

        int lastDotIndex = controller.lastIndexOf(".");
        if (lastDotIndex < 1) {
            throw new IllegalArgumentException("Controller param is not a controller method.");
        }
        String controllerClassName = controller.substring(0, lastDotIndex);
        String controllerMethodName = controller.substring(lastDotIndex + 1);

        Class<?> controllerClass = Class.forName(controllerClassName);

        if (Void.class.equals(paramType)) {
            controllerMethod = controllerClass.getMethod(controllerMethodName, RouteParameters.class);
        }
        else {
            controllerMethod = controllerClass.getMethod(controllerMethodName, paramType, RouteParameters.class);
        }
    }

    /**
     * Invokes the controller static method to compute the HTTP Response.
     *
     * @param param the parameter object in request's body.
     * @param routeParams parameters in the routePath.
     * @return The request response.
     * @throws HttpErrorException If an error occured.
     */
    @Override
    public Response<R> handle(P param, RouteParameters routeParams) throws HttpErrorException {
        try {
            R result = null;
            if (Void.class.equals(getParamType())) {
                result = (R) controllerMethod.invoke(null, routeParams);
            }
            else {
                result = (R) controllerMethod.invoke(null, param, routeParams);
            }
            return new Response<R>(result);
        } catch (Exception e) {
            e.printStackTrace();
           throw new HttpErrorException(500, e);
        }
    }
}
