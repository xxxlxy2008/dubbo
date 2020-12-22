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
package org.apache.dubbo.metadata.report.support;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.metadata.report.MetadataReport;
import org.apache.dubbo.metadata.report.MetadataReportFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractMetadataReportFactory implements MetadataReportFactory {
    private static final String EXPORT_KEY = "export";
    private static final String REFER_KEY = "refer";

    // The lock for the acquisition process of the registry
    private static final ReentrantLock LOCK = new ReentrantLock();

    // Registry Collection Map<metadataAddress, MetadataReport>
    private static final Map<String, MetadataReport> SERVICE_STORE_MAP = new ConcurrentHashMap<String, MetadataReport>();

    @Override
    public MetadataReport getMetadataReport(URL url) {
        // 清理export、refer参数
        url = url.setPath(MetadataReport.class.getName())
                .removeParameters(EXPORT_KEY, REFER_KEY);
        String key = url.toServiceString();
        LOCK.lock();
        try {
            // 从SERVICE_STORE_MAP集合（ConcurrentHashMap<String, MetadataReport>类型）中
            // 查询是否已经缓存有对应的MetadataReport对象
            MetadataReport metadataReport = SERVICE_STORE_MAP.get(key);
            if (metadataReport != null) { // 直接返回缓存的MetadataReport对象
                return metadataReport;
            }
            // 创建新的MetadataReport对象，createMetadataReport()方法由子类具体实现
            metadataReport = createMetadataReport(url);
            if (metadataReport == null) {
                throw new IllegalStateException("Can not create metadata Report " + url);
            }
            // 将MetadataReport缓存到SERVICE_STORE_MAP集合中
            SERVICE_STORE_MAP.put(key, metadataReport);
            return metadataReport;
        } finally {
            // Release the lock
            LOCK.unlock();
        }
    }

    protected abstract MetadataReport createMetadataReport(URL url);
}
