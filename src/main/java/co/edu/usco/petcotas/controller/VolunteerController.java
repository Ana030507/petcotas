package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.VolunteerDto;
import co.edu.usco.petcotas.service.VolunteerService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VolunteerController {

    private final VolunteerService volunteerService;

    /**
     * Crear un nuevo voluntariado (solo admin)
     * Campos esperados en multipart/form-data:
     * - name (String)
     * - description (String opcional)
     * - date (String opcional en formato ISO: "2025-11-15T10:00:00")
     * - file (MultipartFile opcional)
     */
    @PostMapping(consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerDto> create(
            @RequestParam String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) MultipartFile file) throws Exception {

        LocalDateTime parsedDate = null;
        if (date != null && !date.isBlank()) {
            try {
                parsedDate = LocalDateTime.parse(date);
            } catch (Exception e) {
                throw new IllegalArgumentException("El formato de fecha debe ser ISO-8601, ej: 2025-11-15T10:00:00");
            }
        }

        VolunteerDto dto = volunteerService.createVolunteer(name, description, parsedDate, file);
        return ResponseEntity.status(201).body(dto);
    }

    /**
     * Editar un voluntariado existente (solo admin)
     * Campos esperados en multipart/form-data:
     * - name (String opcional)
     * - description (String opcional)
     * - date (String opcional en formato ISO: "2025-11-15T10:00:00")
     * - file (MultipartFile opcional)
     */
    @PutMapping(value = "/{id}", consumes = {"multipart/form-data"})
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerDto> update(
            @PathVariable Long id,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String description,
            @RequestParam(required = false) String date,
            @RequestParam(required = false) MultipartFile file) throws Exception {

        LocalDateTime parsedDate = null;
        if (date != null && !date.isBlank()) {
            try {
                parsedDate = LocalDateTime.parse(date);
            } catch (Exception e) {
                throw new IllegalArgumentException("El formato de fecha debe ser ISO-8601, ej: 2025-11-15T10:00:00");
            }
        }

        VolunteerDto dto = volunteerService.updateVolunteer(id, name, description, parsedDate, file);
        return ResponseEntity.ok(dto);
    }

    // Eliminar (admin)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        volunteerService.deleteVolunteer(id);
        return ResponseEntity.noContent().build();
    }

    // Listado público
    @GetMapping
    public ResponseEntity<List<VolunteerDto>> listPublic() {
        return ResponseEntity.ok(volunteerService.getAllPublic());
    }

    // Detalle público
    @GetMapping("/{id}")
    public ResponseEntity<VolunteerDto> getOne(@PathVariable Long id) {
        return ResponseEntity.ok(volunteerService.getById(id));
    }
}
