package com.demo;

public interface UserService {

    User createUser(int userId, String name, int age);

    User getUser(int userId);

    String getUserName(int userId);

    int getUserId(String name);

    void deleteAll();
}