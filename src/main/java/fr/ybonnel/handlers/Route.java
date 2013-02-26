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
package fr.ybonnel.handlers;

import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

import java.util.List;

import static com.google.common.collect.Lists.newArrayList;

public abstract class Route<P, R> {

    private String path;
    private List<String> pathInSegments;
    private Class<P> paramType;
    private Class<R> returnType;

    public Route(String path, Class<P> paramType, Class<R> returnType) {
        this.path = path;
        pathInSegments = newArrayList(Splitter.on('/').omitEmptyStrings().trimResults().split(path));
        this.paramType = paramType;
        this.returnType = returnType;
    }

    public Class<P> getParamType() {
        return paramType;
    }

    public Class<R> getReturnType() {
        return returnType;
    }

    public boolean isThisPath(String path) {
        if (this.path.equals(path)) {
            return true;
        }

        List<String> queryPath = newArrayList(Splitter.on('/').omitEmptyStrings().trimResults().split(path));

        if (queryPath.size() == pathInSegments.size()) {
            boolean same = true;
            for (int index = 0; index < queryPath.size(); index++) {
                if (!pathInSegments.get(index).startsWith(":")
                        && !pathInSegments.get(index).equals(queryPath.get(index))) {
                    same = false;
                    break;
                }
            }
            return same;
        } else {
            return false;
        }

    }

    public abstract R handle(P param);
}
