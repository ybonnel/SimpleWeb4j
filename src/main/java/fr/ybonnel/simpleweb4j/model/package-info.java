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
 * This package contains the Entity part of SimpleWeb4j.<br/>
 * SimpleWeb4j use hibernate, it has a default configuration with a H2 in memory database,
 * but it's strongly recommended to add your own configuration and
 * use it with {@link fr.ybonnel.simpleweb4j.SimpleWeb4j#setHibernateCfgPath(String)}.<br/>
 * By default, SimpleWeb4J scan the entire classPath to find classes annotated with {@link javax.persistence.Entity}.<br/>
 *
 * SimpleWeb4j have a simple manager to help you with save/delete/get operations : {@link fr.ybonnel.simpleweb4j.model.SimpleEntityManager}.
 */
package fr.ybonnel.simpleweb4j.model;
