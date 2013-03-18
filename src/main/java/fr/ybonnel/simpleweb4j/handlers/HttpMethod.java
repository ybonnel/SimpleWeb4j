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


import javax.servlet.http.HttpServletResponse;

/**
 * HTTP Methods used by SimpleWeb4j..
 */
public enum HttpMethod {
    /**
     * GET method.
     */
    GET,
    /**
     * POST Method.
     */
    POST(HttpServletResponse.SC_CREATED, HttpServletResponse.SC_CREATED),
    /**
     * PUT Method.
     */
    PUT,
    /**
     * DELETE Method.
     */
    DELETE;

    /**
     * Default http status for a method.
     */
    private int defaultStatus;
    /**
     * Default http status for a method if there's no content in response.
     */
    private int defaultStatusWithNoContent;

    /**
     * Constructor with default valued (200 and 204) for defaultStatus and defaultStatusWithNoContent.
     */
    private HttpMethod() {
        this(HttpServletResponse.SC_OK, HttpServletResponse.SC_NO_CONTENT);
    }

    /**
     * Constructor.
     * @param defaultStatus Default http status for a method.
     * @param defaultStatusWithNoContent Default http status for a method if there's no content in response.
     */
    private HttpMethod(int defaultStatus, int defaultStatusWithNoContent) {
        this.defaultStatus = defaultStatus;
        this.defaultStatusWithNoContent = defaultStatusWithNoContent;
    }

    /**
     * Get the HttpMethod from the string value.
     * @param value string value of method.
     * @return the HttpMethod.
     */
    public static HttpMethod fromValue(String value) {
        return HttpMethod.valueOf(value);
    }

    /**
     * @return Default http status for a method.
     */
    public int getDefaultStatus() {
        return defaultStatus;
    }

    /**
     * @return Default http status for a method if there's no content in response.
     */
    public int getDefaultStatusWithNoContent() {
        return defaultStatusWithNoContent;
    }
}
