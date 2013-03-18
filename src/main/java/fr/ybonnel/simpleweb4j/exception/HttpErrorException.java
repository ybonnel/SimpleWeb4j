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
package fr.ybonnel.simpleweb4j.exception;

/**
 * Exception use for error in Route or RestResource.
 */
public class HttpErrorException extends Exception {

    /**
     * Http status used in answer.
     */
    private int status;
    /**
     * Answer to write in response's body.
     */
    private Object answer;

    /**
     * Constructor with http status.
     * @param status http status (404 for not found, 500 of internal server error, ...).
     */
    public HttpErrorException(int status) {
        this(status, null);
    }

    /**
     * Constructor with http status and answer.
     * @param status http status (404 for not found, 500 of internal server error, ...).
     * @param answer answer to write in response's body (written as json).
     */
    public HttpErrorException(int status, Object answer) {
        this.status = status;
        this.answer = answer;

    }

    /**
     * Get the http status.
     * @return the http status.
     */
    public int getStatus() {
        return status;
    }

    /**
     * Get the answer to write in response's body.
     * @return the answer.
     */
    public Object getAnswer() {
        return answer;
    }
}
