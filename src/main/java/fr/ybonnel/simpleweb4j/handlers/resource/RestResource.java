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
package fr.ybonnel.simpleweb4j.handlers.resource;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.Response;
import fr.ybonnel.simpleweb4j.handlers.Route;
import fr.ybonnel.simpleweb4j.handlers.RouteParameters;

import java.util.Collection;

public abstract class RestResource<T> {

    private Class<T> resourceType;
    private String resourceRoute;

    protected RestResource(String resourceRoute, Class<T> resourceType) {
        this.resourceType = resourceType;
        this.resourceRoute = resourceRoute;
        initRoutes();
    }

    public abstract T getById(String id) throws HttpErrorException;
    public abstract Collection<T> getAll() throws HttpErrorException;
    public abstract void update(String id, T resource) throws HttpErrorException;
    public abstract void create(T resource) throws HttpErrorException;
    public abstract void delete(String id) throws HttpErrorException;

    private Route<String, T> routeGetById;
    private Route<Void, Collection<T>> routeGetAll;
    private Route<T, Void> routeCreate;
    private Route<Void, Void> routeDelete;
    private Route<T, Void> routeUpdate;

    private void initRoutes() {
        routeGetById = new Route<String, T>(resourceRoute + "/:id", String.class) {
            @Override
            public Response<T> handle(String param, RouteParameters routeParams) throws HttpErrorException {
                String id = routeParams.getParam("id");
                return new Response<>(getById(id));
            }
        };

        routeCreate = new Route<T, Void>(resourceRoute, resourceType) {
            @Override
            public Response<Void> handle(T param, RouteParameters routeParams) throws HttpErrorException {
                create(param);
                return new Response<>(null);
            }
        };

        routeDelete = new Route<Void, Void>(resourceRoute + "/:id", Void.class) {
            @Override
            public Response<Void> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                delete(routeParams.getParam("id"));
                return new Response<>(null);
            }
        };

        routeUpdate = new Route<T, Void>(resourceRoute + "/:id", resourceType) {
            @Override
            public Response<Void> handle(T param, RouteParameters routeParams) throws HttpErrorException {
                update(routeParams.getParam("id"), param);
                return new Response<>(null);
            }
        };

        routeGetAll = new Route<Void, Collection<T>>(resourceRoute, Void.class) {
            @Override
            public Response<Collection<T>> handle(Void param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>(getAll());
            }
        };
    }

    public Route<String,T> routeGetById() {
        return routeGetById;
    }

    public Route<T, Void> routeCreate() {
        return routeCreate;
    }

    public Route<Void, Void> routeDelete() {
        return routeDelete;
    }

    public Route<T, Void> routeUpdate() {
        return routeUpdate;
    }

    public Route<Void, Collection<T>> routeGetAll() {
        return routeGetAll;
    }
}
