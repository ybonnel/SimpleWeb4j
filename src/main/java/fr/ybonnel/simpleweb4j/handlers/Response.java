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

public class Response<T> {

    private T answer;
    private Integer status;

    public Response(T answer) {
        this(answer, null);
    }

    public Response(T answer, Integer status) {
        this.answer = answer;
        this.status = status;
    }

    public T getAnswer() {
        return answer;
    }

    public Integer getStatus() {
        return status;
    }
}
