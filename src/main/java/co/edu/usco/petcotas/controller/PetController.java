package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.service.PetService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/pets")
@RequiredArgsConstructor
public class PetController {

    private final PetService petService;

    /**
     * GET /api/pets?q=&type=&size=&page=&pageSize=&sort=
     */
 // dentro de PetController.java
    @GetMapping
    public ResponseEntity<?> listPets(
            @RequestParam Optional<String> status,    // <-- nuevo parámetro
            @RequestParam Optional<String> q,
            @RequestParam Optional<String> type,
            @RequestParam Optional<String> size,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int pageSize,
            @RequestParam(defaultValue = "id,desc") String sort // e.g. "id,desc" or "name,asc"
    ) {
        String[] s = sort.split(",");
        Sort.Direction dir = s.length > 1 && "asc".equalsIgnoreCase(s[1]) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sortObj = Sort.by(dir, s[0]);

        var result = petService.findPublicPets(status, q, type, size, page, pageSize, sortObj);
        return ResponseEntity.ok(result);
    }

    /**
     * GET /api/pets/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<PetDetailDto> getOne(@PathVariable Long id) {
        PetDetailDto dto = petService.findPublicPetById(id);
        return ResponseEntity.ok(dto);
    }
}
