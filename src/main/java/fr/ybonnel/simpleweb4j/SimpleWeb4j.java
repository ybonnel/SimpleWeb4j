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
package fr.ybonnel.simpleweb4j;


import fr.ybonnel.simpleweb4j.handlers.FunctionnalRoute;
import fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteUtil;
import fr.ybonnel.simpleweb4j.handlers.HttpMethod;
import fr.ybonnel.simpleweb4j.handlers.SimpleWeb4jHandler;
import fr.ybonnel.simpleweb4j.handlers.LessCompilerHandler;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.filter.AbstractFilter;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.server.SimpleWeb4jServer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.handler.AbstractHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <p>This is the entry point for all your uses of SimpleWeb4j.</p>
 *
 * Sample to use SimpleWeb4j :
 * <pre>{@code
 * public class HelloWorld {
 *     public static void startServer(int port) {
 *         setPort(port);
 *         setPublicResourcesPath("/fr/ybonnel/simpleweb4j/samples/helloworld");
 *         start();
 *     }
 *
 *     public static void main(String[] args) {
 *         startServer(9999);
 *     }
 * } }</pre>
 */
public final class SimpleWeb4j {

    /**
     * Private constructor to avoid instantiation.
     */
    private SimpleWeb4j() {
    }

    /**
     * Used to know if SimpleWeb4j is already initialized.
     */
    private static boolean initialized = false;
    /**
     * Used to know if SimpleWeb4jServer is started.
     */
    private static boolean started = false;
    /**
     * The server.
     */
    private static SimpleWeb4jServer server;
    /**
     * Path to public resources in classPath.
     */
    private static String publicResourcesPath = "/public";

    /**
     * Path to public resources in external (filesystem).
     */
    private static String externalPublicResourcesPath = null;

    /**
     * Default http port.
     */
    public static final int DEFAULT_PORT = 9999;
    /**
     * Http port.
     */
    private static int port = DEFAULT_PORT;

    /**
     * Handler for all request others than static files.
     */
    private static SimpleWeb4jHandler simpleWeb4jHandler = new SimpleWeb4jHandler();
    /**
     * Handler to compile less.
     */
    private static LessCompilerHandler lessCompilerHandler = new LessCompilerHandler();
    /**
     * List of all internal handlers.
     */
    private static List<AbstractHandler> simpleWeb4jHandlers = Arrays.asList(simpleWeb4jHandler, lessCompilerHandler);

    /**
     * Test usage.
     */
    protected static void resetDefaultValues() {
        port = DEFAULT_PORT;
        publicResourcesPath = "/public";
        externalPublicResourcesPath = null;
        lessCompilerHandler.setPublicResourcePath(publicResourcesPath);
        initialized = false;
        handlers = new ArrayList<Handler>(simpleWeb4jHandlers);
        simpleWeb4jHandler.resetFilters();
        setEntitiesClasses();
    }

    /**
     * Change the port of SimpleWeb4j (default port is 9999).
     * @param newPort the port you want.
     */
    public static void setPort(int newPort) {
        if (initialized) {
            throw new IllegalStateException("You must set port before settings any route");
        }
        port = newPort;
    }

    /**
     * <p>Change the path to public resources in classPath.</p>
     * Use : <code>setPublicResourcesPath("/fr/simpleweb4j/mypublicresources");</code>
     * @param newPublicResourcesPath the path you want.
     */
    public static void setPublicResourcesPath(String newPublicResourcesPath) {
        if (initialized) {
            throw new IllegalStateException("You must set public resources path before settings any route");
        }
        publicResourcesPath = newPublicResourcesPath;
        lessCompilerHandler.setPublicResourcePath(publicResourcesPath);
    }

    /**
     * <p>Change the path to public resources external (in filesystem).</p>
     * Use : <code>setPublicResourcesPath("/var/www/mysite");</code>
     * @param newExternalPublicResourcesPath the path you want.
     */
    public static void setExternalPublicResourcesPath(String newExternalPublicResourcesPath) {
        if (initialized) {
            throw new IllegalStateException("You must set public resources path before settings any route");
        }
        externalPublicResourcesPath = newExternalPublicResourcesPath;
    }

    /**
     * Change the path to your hibernate config file.
     * Simple web have his default hibernate config file which is "fr/ybonnel/simpleweb4j/model/hibernate.cfg.xml".
     * @param hibernateCfgPath the path you want.
     */
    public static void setHibernateCfgPath(String hibernateCfgPath) {
        if (initialized) {
            throw new IllegalStateException("You must set hibernate cfg path resources path before settings any route");
        }
        SimpleEntityManager.setCfgPath(hibernateCfgPath);
    }

    /**
     * Set entities classes for hibernate configuration.
     * @param entitiesClasses all entities.
     */
    public static void setEntitiesClasses(Class<?> ... entitiesClasses) {
        if (initialized) {
            throw new IllegalStateException("You must set entities classes before settings any route");
        }
        SimpleEntityManager.setEntitiesClasses(Arrays.asList(entitiesClasses));
    }

    /**
     * Handlers for jetty server.
     */
    private static List<Handler> handlers = new ArrayList<Handler>(simpleWeb4jHandlers);

    /**
     * Add you specific handler.
     * @param handler your handler.
     */
    public static void addSpecificHandler(Handler handler) {
        if (initialized) {
            throw new IllegalStateException("You must add your handlers before settings any route");
        }
        handlers.add(handler);
    }

    /**
     * Add a filter.
     * Filters are called in the add order.
     * @param filter filter to add.
     */
    public static void addFilter(AbstractFilter filter) {
        simpleWeb4jHandler.addFilter(filter);
    }

    /**
     * Initialize the server.
     */
    protected static void init() {
        if (!initialized) {
            server = new SimpleWeb4jServer(port, publicResourcesPath, externalPublicResourcesPath, handlers);
            initialized = true;
        }
    }

    /**
     * Start the server.
     * This method wait the stop of server to finish.
     */
    public static void start() {
        start(true);
    }

    /**
     * Start the server.
     * @param waitStop true if you want wait the stop of server, false otherwise.
     */
    public static void start(boolean waitStop) {
        init();
        started = true;
        server.start(waitStop);
    }

    /**
     * Stop the server.
     */
    public static void stop() {
        if (!started) {
            throw new IllegalStateException("You must start server before stop it!");
        }
        server.stop();
        initialized = false;
        started = false;
    }

    /**
     * Add a new route for GET method.
     * Use :
     * <pre>{@code
     * get(new Route&lt;Void, String&gt;("/resource", Void.class) {
     *     public Response&lt;String&gt; handle(Void param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;("Hello World");
     *     }
     * }); }</pre>
     * @param route your route.
     */
    public static void get(Route route) {
        simpleWeb4jHandler.addRoute(HttpMethod.GET, route);
    }


    /**
     * Add a new route for GET method.
     * Use :
     * <pre>{@code
     * get("/resource", (param, routeParams) -> new Response&lt;&gt;("Hello World"));
     * }</pre>
     * @param routePath path of route.
     * @param route your handle method.
     * @param <R> type of the object to serialize in response body.
     */
    public static <R> void get(String routePath, FunctionnalRoute<Void, R> route) {
        get(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, Void.class));
    }

    /**
     * Add a new route for GET method with jsonp support.
     * Use :
     * <pre>{@code
     * jsonp("CALLBACK", new Route&lt;Void, String&gt;("/resource", Void.class) {
     *     public Response&lt;String&gt; handle(Void param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;("Hello World");
     *     }
     * }); }</pre>
     * @param callbackName name of query param with callback function name
     * @param route your route.
     */
    public static void jsonp(String callbackName, Route route) {
        simpleWeb4jHandler.addJsonpRoute(route, callbackName);
    }


    /**
     * Add a new route for GET method with jsonp support.
     * Use :
     * <pre>{@code
     * jsonp("CALLBACK", "/resource", (param, routeParams) -> new Response&lt;&gt;("Hello World"));
     * }</pre>
     * @param callbackName name of query param with callback function name
     * @param routePath path of route.
     * @param route your handle method.
     * @param <R> type of the object to serialize in response body.
     */
    public static <R> void jsonp(String callbackName, String routePath, FunctionnalRoute<Void, R> route) {
        jsonp(callbackName, FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, Void.class));
    }


    /**
     * Add a new route for POST method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * post(new Route&lt;String, String&gt;("/resource", String.class) {
     *     public Response&lt;String&gt; handle(String param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;(param);
     *     }
     * }); }</pre>
     * @param route your route.
     */
    public static void post(Route route) {
        simpleWeb4jHandler.addRoute(HttpMethod.POST, route);
    }

    /**
     * Add a new route for POST method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * post("/resource", String.class, (param, routeParams) -> new Response&lt;&gt;(param));
     * }</pre>
     * @param routePath routePath of the route.
     * @param paramType class of the object in request's body.
     * @param route your handle method.
     * @param <P> type of the object in request's body.
     * @param <R> type of the object to serialize in response body.
     */
    public static <P, R> void post(String routePath, Class<P> paramType, FunctionnalRoute<P, R> route) {
        post(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, paramType));
    }

    /**
     * Add a new route for POST method.
     * Use :
     * <pre>{@code
     * post("/resource", (param, routeParams) -> new Response&lt;&gt;("Hello World"));
     * }</pre>
     * @param routePath routePath of the route.
     * @param route your handle method.
     * @param <R> type of the object to serialize in response body.
     */
    public static <R> void post(String routePath, FunctionnalRoute<Void, R> route) {
        post(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, Void.class));
    }

    /**
     * Add a new route for PUT method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * put(new Route&lt;String, String&gt;("/resource", String.class) {
     *     public Response&lt;String&gt; handle(String param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;(param);
     *     }
     * }); }</pre>
     * @param route your route.
     */
    public static void put(Route route) {
        simpleWeb4jHandler.addRoute(HttpMethod.PUT, route);
    }


    /**
     * Add a new route for PUT method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * put("/resource", String.class, (param, routeParams) -> new Response&lt;&gt;(param));
     * }</pre>
     * @param routePath routePath of the route.
     * @param paramType class of the object in request's body.
     * @param route your handle method.
     * @param <P> type of the object in request's body.
     * @param <R> type of the object to serialize in response body.
     */
    public static <P, R> void put(String routePath, Class<P> paramType, FunctionnalRoute<P, R> route) {
        put(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, paramType));
    }

    /**
     * Add a new route for PUT method.
     * Use :
     * <pre>{@code
     * put("/resource", (param, routeParams) -> new Response&lt;&gt;("Hello world"));
     * }</pre>
     * @param routePath routePath of the route.
     * @param route your handle method.
     * @param <R> type of the object to serialize in response body.
     */
    public static <R> void put(String routePath, FunctionnalRoute<Void, R> route) {
        put(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, Void.class));
    }


    /**
     * Add a new route for DELETE method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * delete(new Route&lt;Void, String&gt;("/resource", Void.class) {
     *     public Response&lt;String&gt; handle(Void param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;("deleted");
     *     }
     * }); }</pre>
     * @param route your route.
     */
    public static void delete(Route route) {
        simpleWeb4jHandler.addRoute(HttpMethod.DELETE, route);
    }

    /**
     * Add a new route for DELETE method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <pre>{@code
     * delete("/resource", String.class, (param, routeParams) -> new Response&lt;&gt;(param));
     * }</pre>
     * @param routePath routePath of the route.
     * @param paramType class of the object in request's body.
     * @param route your handle method.
     * @param <P> type of the object in request's body.
     * @param <R> type of the object to serialize in response body.
     */
    public static <P, R> void delete(String routePath, Class<P> paramType, FunctionnalRoute<P, R> route) {
        delete(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, paramType));
    }

    /**
     * Add a new route for DELETE method.
     * Use :
     * <pre>{@code
     * delete("/resource", String.class, (param, routeParams) -> new Response&lt;&gt;(param));
     * }</pre>
     * @param routePath routePath of the route.
     * @param route your handle method.
     * @param <R> type of the object to serialize in response body.
     */
    public static <R> void delete(String routePath, FunctionnalRoute<Void, R> route) {
        delete(FunctionnalRouteUtil.functionnalRouteToRoute(route, routePath, Void.class));
    }

    /**
     * Add a new RestResource.
     * Use :
     * <pre>{@code
     * resource(new RestResource&lt;String&gt;("string", String.class) {
     *     {@literal @Override}
     *     public String getById(String id) throws HttpErrorException {
     *         return "myResource";
     *     }
     *
     *     {@literal @Override}
     *     public List&lt;String&gt; getAll() throws HttpErrorException {
     *         return new ArrayList&lt;String&gt;();
     *     }
     *
     *     {@literal @Override}
     *     public void update(String id, String resource) throws HttpErrorException {
     *     }
     *
     *     {@literal @Override}
     *     public void create(String resource) throws HttpErrorException {
     *     }
     *
     *     {@literal @Override}
     *     public void delete(String id) throws HttpErrorException {
     *     }
     * }); }</pre>
     *
     * @param restResource your REST resource.
     */
    public static void resource(RestResource restResource) {
        get(restResource.routeGetById());
        get(restResource.routeGetAll());
        post(restResource.routeCreate());
        delete(restResource.routeDelete());
        put(restResource.routeUpdate());
    }
}
