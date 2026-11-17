package io.github.viktoralex2000.ledger.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.viktoralex2000.ledger.dto.AuthResponse;
import io.github.viktoralex2000.ledger.dto.LoginRequest;
import io.github.viktoralex2000.ledger.dto.RefreshRequest;
import io.github.viktoralex2000.ledger.dto.RegisterRequest;
import io.github.viktoralex2000.ledger.dto.UserDto;
import io.github.viktoralex2000.ledger.model.UserEntity;
import io.github.viktoralex2000.ledger.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")

public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }
    
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest refreshRequest) {
        return ResponseEntity.ok(authService.refresh(refreshRequest.refreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestBody(required = false) RefreshRequest request) {
        authService.logout(request == null ? null : request.refreshToken());
        return ResponseEntity.noContent().build();
    }

    //todo исправить как дойдём до безопасности
    @GetMapping("/me")
   
    public ResponseEntity<UserDto> me(@AuthenticationPrincipal UserEntity user) {
        return ResponseEntity.ok(authService.me(user.getId()));
    }

}
