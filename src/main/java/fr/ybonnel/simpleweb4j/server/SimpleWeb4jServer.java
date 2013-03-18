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
package fr.ybonnel.simpleweb4j.server;


import fr.ybonnel.simpleweb4j.exception.FatalSimpleWeb4jException;
import fr.ybonnel.simpleweb4j.handlers.JsonHandler;
import org.mortbay.jetty.Handler;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.handler.HandlerList;
import org.mortbay.jetty.handler.ResourceHandler;
import org.mortbay.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Server part of SimpleWeb4j.
 * <b>Don't use it directly</b>, use the methods of {@link fr.ybonnel.simpleweb4j.SimpleWeb4j}.
 */
public class SimpleWeb4jServer {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleWeb4jServer.class);

    /**
     * Jetty server.
     */
    private Server jettyServer;

    /**
     * Constructor for unit test.
     * @param jettyServer the Jetty server.
     */
    protected SimpleWeb4jServer(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    /**
     * Constructor.
     * @param port Http port to use.
     * @param jsonHandler the handler for json services.
     * @param publicResourcesPath path to public resources.
     */
    public SimpleWeb4jServer(int port, JsonHandler jsonHandler, String publicResourcesPath) {
        jettyServer = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource(publicResourcesPath));
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        ResourceHandler internalResourceHandler = new ResourceHandler();
        internalResourceHandler.setBaseResource(Resource.newClassPathResource("/fr/ybonnel/simpleweb4j/public"));
        internalResourceHandler.setWelcomeFiles(new String[]{"index.html"});

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{jsonHandler, resourceHandler, internalResourceHandler});

        jettyServer.setHandler(handlers);
    }

    /**
     * Start the server.
     * @param waitStop true is you want the method wait the server stop.
     */
    public void start(boolean waitStop) {
        try {
            LOGGER.info("Starting SimpleWeb4j server");
            jettyServer.start();
            if (waitStop) {
                jettyServer.join();
            }
        } catch (Exception e) {
            throw new FatalSimpleWeb4jException(e);
        }
    }

    /**
     * Stop the server.
     */
    public void stop() {
        try {
            LOGGER.info("Stopping SimpleWeb4j server");
            jettyServer.stop();
        } catch (Exception e) {
            throw new FatalSimpleWeb4jException(e);
        }
    }
}
