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

/**
 * Response for a route.
 * @param <T> type of the object to serialize in response's body.
 */
public class Response<T> {

    /**
     * Answer.
     */
    private T answer;
    /**
     * Http status.
     */
    private Integer status;

    /**
     * Constructor.
     * @param answer object to serialize in response's body.
     */
    public Response(T answer) {
        this(answer, null);
    }

    /**
     * Constructor.
     * @param answer object to serialize in response's body.
     * @param status http status.
     */
    public Response(T answer, Integer status) {
        this.answer = answer;
        this.status = status;
    }

    /**
     * @return object to serialize in response's body.
     */
    public T getAnswer() {
        return answer;
    }

    /**
     * @return http status.
     */
    public Integer getStatus() {
        return status;
    }
}
