package com.demo;

import java.net.URL;

import com.googlecode.jsonrpc4j.JsonRpcHttpClient;

public class JsonRpcClient {

    private static JsonRpcHttpClient rpcHttpClient;

    public static void main(String[] args) throws Throwable {
        rpcHttpClient = new JsonRpcHttpClient(new URL("http://127.0.0.1:9999/rpc"));
        JsonRpcClient jsonRpcClient = new JsonRpcClient();

        jsonRpcClient.deleteAll();
        System.out.println(jsonRpcClient.createUser(1, "testName", 30));
        System.out.println(jsonRpcClient.getUser(1));
        System.out.println(jsonRpcClient.getUserName(1));
        System.out.println(jsonRpcClient.getUserId("testName"));
    }

    public void deleteAll() throws Throwable {
        rpcHttpClient.invoke("deleteAll", null);
    }

    public User createUser(int userId, String name, int age) throws Throwable {
        Object[] params = new Object[]{userId, name, age};
        return rpcHttpClient.invoke("createUser", params, User.class);
    }

    public User getUser(int userId) throws Throwable {
        Integer[] params = new Integer[]{userId};
        return rpcHttpClient.invoke("getUser", params, User.class);
    }

    public String getUserName(int userId) throws Throwable {
        Integer[] params = new Integer[]{userId};
        return rpcHttpClient.invoke("getUserName", params, String.class);
    }

    public int getUserId(String name) throws Throwable {
        String[] params = new String[]{name};
        return rpcHttpClient.invoke("getUserId", params, Integer.class);
    }

}