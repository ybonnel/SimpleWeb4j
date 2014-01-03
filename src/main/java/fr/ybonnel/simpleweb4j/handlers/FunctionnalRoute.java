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

/**
 * This class represent the handle method for a given route.
 * @param <P> type of the object in request's body.
 * @param <R> type of the object to serialize in response body.
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#get(String, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#post(String, Class, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#post(String, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#put(String, Class, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#put(String, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#delete(String, Class, FunctionnalRoute)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#delete(String, FunctionnalRoute)
 */
@FunctionalInterface
public interface FunctionnalRoute<P, R> {


    /**
     * Method to implement to handle a request on the route.
     *
     * @param param       the parameter object in request's body.
     * @param routeParams parameters in the routePath.
     * @return the response to send.
     * @throws HttpErrorException use it for any http error, like new HttpErrorException(404) for "not found" error.
     */
    Response<R> handle(P param, RouteParameters routeParams) throws HttpErrorException;
}
