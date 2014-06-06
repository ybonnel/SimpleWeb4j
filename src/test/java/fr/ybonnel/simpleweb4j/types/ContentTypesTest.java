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
package fr.ybonnel.simpleweb4j.types;

import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Paths;

import static fr.ybonnel.simpleweb4j.types.ContentTypes.get;
import static org.junit.Assert.assertEquals;

public class ContentTypesTest {

    @Before
    public void dummyTest() throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        // just for coverage.
        Constructor<ContentTypes> constructor = ContentTypes.class.getDeclaredConstructor();
        constructor.setAccessible(true);
        constructor.newInstance();
    }

    @Test
    public void find_content_type_from_extension() {
        assertEquals(get(Paths.get("index.html")), "text/html;charset=UTF-8");
        assertEquals(get(Paths.get("data.xml")), "application/xml;charset=UTF-8");
        assertEquals(get(Paths.get("style.css")), "text/css;charset=UTF-8");
        assertEquals(get(Paths.get("style.css.map")), "text/plain;charset=UTF-8");
        assertEquals(get(Paths.get("script.js")), "application/javascript;charset=UTF-8");
        assertEquals(get(Paths.get("text.zip")), "application/zip");
        assertEquals(get(Paths.get("text.gz")), "application/gzip");
        assertEquals(get(Paths.get("text.pdf")), "application/pdf");
        assertEquals(get(Paths.get("image.gif")), "image/gif");
        assertEquals(get(Paths.get("image.jpeg")), "image/jpeg");
        assertEquals(get(Paths.get("image.jpg")), "image/jpeg");
        assertEquals(get(Paths.get("image.png")), "image/png");
        assertEquals(get(Paths.get("font.svg")), "image/svg+xml");
        assertEquals(get(Paths.get("font.eot")), "application/vnd.ms-fontobject");
        assertEquals(get(Paths.get("font.ttf")), "application/x-font-ttf");
        assertEquals(get(Paths.get("font.woff")), "application/x-font-woff");
        assertEquals(get(Paths.get("text.txt")), "text/plain;charset=UTF-8");
        assertEquals(get(Paths.get("unknown")), "text/plain;charset=UTF-8");
    }
}
