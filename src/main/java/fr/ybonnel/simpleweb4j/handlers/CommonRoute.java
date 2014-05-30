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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Common class between
 * {@link fr.ybonnel.simpleweb4j.handlers.Route}
 * and {@link fr.ybonnel.simpleweb4j.handlers.WebSocketRoute}.
 */
abstract class CommonRoute {

    /**
     * Path of the route.
     */
    private final String routePath;

    /**
     * Path in segment (split on '/').
     */
    private final List<String> pathInSegments;

    /**
     * @param routePath routePath of the route.
     */
    protected CommonRoute(String routePath) {
        this.routePath = routePath;
        pathInSegments = new ArrayList<>();
        for (String path : routePath.split("\\/")) {
            if (path.length() > 0) {
                pathInSegments.add(path);
            }
        }
    }

    /**
     * Used to know is a route is valid for a routePath.
     *
     * @param path the routePath.
     * @return true if the route is valid.
     */
    protected boolean isThisPath(String path) {
        if (this.routePath.equals(path)) {
            return true;
        }
        List<String> queryPath = new ArrayList<>();
        for (String segment : path.split("\\/")) {
            if (segment.length() > 0) {
                queryPath.add(segment);
            }
        }
        if (queryPath.size() == pathInSegments.size()) {
            boolean same = true;
            for (int index = 0; index < queryPath.size(); index++) {
                if (!pathInSegments.get(index).startsWith(":")
                        && !pathInSegments.get(index).equals(queryPath.get(index))) {
                    same = false;
                    break;
                }
            }
            return same;
        } else {
            return false;
        }
    }

    /**
     * Get the parameters in route.
     *
     * @param pathInfo        the routePath.
     * @param queryParameters parameters from query.
     * @return the map of parameters in routePath.
     */
    protected Map<String, String> getRouteParams(String pathInfo, Map<String, String> queryParameters) {
        Map<String, String> params = new HashMap<>();
        List<String> queryPath = new ArrayList<>();
        for (String segment : pathInfo.split("\\/")) {
            if (segment.length() > 0) {
                queryPath.add(segment);
            }
        }
        for (int index = 0; index < queryPath.size(); index++) {
            if (pathInSegments.get(index).startsWith(":")) {
                params.put(pathInSegments.get(index).substring(1), queryPath.get(index));
            }
        }
        params.putAll(queryParameters);
        return Collections.unmodifiableMap(params);
    }
}
