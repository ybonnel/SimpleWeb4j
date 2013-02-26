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
package fr.ybonnel.server;


import fr.ybonnel.handlers.JsonHandler;
import fr.ybonnel.exception.FatalSimpleWebException;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.resource.Resource;

public class SimpleWebServer {

    private Server jettyServer;

    public SimpleWebServer(int port, JsonHandler jsonHandler) {
        jettyServer = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        Resource publicResources = Resource.newClassPathResource("/public");
        resourceHandler.setBaseResource(publicResources);
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{jsonHandler, resourceHandler});

        jettyServer.setHandler(handlers);
    }

    public void start() {
        try {
            jettyServer.start();
            jettyServer.join();
        } catch (Exception e) {
            throw new FatalSimpleWebException(e);
        }
    }

    public void stop() {
        try {
            jettyServer.stop();
        } catch (Exception e) {
            throw new FatalSimpleWebException(e);
        }
    }
}
