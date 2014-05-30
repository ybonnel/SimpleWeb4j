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

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * Route for SimpleWeb4j.
 *
 * @param <P> type of the object in request's body.
 * @param <R> type of the object to serialize in response body.
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#get(Route)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#post(Route)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#put(Route)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#delete(Route)
 */
public abstract class Route<P, R> extends CommonRoute {

    /**
     * Class of the parameter (object in request's body).
     */
    private Class<P> paramType;

    /**
     * Produce content type.
     */
    private ContentType contentType;

    /**
     * Constructor of a route.
     * By default the content type is JSON.
     *
     * @param routePath routePath of the route.
     * @param paramType class of the object in request's body.
     */
    public Route(String routePath, Class<P> paramType) {
        this(routePath, paramType, ContentType.JSON);
    }

    /**
     * Constructor of a route.
     *
     * @param routePath   routePath of the route.
     * @param paramType   class of the object in request's body.
     * @param contentType contentType of the object in request's body.
     */
    public Route(String routePath, Class<P> paramType, ContentType contentType) {
        super(routePath);
        this.contentType = contentType;
        this.paramType = paramType;
    }

    /**
     * @return class of the object in request's body.
     */
    public Class<P> getParamType() {
        return paramType;
    }

    /**
     * @return contentType
     */
    public ContentType getContentType() {
        return contentType;
    }

    /**
     * Parse the parameter of route (content of request body).
     *
     * @param request http request.
     * @return the parameters parsed.
     * @throws java.io.IOException in case of IO error.
     */
    protected P getRouteParam(HttpServletRequest request) throws IOException {
        P param = null;
        if (getParamType() != null && getParamType() != Void.class) {
            param = ContentType.GSON.fromJson(request.getReader(), getParamType());
            request.getReader().close();
        }
        return param;
    }

    /**
     * Method to implement to handle a request on the route.
     *
     * @param param       the parameter object in request's body.
     * @param routeParams parameters in the routePath.
     * @return the response to send.
     * @throws HttpErrorException use it for any http error, like new HttpErrorException(404) for "not found" error.
     */
    public abstract Response<R> handle(P param, RouteParameters routeParams) throws HttpErrorException;
}
