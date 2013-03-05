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
package fr.ybonnel.simpleweb.samples.computers;

import fr.ybonnel.simpleweb.exception.HttpErrorException;
import fr.ybonnel.simpleweb.handlers.resource.RestResource;

import javax.servlet.http.HttpServletResponse;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public class ComputerRestResource extends RestResource<Computer> {

    public ComputerRestResource(String resourceRoute) {
        super(resourceRoute, Computer.class);
    }

    private Map<Long, Computer> computers = new HashMap<>();
    private AtomicLong computerIdGenerator = new AtomicLong(0);
    private AtomicLong companyIdGenerator = new AtomicLong(0);

    @Override
    public Computer getById(String id) throws HttpErrorException {
        return computers.get(Long.parseLong(id));
    }

    @Override
    public Collection<Computer> getAll() throws HttpErrorException {
        return computers.values();
    }

    @Override
    public void update(String id, Computer resource) throws HttpErrorException {
        if (computers.containsKey(Long.parseLong(id))) {
            Computer computer = computers.get(Long.parseLong(id));
            computer.name = resource.name;
            computer.introduced = resource.introduced;
            computer.discontinued = resource.discontinued;
            computer.company = resource.company;
        } else {
            throw new HttpErrorException(HttpServletResponse.SC_NOT_FOUND);
        }
    }

    @Override
    public void create(Computer resource) throws HttpErrorException {
        resource.id = computerIdGenerator.incrementAndGet();
        if (resource.company != null) {
            resource.company.id = companyIdGenerator.incrementAndGet();
        }
    }

    @Override
    public void delete(String id) throws HttpErrorException {
        computers.remove(Long.parseLong(id));
    }
}
