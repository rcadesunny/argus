package com.rcadium.argus.infrastructure.web;

import com.rcadium.argus.application.AuthService;
import com.rcadium.argus.application.auth.EmailPasswordCredentials;
import com.rcadium.argus.client.dto.LoginRequest;
import com.rcadium.argus.client.dto.RefreshRequest;
import com.rcadium.argus.client.dto.RegisterRequest;
import com.rcadium.argus.client.dto.TokenResponse;
import com.rcadium.argus.domain.AuthMethod;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public TokenResponse register(@Valid @RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public TokenResponse login(@Valid @RequestBody LoginRequest request) {
        return authService.authenticate(
                AuthMethod.EMAIL_PASSWORD,
                new EmailPasswordCredentials(request.email(), request.password()));
    }

    @PostMapping("/refresh")
    public TokenResponse refresh(@Valid @RequestBody RefreshRequest request) {
        return authService.refresh(request.refreshToken());
    }

    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void logout(@Valid @RequestBody RefreshRequest request) {
        authService.logout(request.refreshToken());
    }

}
