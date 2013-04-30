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

public class CompanyRestResource extends RestResource<Company> {

    public CompanyRestResource(String resourceRoute) {
        super(resourceRoute, Company.class);
    }

    @Override
    public Company getById(String id) throws HttpErrorException {
        Company company = CompanyService.INSTANCE.getById(Long.parseLong(id));
        if (company == null) {
            throw new HttpErrorException(HttpServletResponse.SC_NOT_FOUND);
        }
        return company;
    }

    @Override
    public Collection<Company> getAll() throws HttpErrorException {
        return CompanyService.INSTANCE.getAll();
    }

    @Override
    public void update(String id, Company resource) throws HttpErrorException {
        resource.id = Long.parseLong(id);
        CompanyService.INSTANCE.update(resource);
    }

    @Override
    public Company create(Company resource) throws HttpErrorException {
        CompanyService.INSTANCE.create(resource);
        return resource;
    }

    @Override
    public void delete(String id) throws HttpErrorException {
        CompanyService.INSTANCE.delete(Long.parseLong(id));
    }
}
