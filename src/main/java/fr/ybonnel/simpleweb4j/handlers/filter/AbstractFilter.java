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
package fr.ybonnel.simpleweb4j.handlers.filter;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;

/**
 * This abstract class is the class to implement to add a filter in the request handler.<br/>
 * To add a filter, you must call {@link fr.ybonnel.simpleweb4j.SimpleWeb4j#addFilter(AbstractFilter)}.<br/>
 * For example, filters can be used to add security based on a token passed throw query param :
 * <p><blockquote><pre>
 * addFilter(new AbstractFilter() {
 *     {@literal @Override}
 *     public void handle(Route route, RouteParameters routeParams) throws HttpErrorException {
 *         if (routeParams.getParam("token") == null
 *                 || !authorizedTokens.contains(routeParams.getParam("token")) {
 *             throw new HttpErrorException(403, "unauthorized token");
 *         }
 *     }
 * }
 * </pre></blockquote></p>
 */
public abstract class AbstractFilter {

    /**
     * Implement this method to handle any request.<br/>
     * In a filter you can :
     * <ul>
     *     <li>Access parameters of route (routeParams)</li>
     *     <li>Add parameters to routeParams</li>
     *     <li>Throw an httpErrorException</li>
     * </ul>
     * @param route route to be applied.
     * @param routeParams route params
     * @throws HttpErrorException use it for any http error, like new HttpErrorException(404) for "not found" error.
     */
    public abstract void handle(Route route, RouteParameters routeParams) throws HttpErrorException;
}
