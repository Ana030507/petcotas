package co.edu.usco.petcotas.service;

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

    // ✅ Crear nuevo admin
    public UserEntity createAdmin(UserEntity newAdmin) {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RuntimeException("El rol ROLE_ADMIN no existe en la base de datos"));

        newAdmin.setPasswordHash(passwordEncoder.encode(newAdmin.getPasswordHash()));
        newAdmin.setRole(adminRole);

        return userRepository.save(newAdmin);
    }

    // ✅ Obtener todos los admins
    public List<UserEntity> getAllAdmins() {
        return userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() != null && "ROLE_ADMIN".equals(u.getRole().getName()))
                .toList();
    }

    // ✅ Eliminar un admin por ID
    public void deleteAdmin(Long id) {
        userRepository.deleteById(id);
    }
}
