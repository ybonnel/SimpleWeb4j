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
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.util.resource.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     *
     * @param jettyServer the Jetty server.
     */
    protected SimpleWeb4jServer(Server jettyServer) {
        this.jettyServer = jettyServer;
    }

    /**
     * Constructor.
     *
     * @param port                Http port to use.
     * @param publicResourcesPath path to public resources.
     * @param externalPublicResourcesPath path to external public resources.
     * @param specificHandlers    handlers to add to server.
     */
    public SimpleWeb4jServer(int port, String publicResourcesPath,
                             String externalPublicResourcesPath,
                             List<Handler> specificHandlers) {
        jettyServer = new Server(port);

        ResourceHandler resourceHandler = new ResourceHandler();
        resourceHandler.setBaseResource(Resource.newClassPathResource(publicResourcesPath));
        resourceHandler.setWelcomeFiles(new String[]{"index.html"});

        ResourceHandler internalResourceHandler = new ResourceHandler();
        internalResourceHandler.setBaseResource(Resource.newClassPathResource("/fr/ybonnel/simpleweb4j/public"));
        internalResourceHandler.setWelcomeFiles(new String[]{"index.html"});

        HandlerList handlers = new HandlerList();

        Collections.reverse(specificHandlers);

        List<Handler> handlersList = new ArrayList<>(specificHandlers);
        handlersList.add(resourceHandler);

        if (externalPublicResourcesPath != null) {
            ResourceHandler externalResourceHandler = new ResourceHandler();
            try {
                externalResourceHandler.setBaseResource(Resource.newResource(new File(externalPublicResourcesPath)));
            } catch (IOException e) {
                throw new FatalSimpleWeb4jException(e);
            }
            externalResourceHandler.setWelcomeFiles(new String[]{"index.html"});
            handlersList.add(externalResourceHandler);
        }

        handlersList.add(internalResourceHandler);

        handlers.setHandlers(handlersList.toArray(new Handler[handlersList.size()]));

        jettyServer.setHandler(handlers);
    }

    /**
     * Start the server.
     *
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
