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
package fr.ybonnel.simpleweb4j.samples.computers;

import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;

public class ComputerRestResource extends RestResource<Computer> {

    public ComputerRestResource(String resourceRoute) {
        super(resourceRoute, Computer.class);
    }

    @Override
    public Computer getById(String id) throws HttpErrorException {
        Computer computer = ComputerService.INSTANCE.getById(Long.parseLong(id));
        if (computer == null) {
            throw new HttpErrorException(HttpServletResponse.SC_NOT_FOUND);
        }
        return computer;
    }

    @Override
    public Collection<Computer> getAll() throws HttpErrorException {
        return ComputerService.INSTANCE.getAll();
    }

    @Override
    public void update(String id, Computer resource) throws HttpErrorException {
        resource.id = Long.parseLong(id);
        ComputerService.INSTANCE.update(resource);
    }

    @Override
    public void create(Computer resource) throws HttpErrorException {
        ComputerService.INSTANCE.create(resource);
    }

    @Override
    public void delete(String id) throws HttpErrorException {
        ComputerService.INSTANCE.delete(Long.parseLong(id));
    }
}
