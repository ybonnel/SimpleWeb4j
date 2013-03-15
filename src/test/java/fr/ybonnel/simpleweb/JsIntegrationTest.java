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

import com.google.common.base.Strings;
import fr.ybonnel.simpleweb.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static fr.ybonnel.simpleweb.SimpleWeb.*;
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
    public void should_serve_jquery_library() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/js/jquery.js");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));

        response = testUtil.doMethod("GET", "/js/jquery.min.js");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));
    }

    @Test
    public void should_serve_angular_library() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/js/angular.js");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));

        response = testUtil.doMethod("GET", "/js/angular.min.js");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));
    }

    @Test
    public void should_serve_bootstrap_css() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/css/bootstrap.css");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));

        response = testUtil.doMethod("GET", "/css/bootstrap.min.css");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));

        response = testUtil.doMethod("GET", "/css/bootstrap-responsive.css");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));

        response = testUtil.doMethod("GET", "/css/bootstrap-responsive.min.css");
        assertEquals(200, response.status);
        assertFalse(Strings.isNullOrEmpty(response.body));
    }

}
