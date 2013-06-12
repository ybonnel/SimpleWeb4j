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

import fr.ybonnel.simpleweb4j.entities.SimpleEntity;
import fr.ybonnel.simpleweb4j.exception.FatalSimpleWeb4jException;
import fr.ybonnel.simpleweb4j.handlers.LessCompilerHandler;
import fr.ybonnel.simpleweb4j.server.SimpleWeb4jServer;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * This class is used to test function in SimpleWeb4j which can be test by integration test.
 */
public class SimpleWebUnitTest {

    @Before
    public void setUp() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // Stupid test to cover <init>

        Constructor<SimpleWeb4j> privateConstructor = SimpleWeb4j.class.getDeclaredConstructor();
        privateConstructor.setAccessible(true);
        privateConstructor.newInstance();

        SimpleWeb4j.resetDefaultValues();
    }

    @Test
    public void testSetPortAfterInit() {
        SimpleWeb4j.setPort(9999);
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.setPort(1234);
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetPublicResourcesPathAfterInit() {
        SimpleWeb4j.setPublicResourcesPath("/public");
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.setPublicResourcesPath("/other");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetExternalPublicResourcesPathAfterInit() {
        SimpleWeb4j.setExternalPublicResourcesPath("/public");
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.setExternalPublicResourcesPath("/other");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetExternalPublicResourcesPathWithWrongPath() {
        SimpleWeb4j.setExternalPublicResourcesPath("file://sdjfkl://toto");
        try {
            SimpleWeb4j.init();
            fail("An exception must be throw");
        } catch (FatalSimpleWeb4jException ignore) {
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testStopWithoutStart() {
        SimpleWeb4j.stop();
    }

    @Test
    public void testSetHibernateCfgPathAfterInit() {
        SimpleWeb4j.setHibernateCfgPath("/fr/ybonnel/simpleweb4j/entities/hibernate.cfg.xml");
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.setHibernateCfgPath("/fr/ybonnel/simpleweb4j/entities/hibernate.cfg.xml");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetEntitiesClassesAfterInit() {
        SimpleWeb4j.setEntitiesClasses(SimpleEntity.class);
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.setEntitiesClasses(SimpleEntity.class);
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testAddSpecificHandlerAfterInit() {
        SimpleWeb4j.addSpecificHandler(new LessCompilerHandler());
        SimpleWeb4j.init();
        SimpleWeb4j.init();
        try {
            SimpleWeb4j.addSpecificHandler(new LessCompilerHandler());
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testStartMethod() throws IllegalAccessException, NoSuchFieldException {
        SimpleWeb4jServer mockServer = mock(SimpleWeb4jServer.class);

        SimpleWeb4j.init();
        Field serverField = SimpleWeb4j.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);

        SimpleWeb4j.start();

        verify(mockServer).start(true);
    }
}
