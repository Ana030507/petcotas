package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.model.HomeImage;
import co.edu.usco.petcotas.repository.HomeImageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HomeImageService {

    private static final String UPLOAD_DIR = "C:/petcotas/uploads/home/";
    private final HomeImageRepository homeImageRepository;

    /**
     * Sube imagen y la guarda en DB con active = true por defecto.
     */
    public HomeImage saveHomeImage(MultipartFile file) throws IOException {
        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();

        String fileName = UUID.randomUUID().toString() + "_" + (file.getOriginalFilename() == null ? "img" : file.getOriginalFilename());
        File destination = new File(folder, fileName);
        file.transferTo(destination);

        String imageUrl = "/uploads/home/" + fileName;

        HomeImage image = HomeImage.builder()
                .fileName(fileName)
                .url(imageUrl)
                .createdAt(LocalDateTime.now())
                .active(true)
                .build();

        return homeImageRepository.save(image);
    }

    /**
     * Devuelve las imágenes activas en orden aleatorio, opcionalmente limitado por 'limit'.
     * Si limit <= 0 devuelve todas las activas mezcladas.
     */
    public List<HomeImage> getVisibleImagesRandom(int limit) {
        List<HomeImage> images = homeImageRepository.findAllByActiveTrue();
        Collections.shuffle(images);
        if (limit > 0 && images.size() > limit) {
            return images.subList(0, limit);
        }
        return images;
    }

    /**
     * Lista todas las imágenes (admin) sin filtrar ni reordenar.
     */
    public List<HomeImage> getAllImages() {
        return homeImageRepository.findAll();
    }

    /**
     * Activa/desactiva una imagen (admin).
     */
    @Transactional
    public HomeImage setActive(Long id, boolean active) {
        HomeImage img = homeImageRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "HomeImage no encontrada"));
        img.setActive(active);
        return homeImageRepository.save(img);
    }

    /**
     * Elimina físicamente el archivo (si existe) y el registro en DB.
     */
    @Transactional
    public void deleteHomeImage(Long id) {
        homeImageRepository.findById(id).ifPresent(image -> {
            File file = new File(UPLOAD_DIR + image.getFileName());
            if (file.exists()) file.delete();
            homeImageRepository.delete(image);
        });
    }
}

