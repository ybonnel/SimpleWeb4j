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

import java.util.HashMap;
import java.util.Map;

/**
 * Class used to represent the parameters in a request path.
 */
public class RouteParameters {

    /**
     * Map of parameters by name.
     */
    private Map<String, String> params = new HashMap<>();

    /**
     * Constructor.
     * @param params map of parameters by name.
     */
    protected RouteParameters(Map<String, String> params) {
        this.params = params;
    }

    /**
     * Get a parameter value.
     * @param param name of parameter.
     * @return value of parameter if found, null otherwise.
     */
    public String getParam(String param) {
        return params.get(param);
    }
}
