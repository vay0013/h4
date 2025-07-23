package com.vay.h4.repository;

import com.vay.h4.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsUserByUsername(String username);

    boolean existsUserByEmail(String email);

    Optional<User> findUserByUsername(String username);

    Optional<User> findUserByRefreshToken(String refreshToken);

    User findUserById(Long id);
}
