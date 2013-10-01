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
package fr.ybonnel.simpleweb4j.util;


import org.apache.commons.io.IOUtils;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SimpleWebTestUtil {

    private int port;

    public SimpleWebTestUtil(int port) {
        this.port = port;
    }

    public UrlResponse doMethod(String requestMethod, String path) throws Exception {
        return doMethod(requestMethod, path, null, null);
    }

    public UrlResponse doMethod(String requestMethod, String path, Map<String, String> headers) throws Exception {
        return doMethod(requestMethod, path, null, headers);
    }

    public UrlResponse doMethod(String requestMethod, String path, String body) throws Exception {
        return doMethod(requestMethod, path, body, null);
    }

    public UrlResponse doMethod(String requestMethod, String path, String body, Map<String, String> headers)
            throws Exception {
        URL url = new URL("http://localhost:" + port + path);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);

        if (headers != null) {
            for (Map.Entry<String, String> header : headers.entrySet()) {
                connection.addRequestProperty(header.getKey(), header.getValue());
            }
        }

        if (body != null) {
            connection.setDoOutput(true);
            connection.getOutputStream().write(body.getBytes());
        }

        connection.connect();

        UrlResponse response = new UrlResponse();
        response.status = connection.getResponseCode();
        response.headers = connection.getHeaderFields();
        response.contentType = connection.getContentType();

        if (response.status >= 400) {
            if (connection.getErrorStream() != null) {
                response.body = IOUtils.toString(connection.getErrorStream());
            }
        } else {
            if (connection.getInputStream() != null) {
                if (response.headers.containsKey("Content-Encoding")
                        && response.headers.get("Content-Encoding").get(0).equals("gzip")) {
                    response.body = IOUtils.toString(new GZIPInputStream(connection.getInputStream()));
                    response.isGzipped = true;
                } else {
                    response.body = IOUtils.toString(connection.getInputStream());
                }
            }
        }
        return response;
    }

    public static class UrlResponse {

        public Map<String, List<String>> headers;
        public String body;
        public int status;
        public String contentType;
        public boolean isGzipped = false;
    }
}
