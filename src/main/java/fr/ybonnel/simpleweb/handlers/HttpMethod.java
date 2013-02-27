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
package fr.ybonnel.simpleweb.handlers;


import javax.servlet.http.HttpServletResponse;

public enum HttpMethod {
    GET, POST(HttpServletResponse.SC_CREATED, HttpServletResponse.SC_CREATED), PUT, DELETE, HEAD, TRACE, CONNECT, OPTIONS;

    private int defaultStatus;
    private int defaultStatusWithNoContent;

    private HttpMethod() {
        this(HttpServletResponse.SC_OK, HttpServletResponse.SC_NO_CONTENT);
    }

    private HttpMethod(int defaultStatus, int defaultStatusWithNoContent) {
        this.defaultStatus = defaultStatus;
        this.defaultStatusWithNoContent = defaultStatusWithNoContent;
    }

    public static HttpMethod fromValue(String value) {
        return HttpMethod.valueOf(value);
    }

    public int getDefaultStatus() {
        return defaultStatus;
    }

    public int getDefaultStatusWithNoContent() {
        return defaultStatusWithNoContent;
    }
}
