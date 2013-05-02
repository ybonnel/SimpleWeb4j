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
package fr.ybonnel.simpleweb4j.model;

import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.*;

public class SimpleEntityManagerUnitTest {

    @Before
    public void setup() throws NoSuchFieldException, IllegalAccessException {
        try {
            SimpleEntityManager.closeSession();
            SimpleEntityManager.setEntitiesClasses(null);
        } catch (Exception ignore){}
    }

    @After
    public void tearDown() throws NoSuchFieldException, IllegalAccessException {
        try {
            SimpleEntityManager.closeSession();
            SimpleEntityManager.setEntitiesClasses(null);
        } catch (Exception ignore){}
    }

    @Test
    public void testOpenAlreadyOpenSession() {
        SimpleEntityManager.openSession();
        try {
            SimpleEntityManager.openSession();
            fail("An exception must be throw");
        } catch (IllegalStateException ignore){

        }
    }

    @Test(expected = IllegalStateException.class)
    public void testCloseSessionAlreadyClosed() {
        SimpleEntityManager.closeSession();
    }

    @Test
    public void stupidTestOnPrivateConstructor() throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, IllegalAccessException, InstantiationException {
        for (Class<?> clazz : SimpleEntityManager.class.getDeclaredClasses()) {
            Constructor<?> constructor = clazz.getDeclaredConstructor();
            constructor.setAccessible(true);
            constructor.newInstance();
        }
    }

    @Test
    public void testHasEntities() {
        SimpleEntityManager.setEntitiesClasses(null);
        assertFalse(SimpleEntityManager.hasEntities());

        SimpleEntityManager.setEntitiesClasses(new ArrayList<Class<?>>());
        assertFalse(SimpleEntityManager.hasEntities());

        SimpleEntityManager.setEntitiesClasses(Arrays.asList(Object.class, String.class));
        assertTrue(SimpleEntityManager.hasEntities());
    }

    @Entity
    private static class Entity1 {
        @Id
        private Long id;
    }

    @Entity
    private static class Entity2 {
        @Id
        private Long id;
    }

    @Test
    public void testGetSessionFactory() throws IllegalAccessException, NoSuchFieldException {
        Field oldAnnotatedClasses = SimpleEntityManager.class.getDeclaredField("oldAnnotatedClasses");
        oldAnnotatedClasses.setAccessible(true);
        oldAnnotatedClasses.set(null, null);
        SimpleEntityManager.setEntitiesClasses(Arrays.<Class<?>>asList(Entity1.class));

        SessionFactory sessionFactory = SimpleEntityManager.getSessionFactory();
        assertNotNull(sessionFactory);
        assertTrue(sessionFactory == SimpleEntityManager.getSessionFactory());
        SimpleEntityManager.setEntitiesClasses(Arrays.<Class<?>>asList(Entity1.class));
        assertTrue(sessionFactory == SimpleEntityManager.getSessionFactory());
        SimpleEntityManager.setEntitiesClasses(Arrays.<Class<?>>asList(Entity1.class, Entity2.class));
        assertFalse(sessionFactory == SimpleEntityManager.getSessionFactory());
    }
}
