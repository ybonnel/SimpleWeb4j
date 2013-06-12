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

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;

public class ChangePublicIntegrationTest {

    private Random random = new Random();
    private SimpleWebTestUtil testUtil;
    private File htmlFile;


    @Before
    public void startServer() throws IOException {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);

        htmlFile = File.createTempFile("index", ".html");

        BufferedWriter writer = new BufferedWriter(new FileWriter(htmlFile));
        writer.write("external html file");
        writer.flush();
        writer.close();

        htmlFile.deleteOnExit();


        setPublicResourcesPath("/otherpublic");
        System.out.println(htmlFile.getAbsolutePath());
        setExternalPublicResourcesPath(htmlFile.getParentFile().getAbsolutePath());

        start(false);
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

    @Test
    public void should_serve_basic_html_file_from_external_dir() throws Exception {
        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/" + htmlFile.getName());
        assertEquals(200, response.status);
        assertEquals("external html file", response.body);

    }
}
