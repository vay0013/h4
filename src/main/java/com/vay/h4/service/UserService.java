package com.vay.h4.service;

import com.vay.h4.model.User;

public interface UserService {
    void createUser(User user);

    long login(String username, String password);


    void updateRefreshToken(Long userId, String refreshToken);

    void removeRefreshToken(String username);
}
