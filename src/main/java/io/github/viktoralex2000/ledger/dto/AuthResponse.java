package io.github.viktoralex2000.ledger.dto;

public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresInSeconds,
        String refreshToken,
        UserDto userDto
) {

    public static AuthResponse of(String accessToken,
                                  long expiresInSeconds,
                                  String refreshToken,
                                  UserDto userDto) {
        return new AuthResponse(accessToken,
                "Bearer",
                expiresInSeconds,
                refreshToken,
                userDto);
    }
}
