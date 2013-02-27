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


import fr.ybonnel.simpleweb.exception.HttpErrorException;
import fr.ybonnel.simpleweb.handlers.Response;
import fr.ybonnel.simpleweb.handlers.Route;
import fr.ybonnel.simpleweb.handlers.RouteParameters;
import fr.ybonnel.simpleweb.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.ybonnel.simpleweb.SimpleWeb.*;
import static org.junit.Assert.assertEquals;

public class ChangePublicIntegrationTest {

    private int port;
    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        port = random.nextInt(10000) + 10000;
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);
        setPublicResourcesPath("/otherpublic");


        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();


    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_serve_basic_html_file_from_other_public() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/test.html");
        assertEquals(200, response.status);
        assertEquals("just an other test", response.body);
    }
}
