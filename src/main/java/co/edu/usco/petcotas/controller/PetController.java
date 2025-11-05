package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.PetCreateDto;
import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.dto.PetUpdateDto;
import co.edu.usco.petcotas.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    @GetMapping
    public ResponseEntity<?> listPets(
            @RequestParam Optional<String> status,
            @RequestParam Optional<String> q,
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "id,desc") String sort
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && "asc".equalsIgnoreCase(s[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(dir, s[0]);

        var result = petService.findPublicPets(status, q, type, size, page, pageSize, sortObj);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetDetailDto> getOne(@PathVariable Long id) {
        PetDetailDto dto = petService.findPublicPetById(id);
        return ResponseEntity.ok(dto);
    }

    /* --------------- ADMIN endpoints --------------- */

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDetailDto> create(@RequestBody PetCreateDto dto) {
        PetDetailDto created = petService.createPet(dto);
        return ResponseEntity.status(201).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<PetDetailDto> update(@PathVariable Long id, @RequestBody PetUpdateDto dto) {
        PetDetailDto updated = petService.updatePet(id, dto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        petService.deletePet(id);
        return ResponseEntity.noContent().build();
    }

}
