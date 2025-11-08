package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminService adminService;

    // ✅ Crear un nuevo administrador (solo accesible por ADMIN)
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<UserEntity> createAdmin(@RequestBody UserEntity newAdmin) {
        return ResponseEntity.ok(adminService.createAdmin(newAdmin));
    }

    // ✅ Listar todos los administradores
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/list")
    public ResponseEntity<List<UserEntity>> listAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }

    // ✅ Eliminar un administrador por ID
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Administrador eliminado correctamente.");
    }
}
