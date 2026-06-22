package com.nsi.gamecatalog.dto;

import jakarta.validation.constraints.*;

public class AuthDtos {

    public record LoginRequest(
            @NotBlank String usernameOrEmail,
            @NotBlank String password
    ) {}

    public record LoginResponse(
            String token,
            String role,
            Long id,
            String displayName
    ) {}

    public record RegisterRequest(
            @NotBlank @Size(max = 50) String firstName,
            @NotBlank @Size(max = 50) String lastName,
            @NotBlank @Size(max = 200) String address,
            @NotBlank @Size(max = 100) String city,
            @NotBlank @Email @Size(max = 150) String email,
            @NotBlank @Pattern(regexp = "^[+0-9 \\-]{6,30}$", message = "Invalid phone") String phone,
            @NotBlank @Size(min = 6, max = 100) String password
    ) {}
}
