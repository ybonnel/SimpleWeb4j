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
/**
 * SimpleWeb4j is a simple web framework for java.<br/>
 * On server side, you can have json resource.<br/>
 * On client side, you can use angular.js (or any other MVC javascript framework).<br/>
 * On can have a look on samples in package fr.ybonnel.simpleweb4j.samples in test resources.<br/>
 *
 * The entry class is {@link SimpleWeb4j} :
 * <ul>
 *     <li>{@link SimpleWeb4j#setPort(int)} to change the http port.</li>
 *     <li>{@link SimpleWeb4j#setPublicResourcesPath(String)} to change the path yo your public resources like html files.</li>
 *     <li>{@link fr.ybonnel.simpleweb4j.SimpleWeb4j#start()} to start the server.</li>
 *     <li>{@link fr.ybonnel.simpleweb4j.SimpleWeb4j#stop()} to stop the server.</li>
 *     <li>{@link SimpleWeb4j#resource(fr.ybonnel.simpleweb4j.handlers.resource.RestResource)} to add a Rest resource.</li>
 *     <li>{@link SimpleWeb4j#get(fr.ybonnel.simpleweb4j.handlers.Route)} to add a route on GET method.</li>
 *     <li>{@link SimpleWeb4j#post(fr.ybonnel.simpleweb4j.handlers.Route)} to add a route on POST method.</li>
 *     <li>{@link SimpleWeb4j#put(fr.ybonnel.simpleweb4j.handlers.Route)} to add a route on PUT method.</li>
 *     <li>{@link SimpleWeb4j#delete(fr.ybonnel.simpleweb4j.handlers.Route)} to add a route on DELETE method.</li>
 * </ul>
 *
 * If you have entities to manage, you can see {@link fr.ybonnel.simpleweb4j.model.SimpleEntityManager}.
 *
 */
package fr.ybonnel.simpleweb4j;
