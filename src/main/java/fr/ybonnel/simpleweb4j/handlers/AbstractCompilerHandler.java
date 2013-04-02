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
package fr.ybonnel.simpleweb4j.handlers;

import fr.ybonnel.simpleweb4j.exception.CompileErrorException;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Scanner;

/**
 * Compiler for any files to compile.
 */
public abstract class AbstractCompilerHandler extends AbstractHandler {

    /**
     * Logger.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractCompilerHandler.class);

    /**
     * Path to public resource.
     */
    private String publicResourcePath = "/public";

    /**
     * Change the path to public resources.
     * @param newPublicResourcePath new path.
     */
    public void setPublicResourcePath(String newPublicResourcePath) {
        this.publicResourcePath = newPublicResourcePath;
    }

    /**
     * Get suffixe name like ".coffee" of files to handle.
     * @return the suffixe name.
     */
    protected abstract String getSuffixeName();

    /**
     * Compile the file.
     * @param source source to compile.
     * @return the compiled content.
     * @throws CompileErrorException in case of compile error.
     */
    protected abstract String compile(String source) throws CompileErrorException;

    /**
     * Content type to return.
     * @return content type.
     */
    protected abstract String getContentType();

    /**
     * Handle a request.
     * @param target The target of the request - either a URI or a name.
     * @param request The request either as the {@link org.mortbay.jetty.Request}
     * object or a wrapper of that request. The {@link org.mortbay.jetty.HttpConnection#getCurrentConnection()}
     * method can be used access the Request object if required.
     * @param response The response as the {@link org.mortbay.jetty.Response}
     * object or a wrapper of that request. The {@link org.mortbay.jetty.HttpConnection#getCurrentConnection()}
     * method can be used access the Response object if required.
     * @param dispatch The dispatch mode: {@link #REQUEST}, {@link #FORWARD}, {@link #INCLUDE}, {@link #ERROR}
     * @throws java.io.IOException in case of I/O error.
     */
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException {
        Request baseRequest = request instanceof Request ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        if (baseRequest.isHandled() || !"GET".equals(request.getMethod())) {
            return;
        }

        if (!request.getPathInfo().endsWith(getSuffixeName())) {
            return;
        }

        try {
            InputStream resource = AbstractCompilerHandler.class.getResourceAsStream(
                    publicResourcePath + request.getPathInfo());
            if (resource != null) {
                String result = compile(
                        convertStreamToString(
                                resource));

                baseRequest.setHandled(true);
                response.setContentType(getContentType());
                response.setStatus(HttpServletResponse.SC_OK);
                response.getOutputStream().print(result);
                response.getOutputStream().flush();
                response.getOutputStream().close();
            }
        } catch (CompileErrorException exception) {
            LOGGER.warn("Compile error on {}", request.getPathInfo());
            LOGGER.warn("Compile error", exception.getCause());
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exception.getCause().printStackTrace(printWriter);
            response.getOutputStream().print(writer.toString());
            response.getOutputStream().close();
            baseRequest.setHandled(true);
        }

    }

    /**
     * Convert InputStream to string.
     * @param is inputStream to convert.
     * @return the string converted.
     */
    protected static String convertStreamToString(InputStream is) {
        return new Scanner(is, "UTF-8").useDelimiter("\\A").next();
    }
}
