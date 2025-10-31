package co.edu.usco.petcotas.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.edu.usco.petcotas.model.Role;
import co.edu.usco.petcotas.repository.RoleRepository;

/**
 * Carga inicial de datos (roles básicos) al iniciar la aplicación.
 * Si los roles ya existen, no los duplica.
 */
@Configuration
public class DataLoader {

    @Bean
    CommandLineRunner initRoles(RoleRepository roleRepository) {
        return args -> {
            if (roleRepository.findByName("ROLE_USER").isEmpty()) {
                roleRepository.save(Role.builder().name("ROLE_USER").build());
                System.out.println("✅ ROLE_USER creado.");
            }

            if (roleRepository.findByName("ROLE_ADMIN").isEmpty()) {
                roleRepository.save(Role.builder().name("ROLE_ADMIN").build());
                System.out.println("✅ ROLE_ADMIN creado.");
            }
        };
    }
}
