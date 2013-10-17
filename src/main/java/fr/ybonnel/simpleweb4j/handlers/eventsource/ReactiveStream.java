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
 * Reactive stream.
 * Can be use to have a reactive stream.
 * @param <T> Type of one element.
 */
public interface ReactiveStream<T> {

    /**
     * Method called by SimpleWeb4J to pass the handler.
     * You must call {@link ReactiveHandler#next(Object)} to send next event.
     * You can call {@link ReactiveHandler#close()} to close the stream.
     * @param reactiveHandler the handler.
     */
    void setReactiveHandler(ReactiveHandler<T> reactiveHandler);

}
