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

public class SimpleWeb {

    private static boolean initialized = false;
    private static boolean started = false;
    private static SimpleWebServer server;
    private static String publicResourcesPath = "/public";

    /**
     * Default port is 9999.
     */
    private static int port = 9999;

    private static JsonHandler jsonHandler = new JsonHandler();

    /**
     * Test usage.
     */
    public static void resetDefaultValues() {
        port = 9999;
        publicResourcesPath = "/public";
    }

    public static void setPort(int newPort) {
        if (initialized) {
            throw new IllegalStateException("You must set port before settings any route");
        }
        port = newPort;
    }

    public static void setPublicResourcesPath(String newPublicResourcesPath) {
        if (initialized) {
            throw new IllegalStateException("You must set public resources path before settings any route");
        }
        publicResourcesPath = newPublicResourcesPath;
    }

    public static void setHibernateCfgPath(String hibernateCfgPath) {
        if (initialized) {
            throw new IllegalStateException("You must set hibernate cfg path resources path before settings any route");
        }
        SimpleEntityManager.setCfgPath(hibernateCfgPath);
    }

    public static void setEntitiesPackage(String entitiesPackge) {
        if (initialized) {
            throw new IllegalStateException("You must set entities packge resources path before settings any route");
        }
        SimpleEntityManager.setEntitiesPackage(entitiesPackge);
    }


    private static void init() {
        if (!initialized) {
            server = new SimpleWebServer(port, jsonHandler, publicResourcesPath);
            initialized = true;
        }
    }

    public static void start() {
        init();
        started = true;
        server.start();
    }

    public static void stop() {
        if (!started) {
            throw new IllegalStateException("You must start server before stop it!");
        }
        server.stop();
        initialized = false;
        started = false;
    }

    public static void get(Route route) {
        jsonHandler.addRoute(HttpMethod.GET, route);
    }

    public static void post(Route route) {
        jsonHandler.addRoute(HttpMethod.POST, route);
    }

    public static void put(Route route) {
        jsonHandler.addRoute(HttpMethod.PUT, route);
    }

    public static void delete(Route route) {
        jsonHandler.addRoute(HttpMethod.DELETE, route);
    }

    public static void resource(RestResource restResource) {
        get(restResource.routeGetById());
        get(restResource.routeGetAll());
        post(restResource.routeCreate());
        delete(restResource.routeDelete());
        put(restResource.routeUpdate());
    }
}
