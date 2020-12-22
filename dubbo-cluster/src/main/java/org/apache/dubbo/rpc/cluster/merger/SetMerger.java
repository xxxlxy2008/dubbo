/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.dubbo.rpc.cluster.merger;

import org.apache.dubbo.common.utils.ArrayUtils;
import org.apache.dubbo.rpc.cluster.Merger;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

public class SetMerger implements Merger<Set<?>> {

    @Override
    public Set<Object> merge(Set<?>... items) {
        if (ArrayUtils.isEmpty(items)) {
            // 空结果集时，这就返回空Set集合
            return Collections.emptySet();
        }
        // 创建一个新的HashSet集合，传入的所有Set集合都添加到result中
        Set<Object> result = new HashSet<Object>();
        Stream.of(items).filter(Objects::nonNull).forEach(result::addAll);
        return result;
    }
}
