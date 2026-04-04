package com.finance.dashboard.dto.response;

public record AuthResponse(String token, String email, String role) {
}
