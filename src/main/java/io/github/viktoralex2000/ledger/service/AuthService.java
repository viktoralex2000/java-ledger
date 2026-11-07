package io.github.viktoralex2000.ledger.service;

import io.github.viktoralex2000.ledger.dto.AuthResponse;
import io.github.viktoralex2000.ledger.dto.LoginRequest;
import io.github.viktoralex2000.ledger.dto.RegisterRequest;
import io.github.viktoralex2000.ledger.dto.UserDto;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    public AuthResponse register(RegisterRequest registerRequest) {
        return null;
    }

    public AuthResponse login(LoginRequest loginRequest) {
        return null;
    }

    public AuthResponse refresh(String refreshToken) {
        return null;
    }

    public void logout(String token) {
    }

    public UserDto me(String token) {
        return null;
    }
}
