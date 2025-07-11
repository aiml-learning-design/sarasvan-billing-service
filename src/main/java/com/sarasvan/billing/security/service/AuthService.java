package com.sarasvan.billing.security.service;

import com.sarasvan.billing.dto.AuthRequest;
import com.sarasvan.billing.dto.AuthResponse;
import com.sarasvan.billing.dto.RegisterRequest;
import com.sarasvan.billing.entity.UserEntity;
import com.sarasvan.billing.enums.Role;
import com.sarasvan.billing.exception.handler.DuplicateEntityException;
import com.sarasvan.billing.mapper.UsersMapper;
import com.sarasvan.billing.model.User;
import com.sarasvan.billing.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsersRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEntityException("Email already exists");
        }

        // Email format validation
        if (!isValidEmail(request.getEmail())) {
            throw new IllegalArgumentException("Invalid email format");
        }

        // Email uniqueness check
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email already registered");
        }
        var user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        UserEntity userEntity = UsersMapper.INSTANCE.dtoToEntity(user);

        userRepository.save(userEntity);
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder().token(jwtToken).email(request.getEmail()).build();
    }

    public AuthResponse authenticate(AuthRequest request) {
        System.out.println("Attempting login for email: " + request.email());
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.email(),
                            request.password()
                    )
            );
            var user = userRepository.findByEmail(request.email())
                    .orElseThrow();
            User userDetails = UsersMapper.INSTANCE.entityToDto(user);
            var jwtToken = jwtService.generateToken(userDetails);
            return AuthResponse.builder().token(jwtToken).email(request.email()).build();
        } catch (BadCredentialsException e) {
            System.err.println("Bad credentials for email: " + request.email());
            throw e;
        }
    }

    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}