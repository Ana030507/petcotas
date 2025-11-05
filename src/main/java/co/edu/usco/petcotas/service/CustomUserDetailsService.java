package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(CustomUserDetailsService.class);

    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true) // <- aquí: asegura que la sesión esté abierta mientras accedes a role
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("🔍 Cargando usuario: {}", username);

        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.error("❌ Usuario no encontrado: {}", username);
                    return new UsernameNotFoundException("Usuario no encontrado: " + username);
                });

        String roleName = user.getRole().getName();
        log.debug("👑 Rol del usuario {}: {}", username, roleName);

        GrantedAuthority authority = new SimpleGrantedAuthority(roleName);
        log.info("✅ Authority asignada: {}", authority.getAuthority());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPasswordHash(),
                Collections.singletonList(authority)
        );
    }
}
