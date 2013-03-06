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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public enum ComputerService {
    INSTANCE;

    private Map<Long, Computer> computers = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong(0);

    private ComputerService() {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            Computer apple1 = new Computer();
            apple1.name = "Apple I";
            apple1.introduced = sdf.parse("01/04/1976");
            apple1.discontinued = sdf.parse("01/10/1977");
            apple1.company = CompanyService.INSTANCE.getById(1L);

            Computer apple2 = new Computer();
            apple2.name = "Apple II";
            apple2.introduced = sdf.parse("01/04/1977");
            apple2.discontinued = sdf.parse("01/10/1993");
            apple2.company = CompanyService.INSTANCE.getById(1L);

            create(apple1);
            create(apple2);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public Computer getById(Long id) {
        return computers.get(id);
    }

    public Collection<Computer> getAll() {
        return computers.values();
    }

    public Computer update(Computer resource) {
        if (computers.containsKey(resource.id)) {
            Computer computer = computers.get(resource.id);
            computer.name = resource.name;
            computer.introduced = resource.introduced;
            computer.discontinued = resource.discontinued;
            if (resource.company == null || resource.company.id == null) {
                computer.company = null;
            } else {
                computer.company = CompanyService.INSTANCE.getById(resource.company.id);
            }
            return computer;
        } else {
            return null;
        }
    }

    public void create(Computer resource) {
        resource.id = idGenerator.incrementAndGet();
        if (resource.company == null || resource.company.id == null) {
            resource.company = null;
        } else {
            resource.company = CompanyService.INSTANCE.getById(resource.company.id);
        }
        computers.put(resource.id, resource);
    }

    public Computer delete(Long id) {
        return computers.remove(id);
    }
}
