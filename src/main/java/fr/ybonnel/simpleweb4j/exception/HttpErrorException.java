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

public class HttpErrorException extends Exception {

    private int status;
    private Object answer;

    public HttpErrorException(int status) {
        this(status, null);
    }

    public HttpErrorException(int status, Object answer) {
        this.status = status;
        this.answer = answer;

    }

    public int getStatus() {
        return status;
    }

    public Object getAnswer() {
        return answer;
    }
}
