package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.PetImage;
import co.edu.usco.petcotas.repository.PetImageRepository;
import co.edu.usco.petcotas.repository.PetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ImageService {

    // Si quieres cambiar la ruta, modifica esta constante.
    private static final String UPLOAD_DIR = "C:/petcotas/uploads/";

    private final PetRepository petRepository;
    private final PetImageRepository petImageRepository;

    /**
     * Guarda un archivo en disco dentro de la subcarpeta indicada y devuelve la URL pública.
     * Ej: saveImage(file, "pets/5") -> "/uploads/pets/5/uuid.jpg"
     */
    public String saveImage(MultipartFile file, String subfolder) throws IOException {
        if (file == null || file.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Archivo vacío");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se permiten archivos de imagen");
        }

        // Construir carpeta: UPLOAD_DIR/subfolder
        Path uploadPath = Paths.get(UPLOAD_DIR, subfolder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // Manejar originalFilename seguro
        String original = file.getOriginalFilename() == null ? "" : file.getOriginalFilename();
        String extension = "";
        int idx = original.lastIndexOf('.');
        if (idx >= 0) extension = original.substring(idx);

        String filename = UUID.randomUUID().toString() + extension;
        Path filePath = uploadPath.resolve(filename);

        // Guardar archivo en disco
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        // Devolver URL pública que sirve WebConfig (/uploads/**)
        return "/uploads/" + subfolder.replace("\\", "/") + "/" + filename;
    }

    /**
     * Guarda una imagen de galería asociada a una mascota:
     *  - guarda el archivo en uploads/pets/{petId}/
     *  - crea registro PetImage (url + FK pet) y lo guarda en BD
     */
    @Transactional
    public PetImage storePetImage(Long petId, MultipartFile file) throws IOException {
        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        String subfolder = "pets/" + petId;
        String url = saveImage(file, subfolder);

        PetImage pi = PetImage.builder()
                .url(url)
                .pet(pet)
                .build();

        return petImageRepository.save(pi);
    }
}
