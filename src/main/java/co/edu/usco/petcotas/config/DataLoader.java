package co.edu.usco.petcotas.config;

import co.edu.usco.petcotas.model.Role;
import co.edu.usco.petcotas.model.Status;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.RoleRepository;
import co.edu.usco.petcotas.repository.StatusRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

@Configuration
@RequiredArgsConstructor
public class DataLoader {

    private static final Logger log = LoggerFactory.getLogger(DataLoader.class);

    private final RoleRepository roleRepository;
    private final StatusRepository statusRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // valores por defecto para desarrollo; en producción usa variables de entorno
    @Value("${app.initial-admin.username:admin}")
    private String initialAdminUsername;

    @Value("${app.initial-admin.password:Admin123!}")
    private String initialAdminPassword;

    @Bean
    public ApplicationRunner initData() {
        return args -> {
            log.info("🚀 Iniciando carga de datos iniciales...");

            // --- Roles ---
            Role roleUser = roleRepository.findByName("ROLE_USER")
                    .orElseGet(() -> {
                        Role r = roleRepository.save(Role.builder().name("ROLE_USER").build());
                        log.info("✅ Rol creado: ROLE_USER (id={})", r.getId());
                        return r;
                    });

            Role roleAdmin = roleRepository.findByName("ROLE_ADMIN")
                    .orElseGet(() -> {
                        Role r = roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
                        log.info("✅ Rol creado: ROLE_ADMIN (id={})", r.getId());
                        return r;
                    });

            log.info("✅ Roles asegurados: ROLE_USER (id={}), ROLE_ADMIN (id={})", roleUser.getId(), roleAdmin.getId());

            // --- Statuses ---
            String[] statuses = {"available", "adopted", "pending"};
            for (String s : statuses) {
                statusRepository.findByNameIgnoreCase(s)
                        .orElseGet(() -> {
                            Status st = statusRepository.save(Status.builder().name(s).build());
                            log.info("✅ Status creado: {}", s);
                            return st;
                        });
            }
            log.info("✅ Statuses asegurados: available, adopted, pending");

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
                log.info("👑 Admin inicial creado: username={}, password={}, role={}",
                        initialAdminUsername, initialAdminPassword, roleAdmin.getName());
            } else {
                log.info("ℹ️ Admin inicial ya existe: username={}", initialAdminUsername);
            }

            log.info("✅ ✅ ✅ Carga de datos iniciales completada");
        };
    }
}
