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

import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;
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

import static java.util.Arrays.asList;

/**
 * Compiler for coffee files.
 */
public class CoffeeCompilerHandler extends AbstractHandler {

    /**
     * Logger.
     */
    private static final Logger logger = LoggerFactory.getLogger(CoffeeCompilerHandler.class);

    /**
     * Path to public resource.
     */
    private String publicResourcePath = "/public";

    /**
     * CoffeeScript compiler.
     */
    private JCoffeeScriptCompiler compiler = new JCoffeeScriptCompiler(asList(Option.BARE));

    /**
     * Change the path to public resources.
     * @param newPublicResourcePath new path.
     */
    public void setPublicResourcePath(String newPublicResourcePath) {
        this.publicResourcePath = newPublicResourcePath;
    }

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
     * @throws IOException in case of I/O error.
     */
    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException {
        Request baseRequest = request instanceof Request ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        if (baseRequest.isHandled() || !"GET".equals(request.getMethod())) {
            return;
        }

        if (!request.getPathInfo().endsWith(".coffee")) {
            return;
        }

        try {
            String result = compiler.compile(
                    convertStreamToString(
                            CoffeeCompilerHandler.class.getResourceAsStream(
                                    publicResourcePath + request.getPathInfo())));

            baseRequest.setHandled(true);
            response.setContentType("application/javascript");
            response.setStatus(HttpServletResponse.SC_OK);
            response.getOutputStream().print(result);
            response.getOutputStream().flush();
            response.getOutputStream().close();
        } catch (JCoffeeScriptCompileException exception) {
            logger.warn("CoffeeScript compile error on {}", request.getPathInfo());
            logger.warn("Compile error", exception);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            StringWriter writer = new StringWriter();
            PrintWriter printWriter = new PrintWriter(writer);
            exception.printStackTrace(printWriter);
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
