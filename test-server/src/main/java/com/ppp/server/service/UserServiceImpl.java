package com.ppp.server.service;

import com.ppp.api.User;
import com.ppp.api.UserService;

public class UserServiceImpl implements UserService {
    @Override
    public User getUser(Long id) {
        return User.builder()
            .id(++id)
            .name("张三")
            .build();
    }
}
