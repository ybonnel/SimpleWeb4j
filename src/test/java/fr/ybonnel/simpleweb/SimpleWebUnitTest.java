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
package fr.ybonnel.simpleweb;

import fr.ybonnel.simpleweb.server.SimpleWebServer;
import org.junit.Before;
import org.junit.Test;
import org.reflections.ReflectionUtils;

import java.lang.reflect.Field;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * This class is used to test function in SimpleWeb which can be test by integration test.
 */
public class SimpleWebUnitTest {

    @Before
    public void setUp() {
        // Stupid test to cover <init>
        new SimpleWeb();

        SimpleWeb.resetDefaultValues();
    }

    @Test
    public void testSetPortAfterInit() {
        SimpleWeb.setPort(9999);
        SimpleWeb.init();
        SimpleWeb.init();
        try {
            SimpleWeb.setPort(1234);
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetPublicResourcesPathAfterInit() {
        SimpleWeb.setPublicResourcesPath("/public");
        SimpleWeb.init();
        SimpleWeb.init();
        try {
            SimpleWeb.setPublicResourcesPath("/other");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test(expected = IllegalStateException.class)
    public void testStopWithoutStart() {
        SimpleWeb.stop();
    }

    @Test
    public void testSetHibernateCfgPathAfterInit() {
        SimpleWeb.setHibernateCfgPath("/fr/ybonnel/simpleweb/entities/hibernate.cfg.xml");
        SimpleWeb.init();
        SimpleWeb.init();
        try {
            SimpleWeb.setHibernateCfgPath("/fr/ybonnel/simpleweb/entities/hibernate.cfg.xml");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testSetEntitiesPackageAfterInit() {
        SimpleWeb.setEntitiesPackage("fr.entities");
        SimpleWeb.init();
        SimpleWeb.init();
        try {
            SimpleWeb.setEntitiesPackage("fr.entities");
            fail("An exception must be throw");
        } catch (IllegalStateException ignore) {
        }
    }

    @Test
    public void testStartMethod() throws IllegalAccessException, NoSuchFieldException {
        SimpleWebServer mockServer = mock(SimpleWebServer.class);

        SimpleWeb.init();
        Field serverField = SimpleWeb.class.getDeclaredField("server");
        serverField.setAccessible(true);
        serverField.set(null, mockServer);

        SimpleWeb.start();

        verify(mockServer).start(true);
    }
}
