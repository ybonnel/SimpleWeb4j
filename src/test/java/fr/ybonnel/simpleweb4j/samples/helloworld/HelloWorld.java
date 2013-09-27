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
package fr.ybonnel.simpleweb4j.samples.helloworld;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPort;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.setPublicResourcesPath;
import static fr.ybonnel.simpleweb4j.SimpleWeb4j.start;

public class HelloWorld {

    public static void startServer(int port) {
        setPort(port);
        setPublicResourcesPath("/fr/ybonnel/simpleweb4j/samples/helloworld");
        start();
    }

    public static void main(String[] args) {
        startServer(9999);
    }
}
