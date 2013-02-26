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
package fr.ybonnel.handlers;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;
import org.mortbay.jetty.HttpConnection;
import org.mortbay.jetty.Request;
import org.mortbay.jetty.handler.AbstractHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

public class JsonHandler extends AbstractHandler {

    private Map<HttpMethod, List<Route>> routes = new HashMap<>();
    private Gson gson = new GsonBuilder().create();

    public void addRoute(HttpMethod httpMethod, Route route) {
        if (!routes.containsKey(httpMethod)) {
            routes.put(httpMethod, new ArrayList<Route>());
        }
        routes.get(httpMethod).add(route);
    }


    @Override
    public void handle(String target, HttpServletRequest request, HttpServletResponse response, int dispatch) throws IOException, ServletException {
        Request baseRequest = request instanceof Request ? (Request) request : HttpConnection.getCurrentConnection().getRequest();
        if (baseRequest.isHandled()) {
            return;
        }
        Route route = findRoute(request.getMethod(), request.getPathInfo());
        if (route == null) {
            return;
        }

        Object param = null;
        if (route.getParamType() != null && route.getParamType() != Void.class) {
            param = gson.fromJson(request.getReader(), route.getParamType());
        }
        Object returnObject = route.handle(param);

        response.setStatus(HttpServletResponse.SC_OK);
        if (returnObject != null) {
            response.setContentType("application/json");
            response.getOutputStream().print(gson.toJson(returnObject));
            response.getOutputStream().close();
        }
        baseRequest.setHandled(true);

    }

    private Route findRoute(String httpMethod, String pathInfo) {
        if (!routes.containsKey(HttpMethod.fromValue(httpMethod))) {
            return null;
        }
        for (Route route : routes.get(HttpMethod.fromValue(httpMethod))) {
            if (route.isThisPath(pathInfo)) {
                return route;
            }
        }
        return null;
    }
}
