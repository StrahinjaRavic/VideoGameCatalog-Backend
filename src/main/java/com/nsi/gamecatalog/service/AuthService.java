package com.nsi.gamecatalog.service;

import com.nsi.gamecatalog.dto.AuthDtos.*;
import com.nsi.gamecatalog.entity.Admin;
import com.nsi.gamecatalog.entity.AppUser;
import com.nsi.gamecatalog.exception.ApiException;
import com.nsi.gamecatalog.repository.AdminRepository;
import com.nsi.gamecatalog.repository.AppUserRepository;
import com.nsi.gamecatalog.security.JwtService;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final AdminRepository adminRepo;
    private final AppUserRepository userRepo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public AuthService(AdminRepository adminRepo, AppUserRepository userRepo,
                       PasswordEncoder encoder, JwtService jwtService) {
        this.adminRepo = adminRepo;
        this.userRepo = userRepo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest req) {
        Admin admin = adminRepo.findByUsername(req.usernameOrEmail()).orElse(null);
        if (admin != null && encoder.matches(req.password(), admin.getPassword())) {
            String token = jwtService.generateToken(admin.getUsername(), "ADMIN", admin.getId());
            return new LoginResponse(token, "ADMIN", admin.getId(), admin.getUsername());
        }
        AppUser user = userRepo.findByEmail(req.usernameOrEmail()).orElse(null);
        if (user != null && encoder.matches(req.password(), user.getPassword())) {
            String token = jwtService.generateToken(user.getEmail(), "USER", user.getId());
            return new LoginResponse(token, "USER", user.getId(),
                    user.getFirstName() + " " + user.getLastName());
        }
        throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid credentials");
    }

    @Transactional
    public LoginResponse register(RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new ApiException(HttpStatus.CONFLICT, "Email already in use");
        }
        if (userRepo.existsByPhone(req.phone())) {
            throw new ApiException(HttpStatus.CONFLICT, "Phone already in use");
        }
        AppUser user = userRepo.save(new AppUser(
                req.firstName(), req.lastName(),
                req.address(), req.city(),
                req.email(), req.phone(),
                encoder.encode(req.password())));
        String token = jwtService.generateToken(user.getEmail(), "USER", user.getId());
        return new LoginResponse(token, "USER", user.getId(),
                user.getFirstName() + " " + user.getLastName());
    }
}
