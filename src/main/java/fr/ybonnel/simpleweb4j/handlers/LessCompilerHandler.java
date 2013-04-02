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

import fr.ybonnel.simpleweb4j.exception.CompileErrorException;
import org.lesscss.LessCompiler;
import org.lesscss.LessException;

/**
 * Compiler for less files.
 */
public class LessCompilerHandler extends AbstractCompilerHandler {

    /**
     * Less compiler.
     */
    private LessCompiler compiler = new LessCompiler();

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
            return compiler.compile(source);
        } catch (LessException lessException) {
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
