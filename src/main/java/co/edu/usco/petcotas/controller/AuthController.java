package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.LoginRequest;
import co.edu.usco.petcotas.dto.RegisterRequest;
import co.edu.usco.petcotas.dto.UserDto;
import co.edu.usco.petcotas.model.Role;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.RoleRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import co.edu.usco.petcotas.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepository.existsByUsername(req.getUsername())) {
            return ResponseEntity.badRequest().body(Map.of("error", "El nombre de usuario ya existe"));
        }

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new IllegalStateException("ROLE_USER no encontrado"));

        UserEntity user = UserEntity.builder()
                .username(req.getUsername())
                .email(null)
                .passwordHash(passwordEncoder.encode(req.getPassword()))
                .profileImageUrl(req.getProfileImageUrl())
                .role(userRole)
                .build();

        userRepository.save(user);

        UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileImageUrl(), user.getRole().getName());
        return ResponseEntity.status(201).body(dto);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest req) {
        try {
            Authentication auth = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword())
            );
            SecurityContextHolder.getContext().setAuthentication(auth);

            String token = jwtService.generateToken(req.getUsername());

            UserEntity user = userRepository.findByUsername(req.getUsername())
                    .orElseThrow();

            UserDto dto = new UserDto(user.getId(), user.getUsername(), user.getEmail(), user.getProfileImageUrl(), user.getRole().getName());

            Map<String, Object> body = new HashMap<>();
            body.put("token", token);
            body.put("user", dto);
            return ResponseEntity.ok(body);

        } catch (BadCredentialsException ex) {
            return ResponseEntity.status(401).body(Map.of("error", "Credenciales inválidas"));
        }
    }
}
