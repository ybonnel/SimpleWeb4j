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

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

/**
 * Class to represent a REST Resource.
 * @see fr.ybonnel.simpleweb4j.SimpleWeb4j#resource(RestResource)
 * @param <T> Type of the resource.
 */
public abstract class RestResource<T> {

    /**
     * Class of the resource.
     */
    private Class<T> resourceType;
    /**
     * Route of the resource.
     */
    private String resourceRoute;

    /**
     * Constructor.
     * @param resourceRoute route of the resource.
     * @param resourceType class of the resource.
     */
    protected RestResource(String resourceRoute, Class<T> resourceType) {
        this.resourceType = resourceType;
        this.resourceRoute = resourceRoute;
        initRoutes();
    }

    /**
     * Method to implement for the get by id.
     * This method will be invoked when a GET on "/<resourceRoute>/<id>" is detected.
     * @param id id of the resource.
     * @return the resource.
     * @throws HttpErrorException any error you want to throw, for example, you can throw a new HttpErrorException(404) if resource is not found.
     */
    public abstract T getById(String id) throws HttpErrorException;

    /**
     * Method to implement for the get all.
     * This method will be invoked when a GET on "/<resourceRoute>" is detected.
     * @return the list of all resources.
     * @throws HttpErrorException any error you want to throw, for example,
     * you can throw a new HttpErrorException(204) if there is no resources (no content).
     */
    public abstract Collection<T> getAll() throws HttpErrorException;

    /**
     * Method to implement for the update method.
     * This method will be invoked when a PUT on "/<resourceRoute>/<id>" is detected.
     * @param id the id of the resource to update.
     * @param resource the resource.
     * @throws HttpErrorException any error you want to throw, for example, you can throw a new HttpErrorException(404) if resource is not found.
     */
    public abstract void update(String id, T resource) throws HttpErrorException;

    /**
     * Method to implement to create of a resource.
     * This method will be invoked when a POST on "/<resourceRoute>" is detected.
     * @param resource resource to create.
     * @return result of creation.
     * @throws HttpErrorException any error you want to throw, for example,
     * you can throw a new HttpErrorException(400) if a required attribute is missing (bad request).
     */
    public abstract T create(T resource) throws HttpErrorException;

    /**
     * Method to implement to delete a resource.
     * This method will be invoked when a DELETE on "/<resourceRoute>/<id>" is detected.
     * @param id id of the resource to delete.
     * @throws HttpErrorException any error you want to throw, for example, you can throw a new HttpErrorException(404) if resource is not found.
     */
    public abstract void delete(String id) throws HttpErrorException;

    /**
     * Route for getById.
     */
    private Route<String, T> routeGetById;
    /**
     * Route for getAll.
     */
    private Route<Void, Collection<T>> routeGetAll;
    /**
     * Route for create.
     */
    private Route<T, T> routeCreate;
    /**
     * Route for delete.
     */
    private Route<Void, Void> routeDelete;
    /**
     * Route for update.
     */
    private Route<T, Void> routeUpdate;

    /**
     * Initialise the routes.
     */
    private void initRoutes() {
        routeGetById = new Route<String, T>(resourceRoute + "/:id", String.class) {
            @Override
            public Response<T> handle(String param, RouteParameters routeParams) throws HttpErrorException {
                String id = routeParams.getParam("id");
                return new Response<>(getById(id));
            }
        };

        routeCreate = new Route<T, T>(resourceRoute, resourceType) {
            @Override
            public Response<T> handle(T param, RouteParameters routeParams) throws HttpErrorException {
                return new Response<>(create(param), HttpServletResponse.SC_CREATED);
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

    /**
     * Get the route of getById (don't use it directly).
     * @return the route of getById.
     */
    public Route<String, T> routeGetById() {
        return routeGetById;
    }

    /**
     * Get the route of create (don't use it directly).
     * @return the route of create.
     */
    public Route<T, T> routeCreate() {
        return routeCreate;
    }

    /**
     * Get the route of delete (don't use it directly).
     * @return the route of delete.
     */
    public Route<Void, Void> routeDelete() {
        return routeDelete;
    }

    /**
     * Get the route of update (don't use it directly).
     * @return the route of update.
     */
    public Route<T, Void> routeUpdate() {
        return routeUpdate;
    }

    /**
     * Get the route of getAll (don't use it directly).
     * @return the route of getAll.
     */
    public Route<Void, Collection<T>> routeGetAll() {
        return routeGetAll;
    }
}
