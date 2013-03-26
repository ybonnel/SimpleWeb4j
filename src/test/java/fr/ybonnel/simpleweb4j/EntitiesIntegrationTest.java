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
package fr.ybonnel.simpleweb4j;


import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import fr.ybonnel.simpleweb4j.entities.SimpleEntity;
import fr.ybonnel.simpleweb4j.exception.HttpErrorException;
import fr.ybonnel.simpleweb4j.handlers.resource.RestResource;
import fr.ybonnel.simpleweb4j.model.SimpleEntityManager;
import fr.ybonnel.simpleweb4j.util.SimpleWebTestUtil;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Collection;
import java.util.Random;

import static fr.ybonnel.simpleweb4j.SimpleWeb4j.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EntitiesIntegrationTest {

    private Random random = new Random();
    private SimpleWebTestUtil testUtil;


    @Before
    public void startServer() {
        resetDefaultValues();
        int port = Integer.getInteger("test.http.port", random.nextInt(10000) + 10000);
        setPort(port);
        testUtil = new SimpleWebTestUtil(port);
        setHibernateCfgPath("/fr/ybonnel/simpleweb4j/entities/hibernate.cfg.xml");

        resource(new RestResource<SimpleEntity>("entity", SimpleEntity.class) {
            @Override
            public SimpleEntity getById(String id) throws HttpErrorException {
                return SimpleEntity.simpleEntityManager.getById(Long.parseLong(id));
            }

            @Override
            public Collection<SimpleEntity> getAll() throws HttpErrorException {
                return SimpleEntity.simpleEntityManager.getAll();
            }

            @Override
            public void update(String id, SimpleEntity resource) throws HttpErrorException {
                resource.id = Long.parseLong(id);
                SimpleEntity.simpleEntityManager.update(resource);
            }

            @Override
            public void create(SimpleEntity resource) throws HttpErrorException {
                SimpleEntity.simpleEntityManager.save(resource);
            }

            @Override
            public void delete(String id) throws HttpErrorException {
                SimpleEntity.simpleEntityManager.delete(Long.parseLong(id));
            }
        });

        start(false);

        SimpleEntityManager.openSession();
        SimpleEntityManager.getCurrentSession().beginTransaction();

        for (SimpleEntity entity : SimpleEntity.simpleEntityManager.getAll()) {
            SimpleEntity.simpleEntityManager.delete(entity.id);
        }

        SimpleEntityManager.getCurrentSession().getTransaction().commit();
        SimpleEntityManager.closeSession();
    }

    @After
    public void stopServer() {
        stop();
    }

    @Test
    public void should_manage_an_entity() throws Exception {



        SimpleWebTestUtil.UrlResponse response = testUtil.doMethod("GET", "/entity");
        assertEquals("[]", response.body);
        assertEquals(200, response.status);

        response = testUtil.doMethod("POST", "/entity", "{name:\"nom\"}");
        assertEquals("", response.body);
        assertEquals(201, response.status);

        response = testUtil.doMethod("GET", "/entity");
        assertEquals(200, response.status);
        Collection<SimpleEntity> entities = new GsonBuilder().create().fromJson(response.body, new TypeToken<Collection<SimpleEntity>>(){}.getType());
        assertEquals(1, entities.size());
        SimpleEntity entity = entities.iterator().next();
        assertEquals("nom", entity.name);

        response = testUtil.doMethod("GET", "/entity/" + entity.id);
        assertEquals(200, response.status);
        SimpleEntity newEntity = new GsonBuilder().create().fromJson(response.body, SimpleEntity.class);
        assertEquals(entity.id, newEntity.id);
        assertEquals(entity.name, newEntity.name);

        response = testUtil.doMethod("PUT", "/entity/" + entity.id, "{name:\"newName\"}");
        assertEquals(204, response.status);

        response = testUtil.doMethod("GET", "/entity/" + entity.id);
        assertEquals(200, response.status);
        newEntity = new GsonBuilder().create().fromJson(response.body, SimpleEntity.class);
        assertEquals(entity.id, newEntity.id);
        assertEquals("newName", newEntity.name);

        // Delete (DELETE)
        response = testUtil.doMethod("DELETE", "/entity/" + entity.id);
        assertEquals(204, response.status);

        response = testUtil.doMethod("GET", "/entity");
        assertEquals(200, response.status);
        entities = new GsonBuilder().create().fromJson(response.body, new TypeToken<Collection<SimpleEntity>>(){}.getType());
        assertTrue(entities.isEmpty());
    }
}
