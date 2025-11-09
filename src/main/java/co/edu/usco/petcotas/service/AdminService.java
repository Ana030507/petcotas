package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.AdminCreateRequest;
import co.edu.usco.petcotas.model.Role;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.RoleRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserEntity createAdmin(AdminCreateRequest request) {
        // evitar duplicados por username
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new RuntimeException("Ya existe un usuario con ese username");
        }

        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("Rol ROLE_ADMIN no encontrado"));

        UserEntity admin = UserEntity.builder()
                .username(request.getUsername())
                .email(null) // no pedimos email, lo dejamos en null
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(adminRole)
                .build();

        return userRepository.save(admin);
    }

    public List<UserEntity> getAllAdmins() {
        return userRepository.findByRoleName("ROLE_ADMIN");
    }

    public void deleteAdmin(Long id) {
        userRepository.deleteById(id);
    }
}
