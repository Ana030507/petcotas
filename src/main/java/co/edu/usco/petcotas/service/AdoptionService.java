package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.AdoptionDetailDto;
import co.edu.usco.petcotas.dto.AdoptionRequestDto;
import co.edu.usco.petcotas.dto.AdoptionStatusUpdateDto;
import co.edu.usco.petcotas.dto.AdoptionSummaryDto;
import co.edu.usco.petcotas.model.Adoption;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.Status;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.AdoptionRepository;
import co.edu.usco.petcotas.repository.PetRepository;
import co.edu.usco.petcotas.repository.StatusRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;
    private final StatusRepository statusRepository;

    /**
     * Crea una solicitud de adopción:
     * - valida que la mascota exista y esté "available"
     * - pone la mascota en status "pending"
     * - crea la Adoption con status "pending"
     */
    @Transactional
    public AdoptionDetailDto createAdoption(Long userId, AdoptionRequestDto dto) {
        Pet pet = petRepository.findById(dto.getPetId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Mascota no encontrada"));

        // Solo permitir solicitud si la mascota está available
        if (pet.getStatus() == null || !"available".equalsIgnoreCase(pet.getStatus().getName())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Mascota no disponible para adopción");
        }

        // Cambiar pet a pending
        Status pendingStatus = statusRepository.findByNameIgnoreCase("pending")
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'pending' no existe"));
        pet.setStatus(pendingStatus);
        petRepository.save(pet);

        // Usuario
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        // Crear adopción
        Adoption adoption = Adoption.builder()
                .pet(pet)
                .user(user)
                .status("pending")
                .requestDate(LocalDateTime.now())
                .build();

        Adoption saved = adoptionRepository.save(adoption);
        return mapToDetailDto(saved);
    }

    /**
     * Lista solicitudes. Si userId == null devuelve todas (admin),
     * si se pasa userId devuelve solo las de ese usuario.
     */
    public List<AdoptionSummaryDto> getAllAdoptions(Long userId) {
        List<Adoption> list;
        if (userId == null) {
            list = adoptionRepository.findAll();
        } else {
            list = adoptionRepository.findByUser_Id(userId);
        }
        return list.stream().map(this::mapToSummaryDto).collect(Collectors.toList());
    }

    /**
     * Obtiene detalle de una solicitud. Si requesterId == null (admin) no valida propietario.
     * Si requesterId != null valida que el requester sea el dueño.
     */
    public AdoptionDetailDto getAdoptionById(Long id, Long requesterId, boolean isAdmin) {
        Adoption a = adoptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (!isAdmin) {
            if (!a.getUser().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta solicitud");
            }
        }

        return mapToDetailDto(a);
    }

    /**
     * Actualiza el estado de una adopción (ADMIN).
     * - approved: adoption.status = "approved", pet.status = "adopted", pet.adoptedBy = adoption.user
     * - rejected:  adoption.status = "rejected", pet.status = "available"
     */
    @Transactional
    public AdoptionDetailDto updateStatus(Long adoptionId, AdoptionStatusUpdateDto dto) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        String newStatus = dto.getNewStatus();
        if (newStatus == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Debe indicar el nuevo estado");
        }
        String normalized = newStatus.trim().toLowerCase();

        if (!"approved".equals(normalized) && !"rejected".equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido (use 'approved' o 'rejected')");
        }

        // Solo procesar desde pending
        if (!"pending".equalsIgnoreCase(adoption.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden procesar solicitudes en estado 'pending'");
        }

        Pet pet = adoption.getPet();
        if (pet == null) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "La adopción no tiene mascota asociada");
        }

        if ("approved".equals(normalized)) {
            // Verificar que la mascota aún esté en pending
            if (pet.getStatus() == null || !"pending".equalsIgnoreCase(pet.getStatus().getName())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La mascota no está en estado 'pending' para ser adoptada");
            }

            Status adoptedStatus = statusRepository.findByNameIgnoreCase("adopted")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'adopted' no existe"));

            pet.setStatus(adoptedStatus);
            pet.setAdoptedBy(adoption.getUser());
            petRepository.save(pet);

            adoption.setStatus("approved");
            adoption.setResolutionDate(LocalDateTime.now());
            adoption.setAdminNote(dto.getNotes());
            Adoption saved = adoptionRepository.save(adoption);
            return mapToDetailDto(saved);

        } else { // rejected
            Status available = statusRepository.findByNameIgnoreCase("available")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'available' no existe"));

            pet.setStatus(available);
            petRepository.save(pet);

            adoption.setStatus("rejected");
            adoption.setResolutionDate(LocalDateTime.now());
            adoption.setAdminNote(dto.getNotes());
            Adoption saved = adoptionRepository.save(adoption);
            return mapToDetailDto(saved);
        }
    }

    /**
     * Cancela la adopción por parte del usuario (solo si es owner y está en pending).
     * - elimina la Adoption y devuelve el pet a "available".
     */
    @Transactional
    public void cancelAdoptionByUser(Long adoptionId, Long userId) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (!adoption.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cancelar esta solicitud");
        }

        if (!"pending".equalsIgnoreCase(adoption.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden cancelar solicitudes en estado 'pending'");
        }

        // devolver pet a available
        Pet pet = adoption.getPet();
        if (pet != null) {
            Status available = statusRepository.findByNameIgnoreCase("available")
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'available' no existe"));
            pet.setStatus(available);
            petRepository.save(pet);
        }

        adoptionRepository.delete(adoption);
    }

    /**
     * Borrar una adopción por parte del admin.
     * Si la adopción estaba 'pending' la mascota pasará a 'available'.
     * Si la adopción estaba 'approved' y la mascota fue marcada adoptada por esa solicitud,
     * se revierte adoptedBy y se pone disponible (esto es una operación invasiva; admin debe usarla con cuidado).
     */
    @Transactional
    public void deleteAdoptionByAdmin(Long adoptionId) {
        Adoption adoption = adoptionRepository.findById(adoptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        Pet pet = adoption.getPet();
        if (pet != null) {
            if ("pending".equalsIgnoreCase(adoption.getStatus())) {
                Status available = statusRepository.findByNameIgnoreCase("available")
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'available' no existe"));
                pet.setStatus(available);
                petRepository.save(pet);
            } else if ("approved".equalsIgnoreCase(adoption.getStatus())) {
                // Si la adopción fue aprobada y la mascota está adoptada por el mismo usuario, revertimos
                if (pet.getAdoptedBy() != null && adoption.getUser() != null &&
                        pet.getAdoptedBy().getId().equals(adoption.getUser().getId())) {
                    pet.setAdoptedBy(null);
                    Status available = statusRepository.findByNameIgnoreCase("available")
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Estado 'available' no existe"));
                    pet.setStatus(available);
                    petRepository.save(pet);
                }
            }
        }

        adoptionRepository.delete(adoption);
    }

    /* ---------------------- Helpers de mapeo a DTOs ---------------------- */

    private AdoptionSummaryDto mapToSummaryDto(Adoption a) {
        return new AdoptionSummaryDto(
                a.getId(),
                a.getPet() == null ? null : a.getPet().getName(),
                a.getUser() == null ? null : a.getUser().getUsername(),
                a.getStatus(),
                a.getRequestDate() == null ? null : a.getRequestDate().toString()
        );
    }

    private AdoptionDetailDto mapToDetailDto(Adoption a) {
        return new AdoptionDetailDto(
                a.getId(),
                a.getStatus(),
                a.getRequestDate() == null ? null : a.getRequestDate().toString(),
                a.getResolutionDate() == null ? null : a.getResolutionDate().toString(),
                a.getAdminNote(),
                a.getPet() == null ? null : a.getPet().getName(),
                a.getUser() == null ? null : a.getUser().getEmail()
        );
    }
}
