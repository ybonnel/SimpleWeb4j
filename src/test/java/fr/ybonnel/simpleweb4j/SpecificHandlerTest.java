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
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;

public class SpecificHandlerTest extends AbstractHandler {

    private Random random = new Random();
    private SimpleWebTestUtil testUtil;

    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        addSpecificHandler(this);
        SimpleWeb4j.start(false);
    }

    @After
    public void stopServer() throws Exception {
        SimpleWeb4j.stop();
    }

    @Test
    public void should_servet_get_by_id() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/tutu");
        assertEquals(200, response.status);
        assertEquals("OK", response.body);
    }

    @Override
    public void handle(String target, Request baseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_OK);
        response.getOutputStream().print("OK");
        response.getOutputStream().flush();
        response.getOutputStream().close();
    }
}
