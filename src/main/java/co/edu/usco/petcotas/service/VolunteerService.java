package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.VolunteerDto;
import co.edu.usco.petcotas.model.Volunteer;
import co.edu.usco.petcotas.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerService {

    private final VolunteerRepository volunteerRepository;
    private final ImageService imageService; // tu ImageService: saveImage(MultipartFile, subfolder)

    /**
     * Crear voluntariado (admin). date puede ser null si no se quiere fijar una fecha.
     */
    public VolunteerDto createVolunteer(String name,
                                        String description,
                                        LocalDateTime date,
                                        MultipartFile file) throws IOException {
        if (name == null || name.isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        String imageUrl = null;
        if (file != null && !file.isEmpty()) {
            imageUrl = imageService.saveImage(file, "volunteers");
        }

        Volunteer v = Volunteer.builder()
                .name(name)
                .description(description)
                .date(date)
                .imageUrl(imageUrl)
                .build();

        Volunteer saved = volunteerRepository.save(v);
        return mapToDto(saved);
    }

    /**
     * Actualizar voluntariado (admin). date puede ser null para no cambiar.
     */
    public VolunteerDto updateVolunteer(Long id,
                                        String name,
                                        String description,
                                        LocalDateTime date,
                                        MultipartFile file) throws IOException {
        Volunteer v = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntariado no encontrado"));

        if (name != null && !name.isBlank()) v.setName(name);
        if (description != null) v.setDescription(description);
        if (date != null) v.setDate(date);

        if (file != null && !file.isEmpty()) {
            String imageUrl = imageService.saveImage(file, "volunteers");
            v.setImageUrl(imageUrl);
        }

        Volunteer updated = volunteerRepository.save(v);
        return mapToDto(updated);
    }

    public void deleteVolunteer(Long id) {
        Volunteer v = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntariado no encontrado"));
        volunteerRepository.delete(v);
    }

    /** Lista pública de voluntariados, ya con campos calculados active/finished. */
    public List<VolunteerDto> getAllPublic() {
        return volunteerRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public VolunteerDto getById(Long id) {
        Volunteer v = volunteerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntariado no encontrado"));
        return mapToDto(v);
    }

    /* ----------------- Mapper / Helper ----------------- */

    private VolunteerDto mapToDto(Volunteer v) {
        LocalDateTime now = LocalDateTime.now();
        boolean finished = false;
        boolean active = true;

        if (v.getDate() != null) {
            finished = v.getDate().isBefore(now);
            active = !finished;
        }

        return VolunteerDto.builder()
                .id(v.getId())
                .name(v.getName())
                .description(v.getDescription())
                .imageUrl(v.getImageUrl())
                .date(v.getDate())
                .createdAt(v.getCreatedAt())
                .active(active)
                .finished(finished)
                .build();
    }
}
