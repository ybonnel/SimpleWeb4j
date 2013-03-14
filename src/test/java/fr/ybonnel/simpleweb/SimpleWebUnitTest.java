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

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.fail;

/**
 * This class is used to test function in SimpleWeb which can be test by integration test.
 */
public class SimpleWebUnitTest {

    @Before
    public void setUp() {
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



}
