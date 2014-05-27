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

import java.io.IOException;
import java.util.function.Supplier;

/**
 * Stream interface, useful for EventSource.
 * @param <T> Type of one element.
 */
public interface Stream<T> {

    /**
     * Next event.
     * @return next event;
     * @throws IOException if you want to close event-source.
     */
    T next() throws IOException;

    /**
     * Time before next event.
     * @return time before next event in milliseconds.
     */
    int timeBeforeNextEvent();

    /**
     * Create a stream.
     * @param supplier supplier of value.
     * @param timeBetweenNext time before next event in milliseconds.
     * @param <T> type of event.
     * @return constructed stream.
     */
    public static <T> Stream<T> newStream(Supplier<T> supplier, int timeBetweenNext) {
        return new Stream<T>() {
            @Override public T next() throws IOException {
                return supplier.get();
            }

            @Override public int timeBeforeNextEvent() {
                return timeBetweenNext;
            }
        };
    }



}
