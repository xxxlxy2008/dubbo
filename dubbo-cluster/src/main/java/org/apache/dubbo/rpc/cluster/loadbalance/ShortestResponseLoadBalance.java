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
package org.apache.dubbo.rpc.cluster.loadbalance;

import org.apache.dubbo.common.URL;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.RpcStatus;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * ShortestResponseLoadBalance
 * </p>
 * Filter the number of invokers with the shortest response time of success calls and count the weights and quantities of these invokers.
 * If there is only one invoker, use the invoker directly;
 * if there are multiple invokers and the weights are not the same, then random according to the total weight;
 * if there are multiple invokers and the same weight, then randomly called.
 */
public class ShortestResponseLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "shortestresponse";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 记录Invoker集合的数量
        int length = invokers.size();
        // 用于记录所有Invoker集合中最短响应时间
        long shortestResponse = Long.MAX_VALUE;
        // 具有相同最短响应时间的Invoker个数
        int shortestCount = 0;
        // 存放所有最短响应时间的Invoker的下标
        int[] shortestIndexes = new int[length];
        // 存储每个Invoker的权重
        int[] weights = new int[length];
        // 存储权重总和
        int totalWeight = 0;
        // 记录第一个Invoker对象的权重
        int firstWeight = 0;
        // 最短响应时间Invoker集合中的Invoker权重是否相同
        boolean sameWeight = true;

        for (int i = 0; i < length; i++) {
            Invoker<T> invoker = invokers.get(i);
            RpcStatus rpcStatus = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName());
            // 获取调用成功的平均时间，具体计算方式是：
            // 调用成功的请求数总数对应的总耗时 / 调用成功的请求数总数 = 成功调用的平均时间。
            // RpcStatus 的内容在前面课时已经介绍过了，这里不再重复
            long succeededAverageElapsed = rpcStatus.getSucceededAverageElapsed();
            // 获取的是该Provider当前的活跃请求数，也就是当前正在处理的请求数
            int active = rpcStatus.getActive();
            // 计算一个处理新请求的预估值，也就是如果当前请求发给这个Provider，大概耗时多久处理完成
            long estimateResponse = succeededAverageElapsed * active;
            // 计算该Invoker的权重（主要是处理预热）
            int afterWarmup = getWeight(invoker, invocation);
            weights[i] = afterWarmup;
            if (estimateResponse < shortestResponse) {
                // 第一次找到Invoker集合中最短响应耗时的Invoker对象，记录其相关信息
                shortestResponse = estimateResponse;
                shortestCount = 1;
                shortestIndexes[0] = i;
                totalWeight = afterWarmup;
                firstWeight = afterWarmup;
                sameWeight = true;
            } else if (estimateResponse == shortestResponse) {
                // 出现对个耗时最短的Invoker对象
                shortestIndexes[shortestCount++] = i;
                totalWeight += afterWarmup;
                if (sameWeight && i > 0
                        && afterWarmup != firstWeight) {
                    sameWeight = false;
                }
            }
        }
        if (shortestCount == 1) {
            return invokers.get(shortestIndexes[0]);
        }
        // 如果耗时最短的所有Invoker对象的权重不相同，则通过加权随机负载均衡的方式选择一个Invoker返回
        if (!sameWeight && totalWeight > 0) {
            int offsetWeight = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < shortestCount; i++) {
                int shortestIndex = shortestIndexes[i];
                offsetWeight -= weights[shortestIndex];
                if (offsetWeight < 0) {
                    return invokers.get(shortestIndex);
                }
            }
        }
        // 如果耗时最短的所有Invoker对象的权重相同，则随机返回一个
        return invokers.get(shortestIndexes[ThreadLocalRandom.current().nextInt(shortestCount)]);
    }
}
