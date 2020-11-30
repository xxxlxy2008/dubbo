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
package org.apache.dubbo.common.serialize;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.extension.Adaptive;
import org.apache.dubbo.common.extension.SPI;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Serialization strategy interface that specifies a serializer. (SPI, Singleton, ThreadSafe)
 *
 * The default extension is hessian2 and the default serialization implementation of the dubbo protocol.
 * <pre>
 *     e.g. &lt;dubbo:protocol serialization="xxx" /&gt;
 * </pre>
 */
@SPI("hessian2") // 被@SPI注解修饰，默认是使用hessian2序列化算法
public interface Serialization {

    // 获取ContentType的ID值，是一个byte类型的值，唯一确定一个算法
    byte getContentTypeId();

    // 每一种序列化算法都对应一个ContentType，该方法用于获取ContentType
    String getContentType();

    // 创建一个ObjectOutput对象，ObjectOutput负责实现序列化的功能，即将Java
    // 对象转化为字节序列
    @Adaptive
    ObjectOutput serialize(URL url, OutputStream output) throws IOException;

    // 创建一个ObjectInput对象，ObjectInput负责实现反序列化的功能，即将
    // 字节序列转换成Java对象
    @Adaptive
    ObjectInput deserialize(URL url, InputStream input) throws IOException;

}
