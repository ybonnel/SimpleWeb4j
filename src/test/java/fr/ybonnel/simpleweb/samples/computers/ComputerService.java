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

import org.apache.commons.math.random.RandomData;
import org.apache.commons.math.random.RandomDataImpl;

import java.util.Collection;
import java.util.Date;
import java.util.Random;

public enum ComputerService {
    INSTANCE;

    private static final int COMPUTERS_NUMBER = 1000;

    private final static int SIZE_OF_NAME = 30;
    private RandomData randomData = new RandomDataImpl();
    private Random random = new Random();

    private ComputerService() {
        // Generate companies
        CompanyService.INSTANCE.getAll();

        for (int i=0;i<COMPUTERS_NUMBER;i++) {
            create(generateComputer());
        }
    }

    private Date generateDate(Date min, Date max) {
        return new Date(randomData.nextLong(min.getTime(), max.getTime()));
    }


    private Date minDate = new Date(0L);
    private Date maxDate = new Date();

    private Computer generateComputer() {
        Computer computer = new Computer();

        StringBuilder nameBuilder = new StringBuilder();
        for (int i=0; i<SIZE_OF_NAME;i++) {
            nameBuilder.append((char)('a' + random.nextInt(26)));
        }
        computer.name = nameBuilder.toString();

        boolean generateIntroduced = false;
        if (random.nextBoolean()) {
            generateIntroduced = true;
            computer.introduced = generateDate(minDate, maxDate);
        }

        if (generateIntroduced && random.nextBoolean()) {
            computer.discontinued = generateDate(computer.introduced, maxDate);
        }

        if (random.nextBoolean()) {
            computer.company = CompanyService.INSTANCE.getById((long)random.nextInt(CompanyService.COMPANIES_NUMBER)+1);
        }

        return computer;
    }

    public Computer getById(Long id) {
        return Computer.simpleEntityManager.getById(id);
    }

    public Collection<Computer> getAll() {
        return Computer.simpleEntityManager.getAll();
    }

    public void update(Computer resource) {
        if (resource.company != null) {
            resource.company = Company.simpleEntityManager.getById(resource.company.id);
        }
        Computer.simpleEntityManager.update(resource);
    }

    public void create(Computer resource) {
        Computer.simpleEntityManager.save(resource);
    }

    public void delete(Long id) {
        Computer.simpleEntityManager.delete(id);
    }
}
