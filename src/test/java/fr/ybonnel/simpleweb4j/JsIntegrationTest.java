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

import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class JsIntegrationTest {


    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        start(false);
    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_serve_jquery_library() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/webjars/jquery/2.0.3/jquery.js");
        assertEquals(200, response.status);
        assertFalse(response.body.isEmpty());

        response = testUtil.doMethod("GET", "/webjars/jquery/2.0.3/jquery.min.js");
        assertEquals(200, response.status);
        assertFalse(response.body.isEmpty());
    }

    @Test
    public void should_serve_angular_library() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/webjars/angularjs/1.2.8/angular.js");
        assertEquals(200, response.status);
        assertFalse(response.body, response.body.isEmpty());

        response = testUtil.doMethod("GET", "/webjars/angularjs/1.2.8/angular.min.js");
        assertEquals(200, response.status);
        assertFalse(response.body.isEmpty());
    }

    @Test
    public void should_serve_bootstrap_css() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/webjars/bootstrap/3.0.3/css/bootstrap.css");
        assertEquals(200, response.status);
        assertFalse(response.body.isEmpty());

        response = testUtil.doMethod("GET", "/webjars/bootstrap/3.0.3/css/bootstrap.min.css");
        assertEquals(200, response.status);
        assertFalse(response.body.isEmpty());
    }

    @Test
    public void should_not_serve_unknown_webjar() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/webjars/unknow.css");
        assertEquals(404, response.status);
    }

    @Test
    public void should_support_cache() throws Exception {

        String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
        final SimpleDateFormat format = new SimpleDateFormat(RFC1123_DATE_PATTERN);

        // if we started server since last-modified.
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/webjars/bootstrap/3.0.3/css/bootstrap.css", new HashMap<String, String>(){{
            put("If-Modified-Since", format.format(new Date()));
        }});
        assertEquals(304, response.status);

    }

}
