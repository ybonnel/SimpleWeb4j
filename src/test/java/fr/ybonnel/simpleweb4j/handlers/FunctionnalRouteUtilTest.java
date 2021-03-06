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
package fr.ybonnel.simpleweb4j.handlers;

import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class FunctionnalRouteUtilTest {


    @Test
    public void stupidTestForCoverage() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        Constructor<FunctionnalRouteUtil> privateConstructor = FunctionnalRouteUtil.class.getDeclaredConstructor();
        assertFalse(privateConstructor.isAccessible());
        privateConstructor.setAccessible(true);
        FunctionnalRouteUtil instance = privateConstructor.newInstance();
        assertNotNull(instance);
    }

}
