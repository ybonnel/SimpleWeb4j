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
     * Get content-type from an URI.
     * @param path uri.
     * @return extension.
     */
    public static String get(Path path) {
        switch (extension(path)) {
            case ".html":
            case ".md":
            case ".markdown":
            case ".asciidoc":
                return "text/html;charset=UTF-8";
            case ".xml":
                return "application/xml;charset=UTF-8";
            case ".css":
            case ".less":
                return "text/css;charset=UTF-8";
            case ".js":
            case ".coffee":
            case ".litcoffee":
                return "application/javascript;charset=UTF-8";
            case ".zip":
                return "application/zip";
            case ".gz":
                return "application/gzip";
            case ".pdf":
                return "application/pdf";
            case ".gif":
                return "image/gif";
            case ".jpeg":
            case ".jpg":
                return "image/jpeg";
            case ".png":
                return "image/png";
            case ".svg":
                return "image/svg+xml";
            case ".eot":
                return "application/vnd.ms-fontobject";
            case ".ttf":
                return "application/x-font-ttf";
            case ".woff":
                return "application/x-font-woff";
            default:
                return "text/plain;charset=UTF-8";
        }
    }
}
