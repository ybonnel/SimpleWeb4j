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

import com.github.sommeri.less4j.Less4jException;
import com.github.sommeri.less4j.LessCompiler;
import com.github.sommeri.less4j.core.DefaultLessCompiler;
import fr.ybonnel.simpleweb4j.exception.CompileErrorException;

/**
 * Compiler for less files.
 */
public class LessCompilerHandler extends AbstractCompilerHandler {

    /**
     * Singleton helper.
     */
    private static class LessCompilerHelper {
        /**
         * Less compiler.
         */
        //CHECKSTYLE:OFF
        public static final LessCompiler compiler = new DefaultLessCompiler();
        //CHECKSTYLE:ON
    }

    /**
     * Suffixe name for less file is ".less".
     * @return always ".less".
     */
    @Override
    protected String getSuffixeName() {
        return ".less";
    }

    /**
     * Compile a less file.
     * @param source source to compile.
     * @return the compiled css result.
     * @throws CompileErrorException in case of compile error.
     */
    @Override
    protected String compile(String source) throws CompileErrorException {
        try {
            return LessCompilerHelper.compiler.compile(source).getCss();
        } catch (Less4jException lessException) {
            throw new CompileErrorException(lessException);
        }
    }

    /**
     * Content type for less compiled is "text/css".
     * @return always "text/css".
     */
    @Override
    protected String getContentType() {
        return "text/css";
    }
}
