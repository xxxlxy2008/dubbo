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
package org.apache.dubbo.metadata;

import org.apache.dubbo.common.extension.SPI;

import java.util.Set;

import static org.apache.dubbo.common.extension.ExtensionLoader.getExtensionLoader;

/**
 * The interface for Dubbo service name Mapping
 *
 * @since 2.7.5
 */
@SPI("default")
public interface ServiceNameMapping {
    // 服务接口、group、version、protocol四部分构成了Service ID，并与当前Service Name之间形成映射，
    // 记录到配置中心
    void map(String serviceInterface, String group, String version, String protocol);

    // 根据服务接口、group、version、protocol四部分构成的Service ID，查询对应的Service Name
    Set<String> get(String serviceInterface, String group, String version, String protocol);

    // 获取默认的ServiceNameMapping接口的扩展实现
    static ServiceNameMapping getDefaultExtension() {
        return getExtensionLoader(ServiceNameMapping.class).getDefaultExtension();
    }
}