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
package fr.ybonnel.simpleweb4j.handlers.eventsource;

/**
 * Handler use to send next object for a reactive stream.
 * @param <T> Type of one element.
 */
public interface ReactiveHandler<T> {

    /**
     * Method to call to send next object to the stream.
     * @param object object to send.
     * @throws EndOfStreamException exception used to inform of end of stream.
     */
    void next(T object) throws EndOfStreamException;

    /**
     * Method to call to close the stream.
     */
    void close();
}
