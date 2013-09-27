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

import java.util.Collection;
import java.util.Random;

public enum CompanyService {
    INSTANCE;

    protected final static int COMPANIES_NUMBER = 100;

    private CompanyService() {
        for (int i=0;i<COMPANIES_NUMBER;i++) {
            create(generateCompany());
        }
    }

    private final static int SIZE_OF_NAME = 50;
    private Random random = new Random();

    private Company generateCompany() {
        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<SIZE_OF_NAME;i++) {
            nameBuilder.append((char)('a' + random.nextInt(26)));
        }
        Company company = new Company();
        company.name = nameBuilder.toString();
        return company;
    }

    public Company getById(Long id) {
        return Company.simpleEntityManager.getById(id);
    }

    public Collection<Company> getAll() {
        return Company.simpleEntityManager.getAll();
    }

    public void update(Company resource) {
        Company.simpleEntityManager.update(resource);
    }

    public void create(Company resource) {
        Company.simpleEntityManager.save(resource);
    }

    public void delete(Long id) {
        Company.simpleEntityManager.delete(id);
    }
}
