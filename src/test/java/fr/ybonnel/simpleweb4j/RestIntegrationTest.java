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
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;

public class RestIntegrationTest {

    private Random random = new Random();
    private SimpleWebTestUtil testUtil;

    private static String lastCall = null;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        resource(new RestResource<String>("string", String.class) {
            @Override
            public String getById(String id) throws HttpErrorException {
                return "getById " + id;
            }

            @Override
            public List<String> getAll() throws HttpErrorException {
                return Arrays.asList("getAll1", "getAll2");
            }

            @Override
            public void update(String id, String resource) throws HttpErrorException {
                lastCall = "update " + id + " " + resource;
            }

            @Override
            public String create(String resource) throws HttpErrorException {
                lastCall = "create " + resource;
                return resource;
            }

            @Override
            public void delete(String id) throws HttpErrorException {
                lastCall = "delete " + id;
            }
        });

        start(false);
    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_servet_get_by_id() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/string/123");
        assertEquals(200, response.status);
        assertEquals("\"getById 123\"", response.body);
    }

    @Test
    public void should_servet_getall() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/string");
        assertEquals(200, response.status);
        assertEquals("[\"getAll1\",\"getAll2\"]", response.body);
    }

    @Test
    public void should_servet_create() throws Exception {
        lastCall = null;
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("POST", "/string", "createdResource");
        assertEquals(201, response.status);
        assertEquals("create createdResource", lastCall);
        assertEquals("\"createdResource\"", response.body);
    }

    @Test
    public void should_servet_update() throws Exception {
        lastCall = null;
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("PUT", "/string/123", "updatedResource");
        assertEquals(204, response.status);
        assertEquals("update 123 updatedResource", lastCall);
    }

    @Test
    public void should_servet_delete() throws Exception {
        lastCall = null;
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("DELETE", "/string/123");
        assertEquals(204, response.status);
        assertEquals("delete 123", lastCall);
    }
}
