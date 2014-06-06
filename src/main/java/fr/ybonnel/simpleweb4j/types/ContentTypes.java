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

import java.nio.file.Path;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Content type getter from a path.
 */
public final class ContentTypes {

    /**
     * Private constructor to avoid instanciate.
     */
    private ContentTypes() {
    }

    /**
     * Get extension of a file.
     * @param path file.
     * @return the extension.
     */
    public static String extension(Path path) {
        String filename = path.toString();
        int dotIndex = filename.lastIndexOf('.');
        if (dotIndex <= 0) {
            return "";
        }
        return filename.substring(dotIndex);
    }

    /**
     * Map of content types.
     */
    private static ConcurrentHashMap<String, String> contentTypesMap = new ConcurrentHashMap<String, String>() { {
        put(".html", "text/html;charset=UTF-8");
        put(".xml", "application/xml;charset=UTF-8");
        put(".css", "text/css;charset=UTF-8");
        put(".js", "application/javascript;charset=UTF-8");
        put(".zip", "application/zip");
        put(".gz", "application/gzip");
        put(".pdf", "application/pdf");
        put(".gif", "image/gif");
        put(".jpeg", "image/jpeg");
        put(".jpg", "image/jpeg");
        put(".png", "image/png");
        put(".svg", "image/svg+xml");
        put(".eot", "application/vnd.ms-fontobject");
        put(".ttf", "application/x-font-ttf");
        put(".woff", "application/x-font-woff");
    } };

    /**
     * Get content-type from an URI.
     * @param path uri.
     * @return extension.
     */
    public static String get(Path path) {
        return contentTypesMap.getOrDefault(extension(path), "text/plain;charset=UTF-8");
    }
}
