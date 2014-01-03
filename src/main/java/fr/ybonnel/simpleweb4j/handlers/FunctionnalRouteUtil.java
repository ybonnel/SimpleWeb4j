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
 * Utility class to convert {@link fr.ybonnel.simpleweb4j.handlers.FunctionnalRoute}
 * to {@link fr.ybonnel.simpleweb4j.handlers.Route}.
 * Don't use it :)
 */
public final class FunctionnalRouteUtil {

    /**
     * Private constructor to avoir instanciate.
     */
    private FunctionnalRouteUtil() {
    }

    /**
     * Convert a {@link fr.ybonnel.simpleweb4j.handlers.FunctionnalRoute}
     * to {@link fr.ybonnel.simpleweb4j.handlers.Route}.
     * @param functionnalRoute the functionnalRoute to convert.
     * @param routePath path of route.
     * @param paramType type of body param.
     * @param <P> type of body param.
     * @param <R> return type of route.
     * @return the route converted from functionnal route.
     */
    @SuppressWarnings("unchecked")
    public static <P, R> Route<P, R> functionnalRouteToRoute(FunctionnalRoute functionnalRoute, String routePath, Class paramType) {
        return new Route<P, R>(routePath, paramType) {
            @Override
            public Response handle(P param, RouteParameters routeParams) throws HttpErrorException {
                return functionnalRoute.handle(param, routeParams);
            }
        };
    }
}
