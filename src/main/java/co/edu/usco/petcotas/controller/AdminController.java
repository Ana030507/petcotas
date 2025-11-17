package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.AdminCreateRequest;
import co.edu.usco.petcotas.dto.AdminDto;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.service.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admins")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    // 🟢 Crear nuevo admin
    @PostMapping("/create")
    public ResponseEntity<UserEntity> createAdmin(@RequestBody AdminCreateRequest request) {
        UserEntity created = adminService.createAdmin(request);
        return ResponseEntity.status(201).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AdminDto>> getAllAdmins() {
        return ResponseEntity.ok(adminService.getAllAdmins());
    }



    // 🗑️ Eliminar un admin
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdmin(@PathVariable Long id) {
        adminService.deleteAdmin(id);
        return ResponseEntity.ok("Administrador eliminado correctamente");
    }
}
