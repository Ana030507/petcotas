package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.mapper.PetMapper;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.repository.PetRepository;
import co.edu.usco.petcotas.repository.PetSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;

    /**
     * Lista pública de mascotas con filtros y paginación.
     */
 // dentro de PetService.java
    public Page<PetSummaryDto> findPublicPets(Optional<String> status,
            Optional<String> q,
            Optional<String> type,
            Optional<String> size,
            int page,
            int pageSize,
            Sort sort) {

			Pageable pageable = PageRequest.of(page, pageSize, sort);
			
			// start with no spec
			Specification<Pet> spec = Specification.where(null);
			
			// status: if present and not "all", filter by it; if absent, default to "all"
			String statusToApply = status
				.map(String::trim)
				.filter(s -> !s.isBlank())
				.map(String::toLowerCase)
				.orElse("all"); // por defecto muestras todo
			
			if (!"all".equalsIgnoreCase(statusToApply)) {
			// <-- CORRECCIÓN: usamos hasStatusName, que es el método existente
			spec = spec.and(PetSpecifications.hasStatusName(statusToApply));
			}
			
			if (type.isPresent() && !type.get().isBlank()) {
			spec = spec.and(PetSpecifications.hasType(type.get()));
			}
			if (size.isPresent() && !size.get().isBlank()) {
			spec = spec.and(PetSpecifications.hasSize(size.get()));
			}
			if (q.isPresent() && !q.get().isBlank()) {
			spec = spec.and(PetSpecifications.matchesQuery(q.get()));
			}
			
			Page<Pet> pets = petRepository.findAll(spec, pageable);
			
			return new PageImpl<>(
					pets.stream().map(PetMapper::toSummary).collect(Collectors.toList()),
					pageable,
					pets.getTotalElements()
				);
			}
 
    /**
     * Obtiene detalle de mascota público (lanza 404 si no existe).
     */
    public PetDetailDto findPublicPetById(Long id) {
        Pet p = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        // opcional: si quieres ocultar mascotas adoptadas para el público:
        // if (!"available".equalsIgnoreCase(p.getStatus())) throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no disponible");

        return PetMapper.toDetail(p);
    }
}
