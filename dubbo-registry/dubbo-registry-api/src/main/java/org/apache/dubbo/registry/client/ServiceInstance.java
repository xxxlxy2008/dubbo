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
package org.apache.dubbo.registry.client;

import java.io.Serializable;
import java.util.Map;

/**
 * The model class of an instance of a service, which is used for service registration and discovery.
 * <p>
 *
 * @since 2.7.5
 */
public interface ServiceInstance extends Serializable {
    // 唯一标识
    String getId();

    // 获取当前ServiceInstance所属的Service Name
    String getServiceName();

    // 获取当前ServiceInstance的host
    String getHost();

    // 获取当前ServiceInstance的port
    Integer getPort();

    // 当前ServiceInstance的状态
    default boolean isEnabled() {
        return true;
    }

    // 检测当前ServiceInstance的状态
    default boolean isHealthy() {
        return true;
    }

    // 获取当前ServiceInstance关联的元数据，这些元数据以KV格式存储
    Map<String, String> getMetadata();

    // 计算当前ServiceInstance对象的hashCode值
    int hashCode();

    // 比较两个ServiceInstance对象
    boolean equals(Object another);
}
