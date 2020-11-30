package org.apache.dubbo.demo.consumer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;

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
@Activate(group = {CommonConstants.CONSUMER}, order = -1)
public class JarVersionConsumerFilter implements Filter {

    private static final String JAR_VERSION_NAME_KEY = "dubbo.jar.version";

    // 通过一个LoadingCache缓存各个Class所在的jar包版本
    private LoadingCache<Class<?>, String> versionCache = CacheBuilder.newBuilder()
            .maximumSize(1024).build(new CacheLoader<Class<?>, String>() {
                @Override
                public String load(Class<?> key) throws Exception {
                    return getJarVersion(key);
                }
            });

    @Override
    public Result invoke(Invoker<?> invoker, Invocation invocation) throws RpcException {
        Map<String, String> attachments = invocation.getAttachments();
        String version = versionCache.getUnchecked(invoker.getInterface());
        if (!StringUtils.isBlank(version)) {// 添加版本号
            attachments.put(JAR_VERSION_NAME_KEY, version);
        }
        return invoker.invoke(invocation);
    }

    // 读取Classpath下的"/META-INF/MANIFEST.MF"文件，获取jar包版本
    private String getJarVersion(Class clazz) {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(clazz.getResourceAsStream("/META-INF/MANIFEST.MF")))) {
            String s = null;
            while ((s = reader.readLine()) != null) {
                int i = s.indexOf("Implementation-Version:");
                if (i > 0) {
                    return s.substring(i);
                }
            }
        } catch (IOException e) {
            // 省略异常处理逻辑
        }
        return "";
    }
}