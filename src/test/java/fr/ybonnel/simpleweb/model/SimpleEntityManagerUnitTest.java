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
package fr.ybonnel.simpleweb.model;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Field;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class SimpleEntityManagerUnitTest {

    @Before
    public void setup() {
        SimpleEntityManager.setEntitiesPackage(null);
        try {
            SimpleEntityManager.closeSession();
        } catch (Exception ignore){}
    }

    @After
    public void tearDown() {
        try {
            SimpleEntityManager.closeSession();
        } catch (Exception ignore){};
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
    public void testActualEntitiesPackageHasChange() throws NoSuchFieldException, IllegalAccessException {
        Field actualEntitiesPackage = SimpleEntityManager.class.getDeclaredField("actualEntitiesPackage");
        actualEntitiesPackage.setAccessible(true);
        actualEntitiesPackage.set(null, null);
        SimpleEntityManager.setEntitiesPackage(null);
        assertFalse(SimpleEntityManager.actualEntitiesPackageHasChange());

        SimpleEntityManager.setEntitiesPackage("test");
        assertTrue(SimpleEntityManager.actualEntitiesPackageHasChange());

        actualEntitiesPackage.set(null, "test");
        SimpleEntityManager.setEntitiesPackage(null);
        assertTrue(SimpleEntityManager.actualEntitiesPackageHasChange());

        SimpleEntityManager.setEntitiesPackage("test2");
        assertTrue(SimpleEntityManager.actualEntitiesPackageHasChange());

        SimpleEntityManager.setEntitiesPackage("test");
        assertFalse(SimpleEntityManager.actualEntitiesPackageHasChange());
    }
}
