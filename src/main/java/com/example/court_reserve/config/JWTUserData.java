package com.example.court_reserve.config;

import lombok.Builder;

@Builder
public record JWTUserData(Long userId, String name, String email, String role) {
}
