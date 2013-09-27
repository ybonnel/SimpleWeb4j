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
package fr.ybonnel.simpleweb4j.samples.forms;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;
import fr.ybonnel.simpleweb4j.samples.forms.model.Countries;

import java.util.List;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;

public class Forms {

    public static void startServer(int port) {
        setPort(port);
        setPublicResourcesPath("/fr/ybonnel/simpleweb4j/samples/forms/public");

        get(new Route<Void, List<String>>("countries", Void.class) {
            @Override
            public Response<List<String>> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>(Countries.list());
            }
        });

        start();
    }

    public static void main(String[] args) {
        startServer(9999);

    }
}
