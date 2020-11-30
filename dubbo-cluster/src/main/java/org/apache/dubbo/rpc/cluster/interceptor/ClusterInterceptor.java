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
package org.apache.dubbo.rpc.cluster.interceptor;

import org.apache.dubbo.common.extension.SPI;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;
import org.apache.dubbo.rpc.cluster.support.AbstractClusterInvoker;

/**
 * Different from {@link Filter}, ClusterInterceptor works at the outmost layer, before one specific address/invoker is picked.
 */
@SPI
public interface ClusterInterceptor {
    // 前置拦截方法
    void before(AbstractClusterInvoker<?> clusterInvoker, Invocation invocation);
    // 后置拦截方法
    void after(AbstractClusterInvoker<?> clusterInvoker, Invocation invocation);
    // 调用ClusterInvoker的invoke()方法完成请求
    default Result intercept(AbstractClusterInvoker<?> clusterInvoker,
                             Invocation invocation) throws RpcException {
        return clusterInvoker.invoke(invocation);
    }

    // 这个Listener用来监听请求的正常结果以及异常
    interface Listener {

        void onMessage(Result appResponse, AbstractClusterInvoker<?> clusterInvoker, Invocation invocation);

        void onError(Throwable t, AbstractClusterInvoker<?> clusterInvoker, Invocation invocation);
    }
}
