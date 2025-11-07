package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.model.PetImage;
import co.edu.usco.petcotas.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.access.prepost.PreAuthorize;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private static final String UPLOAD_DIR = "C:/petcotas/uploads/";
    private final ImageService imageService;

    // 📦 Subida general (solo admin, por seguridad)
    @PostMapping(value = "/upload/{type}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> uploadImage(
            @PathVariable String type,
            @RequestParam("file") MultipartFile file) throws IOException {

        if (!type.matches("pets|profiles|home")) {
            return ResponseEntity.badRequest().body("Tipo inválido de imagen");
        }

        File folder = new File(UPLOAD_DIR + type);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String fileName = System.currentTimeMillis() + "_" +
                (file.getOriginalFilename() == null ? "img" : file.getOriginalFilename());
        File destination = new File(folder, fileName);
        file.transferTo(destination);

        String imageUrl = "/uploads/" + type + "/" + fileName;
        return ResponseEntity.ok(imageUrl);
    }

    // 🐶 Subida de imagen de mascota (solo admin)
    @PostMapping(value = "/pets/{petId}", consumes = "multipart/form-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadPetImage(@PathVariable Long petId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            PetImage saved = imageService.storePetImage(petId, file);
            return ResponseEntity.status(201).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body("Error al subir imagen de mascota: " + e.getMessage());
        }
    }
}

