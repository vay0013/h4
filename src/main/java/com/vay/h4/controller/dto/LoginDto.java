package com.vay.h4.controller.dto;

import com.vay.h4.model.Role;

public record LoginDto(String username, String password, String email, Role... roles) {
}