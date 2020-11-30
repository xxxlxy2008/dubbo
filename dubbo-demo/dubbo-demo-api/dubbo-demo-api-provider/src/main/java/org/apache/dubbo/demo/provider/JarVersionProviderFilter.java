package org.apache.dubbo.demo.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import org.apache.dubbo.common.constants.CommonConstants;
import org.apache.dubbo.common.extension.Activate;
import org.apache.dubbo.common.utils.StringUtils;
import org.apache.dubbo.rpc.Filter;
import org.apache.dubbo.rpc.Invocation;
import org.apache.dubbo.rpc.Invoker;
import org.apache.dubbo.rpc.Result;
import org.apache.dubbo.rpc.RpcException;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

/**
 * Created on 2020-08-20
 */
@Activate(group = {CommonConstants.PROVIDER}, order = -1)
public class JarVersionProviderFilter implements Filter {

    private static final String JAR_VERSION_NAME_KEY = "dubbo.jar.version";

    private static final Map<String, AtomicLong> versionState = new ConcurrentHashMap<>();

    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    public JarVersionProviderFilter() {
        // 启动定时任务
        SCHEDULED_EXECUTOR_SERVICE.schedule(() -> {
            for (Map.Entry<String, AtomicLong> entry : versionState.entrySet()) {
                // 打印日志并将统计数据重置
                System.out.println(entry.getKey() + ":" + entry.getValue().getAndSet(0));
            }
        }, 1, TimeUnit.MINUTES);
    }

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        String versionAttachment = invocation.getAttachment(JAR_VERSION_NAME_KEY);
        if (!StringUtils.isBlank(versionAttachment)) {
            // 递增该版本的统计值
            AtomicLong count = versionState.computeIfAbsent(versionAttachment, v -> new AtomicLong(0L));
            count.getAndIncrement();
        }
        return invoker.invoke(invocation);
    }
}