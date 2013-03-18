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

import fr.ybonnel.simpleweb4j.exception.FatalSimpleWebException;
import org.junit.Before;
import org.junit.Test;
import org.mortbay.jetty.Server;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class SimpleWebServerUnitTest {

    private SimpleWeb4jServer simpleWeb4jServer;
    private Server jettyServer;
    private ServerInterface serverInterface;

    private static interface ServerInterface {
        void start();
        void join();
        void stop();
    }

    @Before
    public void setup() {
        serverInterface = mock(ServerInterface.class);
        jettyServer = new Server(){
            @Override
            protected void doStart() throws Exception {
                serverInterface.start();
            }

            @Override
            protected void doStop() throws Exception {
                serverInterface.stop();
            }

            @Override
            public void join() throws InterruptedException {
                serverInterface.join();
            }
        };
        simpleWeb4jServer = new SimpleWeb4jServer(jettyServer);
    }

    @Test
    public void testStartWithWaitStop() throws Exception {

        simpleWeb4jServer.start(true);

        verify(serverInterface).start();
        verify(serverInterface).join();
    }

    @Test(expected = FatalSimpleWebException.class)
    public void testStartWithFatalError() {

        doThrow(new NullPointerException()).when(serverInterface).start();

        simpleWeb4jServer.start(false);

    }

    @Test(expected = FatalSimpleWebException.class)
    public void testStopWithFatalError() {

        simpleWeb4jServer.start(true);

        doThrow(new NullPointerException()).when(serverInterface).stop();

        simpleWeb4jServer.stop();
    }

}
