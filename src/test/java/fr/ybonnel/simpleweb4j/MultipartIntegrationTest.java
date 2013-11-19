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
import fr.ybonnel.simpleweb4j.handlers.ContentType;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;
import fr.ybonnel.simpleweb4j.util.MultipartUtility;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.apache.commons.io.IOUtils;
import org.eclipse.jetty.util.MultiPartInputStreamParser;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.TestCase.assertNull;
import static org.junit.Assert.assertEquals;

public class MultipartIntegrationTest {

    public static class TestUploadImage {
        private String id;
        private byte[] image;
        private String imageName;
        private String other;
    }

    private static TestUploadImage lastCall = null;
    private int port;
    private Random random = new Random();

    @Before
    public void startServer() {
        resetDefaultValues();
        port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);

        resource(new RestResource<TestUploadImage>("multipart", TestUploadImage.class) {
            @Override
            public TestUploadImage getById(String id) throws HttpErrorException {
                return null;
            }

            @Override
            public List<TestUploadImage> getAll() throws HttpErrorException {
                return Collections.emptyList();
            }

            @Override
            public void update(String id, TestUploadImage resource) throws HttpErrorException {
                resource.id = id;
                lastCall = resource;
            }

            @Override
            public TestUploadImage create(TestUploadImage resource) throws HttpErrorException {
                lastCall = resource;
                return resource;
            }

            @Override
            public Route<TestUploadImage, TestUploadImage> routeCreate() {
                return new Route<TestUploadImage, TestUploadImage>("multipart", TestUploadImage.class) {
                    @Override
                    public Response<TestUploadImage> handle(TestUploadImage param, RouteParameters routeParams) throws HttpErrorException {
                        return new Response<>(create(param), HttpServletResponse.SC_CREATED);
                    }

                    @Override
                    protected TestUploadImage getRouteParam(HttpServletRequest request) throws IOException {
                        try {
                            Part dataPart = request.getPart("data");
                            TestUploadImage data = ContentType.GSON.fromJson(new InputStreamReader(dataPart.getInputStream()), getParamType());

                            Part imagePart = request.getPart("image");
                            if (null != imagePart) {
                                data.imageName = ((MultiPartInputStreamParser.MultiPart) imagePart).getContentDispositionFilename();
                                data.image = IOUtils.toByteArray(imagePart.getInputStream());
                            }
                            return data;
                        } catch (ServletException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public Route<TestUploadImage, Void> routeUpdate() {
                return new Route<TestUploadImage, Void>("multipart/:id", TestUploadImage.class) {
                    @Override
                    public Response<Void> handle(TestUploadImage param, RouteParameters routeParams) throws HttpErrorException {
                        update(routeParams.getParam("id"), param);
                        return new Response<>(null);
                    }

                    @Override
                    protected TestUploadImage getRouteParam(HttpServletRequest request) throws IOException {
                        try {
                            Part dataPart = request.getPart("data");
                            InputStreamReader dataReader = new InputStreamReader(dataPart.getInputStream());
                            TestUploadImage data = ContentType.GSON.fromJson(dataReader, getParamType());
                            dataReader.close();

                            Part imagePart = request.getPart("image");
                            if (null != imagePart) {
                                data.imageName = ((MultiPartInputStreamParser.MultiPart) imagePart).getContentDispositionFilename();
                                data.image = IOUtils.toByteArray(imagePart.getInputStream());
                            }
                            return data;
                        } catch (ServletException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                };
            }

            @Override
            public void delete(String id) throws HttpErrorException {
            }
        });

        start(false);
    }

    @After
    public void stopServer() {
        stop();
    }

    private static String getProjectPath() {
        URL resource = MultipartIntegrationTest.class.getClassLoader().getResource("");
        assert resource != null;
        String path = resource.getPath();
        return path.substring(0, path.indexOf("target"));
    }

    @Test
    public void should_servet_create() throws Exception {
        lastCall = null;

        File image = new File(getProjectPath() + "images/logo-simpleweb4j-140.png");

        MultipartUtility multipart = new MultipartUtility("POST", "http://localhost:" + port + "/multipart");
        multipart.addFormField("data", "{'other':'multipartTest'}");
        multipart.addFilePart("image", image);

        SimpleWebTestUtil.UrlResponse response = multipart.response();
        assertEquals(201, response.status);
        assertNull(lastCall.id);
        assertEquals("multipartTest", lastCall.other);
        assertEquals(image.getName(), lastCall.imageName);
        assertNotNull(lastCall.image);
    }

    @Test
    public void should_servet_update() throws Exception {
        lastCall = null;

        File image = new File(getProjectPath() + "images/logo-simpleweb4j-140.png");

        MultipartUtility multipart = new MultipartUtility("PUT", "http://localhost:" + port +  "/multipart/123");
        multipart.addFormField("data", "{'other':'updateTest'}");
        multipart.addFilePart("image", image);

        SimpleWebTestUtil.UrlResponse response = multipart.response();
        assertEquals(204, response.status);
        assertEquals("123", lastCall.id);
        assertEquals("updateTest", lastCall.other);
        assertEquals(image.getName(), lastCall.imageName);
        assertNotNull(lastCall.image);
    }
}
