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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

public enum CompanyService {
    INSTANCE;

    private Map<Long, Company> companies = new HashMap<>();
    private AtomicLong idGenerator = new AtomicLong(0);

    private CompanyService() {
        Company apple = new Company();
        apple.name = "Apple Inc.";
        Company atari = new Company();
        atari.name = "Atari";
        create(apple);
        create(atari);
    }

    public Company getById(Long id) {
        return companies.get(id);
    }

    public Collection<Company> getAll() {
        return companies.values();
    }

    public Company update(Company resource) {
        if (companies.containsKey(resource.id)) {
            Company company = companies.get(resource.id);
            company.name = resource.name;
            return company;
        } else {
            return null;
        }
    }

    public void create(Company resource) {
        resource.id = idGenerator.incrementAndGet();
        companies.put(resource.id, resource);
    }

    public Company delete(Long id) {
        return companies.remove(id);
    }
}
