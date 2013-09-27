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

import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;
import fr.ybonnel.simpleweb4j.handlers.eventsource.Stream;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;

public class EventSourceTest {

    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        get(new Route<Void, Stream<String>>("/eventsource", Void.class) {
            @Override
            public Response<Stream<String>> handle(Void param, RouteParameters routeParams) {
                return new Response<Stream<String>>(new Stream<String>(){

                    int index = 0;

                    @Override
                    public String next() throws IOException {
                        if (index == 10) {
                            throw new IOException("end of event-source");
                        }
                        return Integer.toString(index++);
                    }

                    @Override
                    public int timeBeforeNextEvent() {
                        return 1;
                    }
                });
            }
        });

        start(false);
    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_serve_event_source() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/eventsource");
        assertEquals("text/event-stream;charset=" + Charset.defaultCharset().displayName(), response.contentType);
        StringBuilder expectedResponse = new StringBuilder();
        for (int index = 0; index < 10; index++) {
            expectedResponse.append("data: \"");
            expectedResponse.append(index);
            expectedResponse.append("\"\n\n");
        }
        assertEquals(expectedResponse.toString(), response.body);
    }
}
