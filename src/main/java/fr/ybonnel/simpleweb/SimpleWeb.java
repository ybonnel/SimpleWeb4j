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
package fr.ybonnel.simpleweb;


import fr.ybonnel.simpleweb.handlers.HttpMethod;
import fr.ybonnel.simpleweb.handlers.JsonHandler;
import fr.ybonnel.simpleweb.handlers.Route;
import fr.ybonnel.simpleweb.handlers.resource.RestResource;
import fr.ybonnel.simpleweb.model.SimpleEntityManager;
import fr.ybonnel.simpleweb.server.SimpleWebServer;

/**
 * This is the entry point for all your uses of SimpleWeb.<br/>
 *
 * Sample to use SimpleWeb :
 * <p><blockquote><pre>
 * public class HelloWorld {
 *     public static void startServer(int port) {
 *         setPort(port);
 *         setPublicResourcesPath("/fr/ybonnel/simpleweb/samples/helloworld");
 *         start();
 *     }
 *
 *     public static void main(String[] args) {
 *         startServer(9999);
 *     }
 * }
 * </pre></blockquote></p>
 */
public final class SimpleWeb {

    /**
     * Private constructor to avoid instantiation.
     */
    private SimpleWeb() {
    }

    /**
     * Used to know if SimpleWeb is already initialized.
     */
    private static boolean initialized = false;
    /**
     * Used to know if SimpleWebServer is started.
     */
    private static boolean started = false;
    /**
     * The server.
     */
    private static SimpleWebServer server;
    /**
     * Path to public resources in classPath.
     */
    private static String publicResourcesPath = "/public";

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
    private static JsonHandler jsonHandler = new JsonHandler();

    /**
     * Test usage.
     */
    protected static void resetDefaultValues() {
        port = DEFAULT_PORT;
        publicResourcesPath = "/public";
        initialized = false;
    }

    /**
     * Change the port of SimpleWeb (default port is 9999).
     * @param newPort the port you want.
     */
    public static void setPort(int newPort) {
        if (initialized) {
            throw new IllegalStateException("You must set port before settings any route");
        }
        port = newPort;
    }

    /**
     * Change the path to public resources in classPath.<br/>
     * Use : <code>setPublicResourcesPath("/fr/simpleweb/mypublicresources");</code>
     * @param newPublicResourcesPath the path you want.
     */
    public static void setPublicResourcesPath(String newPublicResourcesPath) {
        if (initialized) {
            throw new IllegalStateException("You must set public resources path before settings any route");
        }
        publicResourcesPath = newPublicResourcesPath;
    }

    /**
     * Change the path to your hibernate config file.
     * Simple web have his default hibernate config file which is "fr/ybonnel/simpleweb/model/hibernate.cfg.xml".
     * @param hibernateCfgPath the path you want.
     */
    public static void setHibernateCfgPath(String hibernateCfgPath) {
        if (initialized) {
            throw new IllegalStateException("You must set hibernate cfg path resources path before settings any route");
        }
        SimpleEntityManager.setCfgPath(hibernateCfgPath);
    }

    /**
     * Change the package of your entities.
     * Default value is all.
     * @param entitiesPackage the package you want.
     */
    public static void setEntitiesPackage(String entitiesPackage) {
        if (initialized) {
            throw new IllegalStateException("You must set entities packge resources path before settings any route");
        }
        SimpleEntityManager.setEntitiesPackage(entitiesPackage);
    }

    /**
     * Initialize the server.
     */
    protected static void init() {
        if (!initialized) {
            server = new SimpleWebServer(port, jsonHandler, publicResourcesPath);
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
     * <p><blockquote><pre>
     * get(new Route&lt;Void, String&gt;("/resource", Void.class) {
     *     public Response&lt;String&gt; handle(Void param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;("Hello World");
     *     }
     * });
     * </pre></blockquote></p>
     * @param route your route.
     */
    public static void get(Route route) {
        jsonHandler.addRoute(HttpMethod.GET, route);
    }



    /**
     * Add a new route for POST method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <p><blockquote><pre>
     * post(new Route&lt;String, String&gt;("/resource", String.class) {
     *     public Response&lt;String&gt; handle(String param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;(param);
     *     }
     * });
     * </pre></blockquote></p>
     * @param route your route.
     */
    public static void post(Route route) {
        jsonHandler.addRoute(HttpMethod.POST, route);
    }

    /**
     * Add a new route for PUT method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <p><blockquote><pre>
     * put(new Route&lt;String, String&gt;("/resource", String.class) {
     *     public Response&lt;String&gt; handle(String param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;(param);
     *     }
     * });
     * </pre></blockquote></p>
     * @param route your route.
     */
    public static void put(Route route) {
        jsonHandler.addRoute(HttpMethod.PUT, route);
    }


    /**
     * Add a new route for DELETE method.
     * The request body is transform from json to object and path to param.
     * Use :
     * <p><blockquote><pre>
     * delete(new Route&lt;Void, String&gt;("/resource", Void.class) {
     *     public Response&lt;String&gt; handle(Void param, RouteParameters routeParams) {
     *         return new Response&lt;&gt;("deleted");
     *     }
     * });
     * </pre></blockquote></p>
     * @param route your route.
     */
    public static void delete(Route route) {
        jsonHandler.addRoute(HttpMethod.DELETE, route);
    }

    /**
     * Add a new RestResource.
     * Use :
     * <p><blockquote><pre>
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
     * });
     * </pre></blockquote></p>
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
