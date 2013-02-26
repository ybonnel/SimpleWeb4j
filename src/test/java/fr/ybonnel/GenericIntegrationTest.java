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


import fr.ybonnel.exception.HttpErrorException;
import fr.ybonnel.handlers.Response;
import fr.ybonnel.handlers.Route;
import fr.ybonnel.handlers.RouteParameters;
import fr.ybonnel.util.SimpleWebTestUtil;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Random;

import static fr.ybonnel.SimpleWeb.*;
import static org.junit.Assert.assertEquals;

public class GenericIntegrationTest {

    private static int port;
    private static Random random = new Random();
    private static SimpleWebTestUtil testUtil;


    @BeforeClass
    public static void startServer() {
        port = random.nextInt(10000) + 10000;
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        get(new Route<Void, String>("/resource", Void.class, String.class) {
            @Override
            public Response<String> handle(Void param, RouteParameters routeParams) {
                return new Response<>("Hello World");
            }
        });

        get(new Route<Void, String>("/resource/:name", Void.class, String.class) {
            @Override
            public Response<String> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                if (routeParams.getParam("name").equals("notfound")) {
                    throw new HttpErrorException(404, "notfound not found");
                }
                return new Response<>("Hello " + routeParams.getParam("name"));
            }
        });

        post(new Route<String, String>("/resource", String.class, String.class) {

            @Override
            public Response<String> handle(String param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>("Hello " + param);
            }
        });

        get(new Route<Void, String>("/othercode", Void.class, String.class) {
            @Override
            public Response<String> handle(Void param, RouteParameters routeParams) {
                return new Response<>("I m a teapot", 418);
            }
        });

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

    @AfterClass
    public static void stopServer() {
        stop();
    }

    @Test
    public void should_serve_basic_html_file() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/test.html");
        assertEquals(200, response.status);
        assertEquals("just a test", response.body);
    }

    @Test
    public void should_answer_to_simple_get() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource");
        assertEquals(200, response.status);
        assertEquals("application/json", response.contentType);
        assertEquals("\"Hello World\"", response.body);
    }

    @Test
    public void should_answer_to_get_with_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource/myName");
        assertEquals(200, response.status);
        assertEquals("application/json", response.contentType);
        assertEquals("\"Hello myName\"", response.body);
    }

    @Test
    public void can_send_http_error() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource/notfound");
        assertEquals(404, response.status);
        assertEquals("application/json", response.contentType);
        assertEquals("\"notfound not found\"", response.body);
    }

    @Test
    public void can_post_json() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("POST", "/resource", "\"myName\"");
        assertEquals(201, response.status);
        assertEquals("application/json", response.contentType);
        assertEquals("\"Hello myName\"", response.body);
    }

    @Test
    public void can_answer_specific_http_code() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/othercode");
        assertEquals(418, response.status);
        assertEquals("application/json", response.contentType);
        assertEquals("\"I m a teapot\"", response.body);
    }

    public static void main(String[] args) {
        startServer();
        System.out.println(port);
    }


}
