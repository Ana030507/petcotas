package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.model.PetImage;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.UserRepository;
import co.edu.usco.petcotas.service.ImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ImageController {

    private static final String UPLOAD_DIR = "C:/petcotas/uploads/";
    private final ImageService imageService;
    private final UserRepository userRepository;

    // -------------------- Perfil - usuario autenticado --------------------
    // Subir/actualizar MI foto de perfil (cualquiera con token)
    @PostMapping(value = "/profiles/me", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadMyProfileImage(Authentication authentication,
                                                  @RequestParam("file") MultipartFile file) {
        try {
            String username = authentication.getName();

            UserEntity user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

            // Guardar archivo en /uploads/profiles/{userId}/...
            String url = imageService.saveImage(file, "profiles/" + user.getId());

            // Actualizar campo profileImageUrl del usuario
            user.setProfileImageUrl(url);
            userRepository.save(user);

            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("url", url));
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen: " + e.getMessage());
        } catch (ResponseStatusException rse) {
            return ResponseEntity.status(rse.getStatusCode()).body(rse.getReason());
        }
    }

    // Obtener la URL de MI foto de perfil (usuario autenticado)
    @GetMapping("/profiles/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMyProfileImage(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return ResponseEntity.ok(Map.of("url", user.getProfileImageUrl()));
    }

    // -------------------- Galería de mascotas (solo admin) --------------------
    // Subida de imagen de mascota (guarda registro en pet_images) — solo ADMIN
    @PostMapping(value = "/pets/{petId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> uploadPetImage(@PathVariable Long petId,
                                            @RequestParam("file") MultipartFile file) {
        try {
            PetImage saved = imageService.storePetImage(petId, file);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al guardar la imagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Error al subir imagen de mascota: " + e.getMessage());
        }
    }
}
