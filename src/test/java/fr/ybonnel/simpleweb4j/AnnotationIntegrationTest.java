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


import fr.ybonnel.simpleweb4j.annotations.*;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;

public class AnnotationIntegrationTest {

    public static final String CONTENT_TYPE = "application/json;charset=" + Charset.defaultCharset().displayName();
    private Random random = new Random();
    private SimpleWebTestUtil testUtil;

    public static class Controller {

        @Jsonp(callback = "CALLBACK", path = "/jsonp", resultType = String.class)
        @Get(path = "/resource", resultType = String.class)
        public static String helloWorld() {
            return "Hello World";
        }

        @Get(path = "/resource/:name", resultType = String.class)
        public static String helloName(@RouteParam("name") String name) throws HttpErrorException {
            if (name.equals("notfound")) {
                throw new HttpErrorException(404);
            }
            return "Hello " + name;
        }

        @Post(path = "/resource", paramType = String.class, resultType = String.class)
        @Put(path = "/resource/put", paramType = String.class, resultType = String.class)
        public static String helloPost(String name) {
            return "Hello " + name;
        }

        @Get(path = "/othercode", resultType = String.class)
        public static Response<String> iAmATeaPot() {
            return new Response<>("I m a teapot", 418);
        }

        @Delete(path = "/resource/delete", resultType = String.class)
        public static String delete() {
            return "deleted";
        }
    }


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        setAnnotatedClasses(Controller.class);

        start(false);
    }

    @After
    public void stopServer() {
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
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello World\"", response.body);
    }

    @Test
    public void should_answer_to_get_with_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource/myName");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello myName\"", response.body);
    }

    @Test
    public void can_send_http_error() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource/notfound");
        assertEquals(404, response.status);
    }

    @Test
    public void can_post_json() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("POST", "/resource", "\"myName\"");
        assertEquals(201, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello myName\"", response.body);
    }

    @Test
    public void can_answer_specific_http_code() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/othercode");
        assertEquals(418, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"I m a teapot\"", response.body);
    }

    @Test
    public void can_answer_to_put_method() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/resource/put", "\"myName\"");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello myName\"", response.body);
    }

    @Test
    public void can_answer_to_delete_method() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/resource/delete");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"deleted\"", response.body);
    }

    @Test
    public void should_answer_to_simple_get_with_jsonp() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/jsonp?CALLBACK=foo");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("foo(\"Hello World\");", response.body);
    }
}
