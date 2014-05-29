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
 * @param <R> type of the object to serialize in response body.
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#get(String, fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteWithNoParam)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#post(String, fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteWithNoParam)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#put(String, fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteWithNoParam)
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#delete(String, fr.ybonnel.simpleweb4j.handlers.FunctionnalRouteWithNoParam)
 */
@FunctionalInterface
public interface FunctionnalRouteWithNoParam<R> {


    /**
     * Method to implement to handle a request on the route.
     *
     * @return the response to send.
     * @throws fr.ybonnel.simpleweb4j.exception.HttpErrorException use it for any http error, like new HttpErrorException(404) for "not found" error.
     */
    Response<R> handle() throws HttpErrorException;
}
