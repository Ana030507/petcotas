package co.edu.usco.petcotas.config;

import co.edu.usco.petcotas.model.Role;
import co.edu.usco.petcotas.model.Status;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.RoleRepository;
import co.edu.usco.petcotas.repository.StatusRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // valores por defecto para desarrollo; en producción usa variables de entorno
    @Value("${app.initial-admin.username:admin}")
    private String initialAdminUsername;

    @Value("${APP_INITIAL_ADMIN_PASSWORD:Admin123!}")
    private String initialAdminPassword;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            // --- Roles ---
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_USER").build()));
            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> roleRepository.save(Role.builder().name("ROLE_ADMIN").build()));

            System.out.println("➡️ Roles iniciales asegurados.");

            // --- Statuses ---
            String[] statuses = {"available", "adopted", "pending"};
            for (String s : statuses) {
                statusRepository.findByNameIgnoreCase(s)
                        .orElseGet(() -> statusRepository.save(Status.builder().name(s).build()));
            }
            System.out.println("➡️ Statuses iniciales asegurados: available, adopted, pending.");

            // --- Admin inicial ---
            Optional<UserEntity> existingAdmin = userRepository.findByUsername(initialAdminUsername);
            if (existingAdmin.isEmpty()) {
                UserEntity admin = UserEntity.builder()
                        .username(initialAdminUsername)
                        .email(null) // si no quieres email inicial
                        .passwordHash(passwordEncoder.encode(initialAdminPassword))
                        .role(roleAdmin)
                        .profileImageUrl(null)
                        .build();
                userRepository.save(admin);
                System.out.println("✅ Admin inicial creado: " + initialAdminUsername);
            } else {
                System.out.println("ℹ️ Admin inicial ya existe: " + initialAdminUsername);
            }
        };
    }
}
