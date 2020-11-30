package com.demo;

import java.util.ArrayList;
import java.util.List;

public class UserServiceImpl implements UserService {

    private List<User> users = new ArrayList<>();

    @Override
    public User createUser(int userId, String name, int age) {
        System.out.println("createUser method");
        User user = new User();
        user.setUserId(userId);
        user.setName(name);
        user.setAge(age);
        users.add(user);
        return user;
    }

    @Override
    public User getUser(int userId) {
        System.out.println("getUser method");
        return users.stream().filter(u -> u.getUserId() == userId).findAny().get();
    }

    @Override
    public String getUserName(int userId) {
        System.out.println("getUserName method");
        return getUser(userId).getName();
    }

    @Override
    public int getUserId(String name) {
        System.out.println("getUserId method");
        return users.stream().filter(u -> u.getName().equals(name)).findAny().get().getUserId();
    }

    @Override
    public void deleteAll() {
        System.out.println("deleteAll");
        users.clear();
    }
}