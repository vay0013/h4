package com.vay.h4.service;

import com.vay.h4.exception.UserAlreadyExistsException;
import com.vay.h4.exception.UserNotFoundException;
import com.vay.h4.model.User;
import com.vay.h4.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Base64;

@Service
@RequiredArgsConstructor
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createUser(User payload) {
        if (userRepository.existsUserByEmail(payload.getEmail())) {
            throw new UserAlreadyExistsException("user with email=%s already exists".formatted(payload.getEmail()));
        }
        if (userRepository.existsUserByUsername(payload.getUsername())) {
            throw new UserAlreadyExistsException("user with username=%s already exists".formatted(payload.getUsername()));
        }

        byte[] hashed = Base64.getDecoder().decode(payload.getPassword());
        payload.setPassword(new String(hashed));
        userRepository.save(payload);
    }

    @Override
    @Transactional(readOnly = true)
    public long login(String username, String password) {
        User user = userRepository.findUserByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("user with username=%s not found".formatted(username)));

        String unhashed = Base64.getEncoder().encodeToString(password.getBytes());
        if (!user.getPassword().equals(unhashed)) {
            throw new UserNotFoundException("Invalid password for username=%s".formatted(username));
        }
        return user.getId();
    }

    @Override
    @Transactional
    public void updateRefreshToken(Long userId, String refreshToken) {
        User user = userRepository.findUserById(userId);
        if (user != null) {
            user.setRefreshToken(refreshToken);
            userRepository.save(user);
        }
    }

    @Override
    @Transactional
    public void removeRefreshToken(String username) {
        userRepository.findUserByUsername(username).ifPresentOrElse(user -> user.setRefreshToken(null),
                () -> {
                    throw new UserNotFoundException("user with username=%s not found".formatted(username));
                });
    }
}