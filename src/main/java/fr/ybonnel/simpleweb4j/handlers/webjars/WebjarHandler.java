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
package fr.ybonnel.simpleweb4j.handlers.webjars;

import fr.ybonnel.simpleweb4j.types.ContentTypes;
import org.apache.commons.lang3.time.DateUtils;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Handler to support webjars.
 */
public class WebjarHandler extends AbstractHandler {

    /**
     * Size of buffer for copy.
     */
    private static final int BUFFER_SIZE = 8192;
    /**
     * Start time.
     */
    private final Date startTime = new Date();

    /**
     * Pattern for http dates.
     */
    private static final String RFC1123_DATE_PATTERN = "EEE, dd MMM yyyy HH:mm:ss zzz";
    /**
     * Formatter in thread local.
     */
    private final ThreadLocal<SimpleDateFormat> rfc1123Format = new ThreadLocal<>();

    /**
     * Get RFC1123 date formatter.
     * @return date formatter.
     */
    public SimpleDateFormat getRfc1123Format() {
        if (rfc1123Format.get() == null) {
            rfc1123Format.set(new SimpleDateFormat(RFC1123_DATE_PATTERN));
        }
        return rfc1123Format.get();
    }


    /**
     * Handle a request.
     *
     * @param target      The target of the request - either a URI or a name.
     * @param baseRequest The original unwrapped request object.
     * @param request     The request either as the {@link Request}
     *                    object or a wrapper of that request.
     * @param response    The response as the {@link org.eclipse.jetty.server.Response}
     *                    object or a wrapper of that request.
     * @throws IOException in case of IO error.
     */
    @Override
    public void handle(String target, Request baseRequest,
                       HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (baseRequest.isHandled()
                || !baseRequest.getPathInfo().startsWith("/webjars/")) {
            return;
        }

        URL classpathUrl = ClassLoader.getSystemResource("META-INF/resources" + baseRequest.getPathInfo());
        if (classpathUrl == null) {
            return;
        }

        baseRequest.setHandled(true);
        if (baseRequest.getHeader("if-modified-since") != null) {
            // webjar are never modified (version is in path).
            response.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        try (InputStream in = classpathUrl.openStream();
             OutputStream out = response.getOutputStream()) {
            response.setContentType(ContentTypes.get(Paths.get(baseRequest.getPathInfo())));
            response.addHeader("cache-control", "public, max-age=31536000");
            response.addHeader("last-modified", getRfc1123Format().format(startTime));
            response.addHeader("expires", getRfc1123Format().format(DateUtils.addWeeks(new Date(), 1)));

            int count;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((count = in.read(buffer)) > 0) {
                out.write(buffer, 0, count);
            }
        }
    }
}
