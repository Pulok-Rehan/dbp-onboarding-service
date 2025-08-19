package com.bracepl.dbp_onboarding_service.keycloak;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class KeycloakSignupController {

    private final KeycloakService keycloakService;

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody SignupRequest request) {
        try {
            keycloakService.registerUser(request);
            return ResponseEntity.ok("User registered successfully in Keycloak");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Failed to register: " + e.getMessage());
        }
    }
}
