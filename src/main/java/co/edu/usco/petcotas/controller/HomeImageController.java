package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.model.HomeImage;
import co.edu.usco.petcotas.service.HomeImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;

@RestController
@RequestMapping("/api/images/home")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class HomeImageController {

    private final HomeImageService homeImageService;

    // 🧷 Subir imagen (solo admin)
    @PostMapping(consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadHomeImage(@RequestParam("file") MultipartFile file) {
        try {
            HomeImage saved = homeImageService.saveHomeImage(file);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir imagen: " + e.getMessage());
        }
    }

    // 👁️ Obtener imágenes activas en orden aleatorio (público). limit=0 => todas
    @GetMapping
    public ResponseEntity<List<HomeImage>> getVisibleImages(@RequestParam(defaultValue = "0") int limit) {
        return ResponseEntity.ok(homeImageService.getVisibleImagesRandom(limit));
    }

    // 📋 Listado admin (todas)
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<HomeImage>> getAllAdmin() {
        return ResponseEntity.ok(homeImageService.getAllImages());
    }

    // 🔁 Activar/desactivar (admin)
    @PatchMapping("/admin/{id}/active")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> setActive(@PathVariable Long id, @RequestParam boolean active) {
        HomeImage updated = homeImageService.setActive(id, active);
        return ResponseEntity.ok(updated);
    }

    // 🗑️ Eliminar (admin)
    @DeleteMapping("/admin/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteHomeImage(@PathVariable Long id) {
        homeImageService.deleteHomeImage(id);
        return ResponseEntity.ok("Imagen eliminada correctamente");
    }
}
