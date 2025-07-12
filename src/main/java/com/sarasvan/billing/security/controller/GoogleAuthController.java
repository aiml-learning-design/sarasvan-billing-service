/*
package com.sarasvan.billing.security.controller;

import com.sarasvan.billing.dto.AuthResponse;
import com.sarasvan.billing.entity.UserEntity;
import com.sarasvan.billing.repository.UsersRepository;
import com.sarasvan.billing.security.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;

import java.util.Collections;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class GoogleAuthController {

    private final UsersRepository userRepository;
    private final JwtService jwtService;

    private final String CLIENT_ID = "YOUR_GOOGLE_CLIENT_ID";

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleTokenRequest request) {
        try {
            final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier
                    .Builder(GoogleNetHttpTransport.newTrustedTransport(), JacksonFactory.getDefaultInstance())
                    .setAudience(Collections.singletonList(CLIENT_ID))
                    .build();

            GoogleIdToken idToken = verifier.verify(request.getToken());

            if (idToken != null) {
                GoogleIdToken.Payload payload = idToken.getPayload();

                String email = payload.getEmail();
                String name = (String) payload.get("name");

                UserEntity user = userRepository.findByEmail(email)
                        .orElseGet(() -> {
                            UserEntity newUser = new UserEntity();
                            newUser.setEmail(email);
                  //          newUser.setFullName(name);
                    //        newUser.setUserRole("USER");
                            return userRepository.save(newUser);
                        });

                String jwtToken = jwtService.generateToken(user);
                return ResponseEntity.ok(new AuthResponse(jwtToken));
            } else {
                return ResponseEntity.badRequest().body("Invalid Google token");
            }

        } catch (Exception e) {
            return ResponseEntity.status(500).body("Google login failed: " + e.getMessage());
        }
    }
}
*/
