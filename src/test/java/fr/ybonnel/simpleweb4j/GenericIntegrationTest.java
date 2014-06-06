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


import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.*;

public class GenericIntegrationTest {

    public static final String CONTENT_TYPE = "application/json;charset=" + Charset.defaultCharset().displayName();
    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        jsonp("CALLBACK", "/jsonp", () -> new Response<>("Hello World"));

        jsonp("CALLBACK", "/jsonp/:name",
                (param, routeParameters) -> new Response<>("Hello " + routeParameters.getParam("name")));

        get("/resource", () -> new Response<>("Hello World"));

        get("/resource/:name", (param, routeParams) -> {
            if (routeParams.getParam("name").equals("notfound")) {
                throw new HttpErrorException(404);
            }
            return new Response<>("Hello " + routeParams.getParam("name"));
        });


        post("/resource/:name", (param, routeParams) -> new Response<>("Hello " + routeParams.getParam("name")));

        post("/resource", String.class, (param, routeParams) -> new Response<>("Hello " + param));

        post("/resource-ss-params", () -> new Response<>("resource-ss-params"));

        get("/othercode", (param, routeParams) -> new Response<>("I m a teapot", 418));

        put("/resource/put", String.class, (param, routeParams) -> new Response<>("Hello " + param));

        put("/resource/put/:name", (param, routeParams) -> new Response<>("Hello " + routeParams.getParam("name")));

        put("/put-ss-param", () -> new Response<>("put-ss-param"));

        delete("/resource/delete", () -> new Response<>("deleted"));

        delete("/resource/deleteparam/:name", (param, routeParams) -> new Response<>("deleted " + routeParams.getParam("name")));

        delete("/resource/delete/param", String.class, (param, routeParams) -> new Response<>("deleted " + param));

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
    public void can_post_with_no_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("POST", "/resource-ss-params");
        assertEquals(201, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"resource-ss-params\"", response.body);
    }

    @Test
    public void can_post_with_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("POST", "/resource/me");
        assertEquals(201, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello me\"", response.body);
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
    public void can_answer_to_put_method_with_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/resource/put/me");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello me\"", response.body);
    }

    @Test
    public void can_answer_to_put_method_with_no_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/put-ss-param");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"put-ss-param\"", response.body);
    }

    @Test
    public void can_answer_to_delete_method() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/resource/delete");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"deleted\"", response.body);
    }

    @Test
    public void can_answer_to_delete_method_with_param_in_path() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/resource/deleteparam/me");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"deleted me\"", response.body);
    }

    @Test
    public void can_answer_to_delete_method_with_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/resource/delete/param", "\"myName\"");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"deleted myName\"", response.body);
    }

    @Test
    public void should_answer_to_simple_get_with_jsonp() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/jsonp?CALLBACK=foo");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("foo(\"Hello World\");", response.body);
    }

    @Test
    public void should_answer_to_simple_get_with_jsonp_and_param() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/jsonp/me?CALLBACK=foo");
        assertEquals(200, response.status);
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("foo(\"Hello me\");", response.body);
    }

    @Test
    public void should_answer_with_gzip_response_when_its_possible() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource", new HashMap<String, String>() {{
            put("Accept-Encoding", "deflate,gzip,sdch");
        }});
        assertEquals(200, response.status);
        assertEquals("gzip", response.headers.get("Content-Encoding").get(0));
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello World\"", response.body);
        assertTrue(response.isGzipped);
    }

    @Test
    public void should_not_answer_with_gzip_response_when_its_not_possible() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/resource", new HashMap<String, String>() {{
            put("Accept-Encoding", "deflate,sdch");
        }});
        assertEquals(200, response.status);
        assertNull(response.headers.get("Content-Encoding"));
        assertEquals(CONTENT_TYPE, response.contentType);
        assertEquals("\"Hello World\"", response.body);
        assertFalse(response.isGzipped);
    }
}
