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
 * LeastActiveLoadBalance
 * <p>
 * Filter the number of invokers with the least number of active calls and count the weights and quantities of these invokers.
 * If there is only one invoker, use the invoker directly;
 * if there are multiple invokers and the weights are not the same, then random according to the total weight;
 * if there are multiple invokers and the same weight, then randomly called.
 */
public class LeastActiveLoadBalance extends AbstractLoadBalance {

    public static final String NAME = "leastactive";

    @Override
    protected <T> Invoker<T> doSelect(List<Invoker<T>> invokers, URL url, Invocation invocation) {
        // 初始化Invoker数量
        int length = invokers.size();
        // 记录最小的活跃请求数
        int leastActive = -1;
        // 记录活跃请求数最小的Invoker集合的个数
        int leastCount = 0;
        // 记录活跃请求数最小的Invoker在invokers数组中的下标位置
        int[] leastIndexes = new int[length];
        // 记录活跃请求数最小的Invoker集合中，每个Invoker的权重值
        int[] weights = new int[length];
        // 记录活跃请求数最小的Invoker集合中，所有Invoker的权重值之和
        int totalWeight = 0;
        // 记录活跃请求数最小的Invoker集合中，第一个Invoker的权重值
        int firstWeight = 0;
        // 是否活跃请求数最小的集合中，所有Invoker的权重值是否相同
        boolean sameWeight = true;

        for (int i = 0; i < length; i++) { // 遍历所有Invoker，获取活跃请求数最小的Invoker集合
            Invoker<T> invoker = invokers.get(i);
            // 获取该Invoker的活跃请求数
            int active = RpcStatus.getStatus(invoker.getUrl(), invocation.getMethodName()).getActive();
            // 获取该Invoker的权重
            int afterWarmup = getWeight(invoker, invocation);
            weights[i] = afterWarmup;
            // 比较活跃请求数
            if (leastActive == -1 || active < leastActive) {
                // 当前的Invoker是第一个活跃请求数最小的Invoker，则记录如下信息
                leastActive = active; // 重新记录最小的活跃请求数
                leastCount = 1; // 重新记录活跃请求数最小的Invoker集合个数
                leastIndexes[0] = i; // 重新记录Invoker
                totalWeight = afterWarmup; // 重新记录总权重值
                firstWeight = afterWarmup; // 该Invoker作为第一个Invoker，记录其权重值
                sameWeight = true; // 重新记录是否权重值相等
            } else if (active == leastActive) {
                // 当前Invoker属于活跃请求数最小的Invoker集合
                leastIndexes[leastCount++] = i; // 记录该Invoker的下标
                totalWeight += afterWarmup; // 更新总权重
                if (sameWeight && afterWarmup != firstWeight) {
                    sameWeight = false; // 更新权重值是否相等
                }
            }
        }
        // 如果只有一个活跃请求数最小的Invoker对象，直接返回即可
        if (leastCount == 1) {
            return invokers.get(leastIndexes[0]);
        }
        // 下面按照RandomLoadBalance的逻辑，从活跃请求数最小的Invoker集合中，随机选择一个Invoker对象返回
        if (!sameWeight && totalWeight > 0) {
            int offsetWeight = ThreadLocalRandom.current().nextInt(totalWeight);
            for (int i = 0; i < leastCount; i++) {
                int leastIndex = leastIndexes[i];
                offsetWeight -= weights[leastIndex];
                if (offsetWeight < 0) {
                    return invokers.get(leastIndex);
                }
            }
        }
        return invokers.get(leastIndexes[ThreadLocalRandom.current().nextInt(leastCount)]);
    }
}
