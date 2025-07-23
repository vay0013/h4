package com.vay.h4.controller;

import com.vay.h4.controller.dto.LoginDto;
import com.vay.h4.controller.dto.TokenResponse;
import com.vay.h4.exception.InvalidTokenException;
import com.vay.h4.model.User;
import com.vay.h4.service.UserService;
import com.vay.h4.util.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("api")
public class AuthController {
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("login")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse login(@RequestBody LoginDto payload) {
        Long userId = userService.login(payload.username(), payload.password());
        String accessToken = jwtTokenProvider.generateToken(payload.username());
        String refreshToken = jwtTokenProvider.generateRefreshToken(payload.username());
        userService.updateRefreshToken(userId, refreshToken);
        return new TokenResponse(accessToken, refreshToken);
    }

    @PostMapping("signup")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse signup(@RequestBody LoginDto payload) {
        String accessToken = jwtTokenProvider.generateToken(payload.username());
        String refreshToken = jwtTokenProvider.generateRefreshToken(payload.username());
        userService.createUser(
                new User(null,
                        payload.username(),
                        payload.password(),
                        payload.email(),
                        refreshToken,
                        Set.of(payload.roles())));
        return new TokenResponse(accessToken, refreshToken);
    }

    @PostMapping("refresh")
    @ResponseStatus(HttpStatus.OK)
    public TokenResponse refresh(@RequestBody String refreshToken) {
        if (jwtTokenProvider.validateToken(refreshToken)) {
            String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
            String accessToken = jwtTokenProvider.generateToken(username);
            String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
            return new TokenResponse(accessToken, newRefreshToken);
        }
        throw new InvalidTokenException("Invalid refresh token");
    }

    @PostMapping("logout")
    @ResponseStatus(HttpStatus.OK)
    public void logout(@RequestBody String refreshToken) {
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        userService.removeRefreshToken(username);
    }
}
