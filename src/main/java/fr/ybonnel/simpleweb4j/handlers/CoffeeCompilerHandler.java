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
import org.jcoffeescript.JCoffeeScriptCompileException;
import org.jcoffeescript.JCoffeeScriptCompiler;
import org.jcoffeescript.Option;

import static java.util.Arrays.asList;

/**
 * Compiler for coffee files.
 */
public class CoffeeCompilerHandler extends AbstractCompilerHandler {

    /**
     * CoffeeScript compiler.
     */
    private JCoffeeScriptCompiler compiler = new JCoffeeScriptCompiler(asList(Option.BARE));


    /**
     * Suffixe name for CoffeeScript file is ".coffee".
     * @return always ".coffee".
     */
    @Override
    protected String getSuffixeName() {
        return ".coffee";
    }

    /**
     * Compile a CoffeeScript file.
     * @param source source to compile.
     * @return the compiled javascript result.
     * @throws CompileErrorException in case of compile error.
     */
    @Override
    protected String compile(String source) throws CompileErrorException {
        try {
            return compiler.compile(source);
        } catch (JCoffeeScriptCompileException coffeeCompileException) {
            throw new CompileErrorException(coffeeCompileException);
        }
    }

    /**
     * Content type for CoffeeScript compiled is "application/javascript".
     * @return always "application/javascript".
     */
    @Override
    protected String getContentType() {
        return "application/javascript";
    }
}
