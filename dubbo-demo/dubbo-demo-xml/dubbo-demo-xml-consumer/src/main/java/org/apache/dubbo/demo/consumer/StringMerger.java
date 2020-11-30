package org.apache.dubbo.demo.consumer;

import org.apache.dubbo.common.utils.ArrayUtils;
import org.apache.dubbo.rpc.cluster.Merger;

/**
 * Created on 2020-09-28
 */
public class StringMerger implements Merger<String> {
    @Override
    public String merge(String... items) {
        if (ArrayUtils.isEmpty(items)) {
            return "";
        }
        String result = "";
        for (String item : items) {
            result += item + "|";
        }
        return result;
    }
}