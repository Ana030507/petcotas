package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.PetCreateDto;
import co.edu.usco.petcotas.dto.PetDetailDto;
import co.edu.usco.petcotas.dto.PetSummaryDto;
import co.edu.usco.petcotas.dto.PetUpdateDto;
import co.edu.usco.petcotas.mapper.PetMapper;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.Status;
import co.edu.usco.petcotas.repository.PetRepository;
import co.edu.usco.petcotas.repository.PetSpecifications;
import co.edu.usco.petcotas.repository.StatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PetService {

    private final PetRepository petRepository;
    private final StatusRepository statusRepository;

    // ---------------- existing public methods (list & detail) ----------------
    public Page<PetSummaryDto> findPublicPets(Optional<String> status,
                                             Optional<String> q,
                                             Optional<String> type,
                                             Optional<String> size,
                                             int page,
                                             int pageSize,
                                             Sort sort) {

        Pageable pageable = PageRequest.of(page, pageSize, sort);

        Specification<Pet> spec = Specification.where(null);

        String statusToApply = status
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .map(String::toLowerCase)
                .orElse("all");

        if (!"all".equalsIgnoreCase(statusToApply)) {
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

    public PetDetailDto findPublicPetById(Long id) {
        Pet p = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));
        return PetMapper.toDetail(p);
    }

    // ---------------- admin methods ----------------

    public PetDetailDto createPet(PetCreateDto dto) {
        // validations
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El nombre es obligatorio");
        }

        Pet pet = PetMapper.fromCreateDto(dto);

        // Resolve status: priority -> statusId, statusName, default
        Status status = resolveStatus(dto.getStatusId(), dto.getStatusName());
        pet.setStatus(status);

        Pet saved = petRepository.save(pet);
        return PetMapper.toDetail(saved);
    }

    public PetDetailDto updatePet(Long id, PetUpdateDto dto) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        PetMapper.updateFromDto(pet, dto);

        // update status if provided (id or name)
        if (dto.getStatusId() != null || (dto.getStatusName() != null && !dto.getStatusName().isBlank())) {
            Status s = resolveStatus(dto.getStatusId(), dto.getStatusName());
            pet.setStatus(s);
        }

        Pet updated = petRepository.save(pet);
        return PetMapper.toDetail(updated);
    }

    public void deletePet(Long id) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        if (pet.getAdoptedBy() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No es posible eliminar una mascota ya adoptada");
        }

        petRepository.delete(pet);
    }

    /**
     * Cambia status usando statusId o statusName (si ambos se pasan, se prioriza statusId).
     */
    public PetDetailDto changeStatus(Long id, Long statusId, String statusName) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        Status s = resolveStatus(statusId, statusName);
        pet.setStatus(s);
        Pet saved = petRepository.save(pet);
        return PetMapper.toDetail(saved);
    }

    /* ---------------- helper methods ---------------- */

    /**
     * Resuelve Status a partir de id o nombre. Si ambos son null, intenta obtener
     * un status por defecto (buscando "Disponible" o "Available" y si no encuentra,
     * toma el primer status en la tabla).
     */
    private Status resolveStatus(Long statusId, String statusName) {
        if (statusId != null) {
            return statusRepository.findById(statusId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status no encontrado por id"));
        }

        if (statusName != null && !statusName.isBlank()) {
            return statusRepository.findByNameIgnoreCase(statusName)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Status no encontrado por nombre"));
        }

        // No id ni name: buscar un status por defecto
        // Primero intentamos "Disponible" (español), luego "Available" (inglés), sino el primer status.
        Optional<Status> maybe = statusRepository.findByNameIgnoreCase("Disponible");
        if (maybe.isPresent()) return maybe.get();

        maybe = statusRepository.findByNameIgnoreCase("Available");
        if (maybe.isPresent()) return maybe.get();

        return statusRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "No hay estados definidos en la base de datos"));
    }
}
