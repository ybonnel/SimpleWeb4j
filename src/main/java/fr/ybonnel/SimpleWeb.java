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
package fr.ybonnel;


import fr.ybonnel.handlers.HttpMethod;
import fr.ybonnel.handlers.JsonHandler;
import fr.ybonnel.handlers.Route;
import fr.ybonnel.server.SimpleWebServer;

public class SimpleWeb {

    private static boolean initialized = false;
    private static boolean started = false;
    private static SimpleWebServer server;

    /**
     * Default port is 9999.
     */
    private static int port = 9999;

    private static JsonHandler jsonHandler = new JsonHandler();

    public static void setPort(int newPort) {
        if (initialized) {
            throw new IllegalStateException("You must set port before settings any route");
        }
        port = newPort;
    }


    private static void init() {
        if (!initialized) {
            server = new SimpleWebServer(port, jsonHandler);
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
    }

    public static void get(Route route) {
        jsonHandler.addRoute(HttpMethod.GET, route);
    }



}
