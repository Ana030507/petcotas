package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.*;
import co.edu.usco.petcotas.model.Adoption;
import co.edu.usco.petcotas.model.Pet;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.AdoptionRepository;
import co.edu.usco.petcotas.repository.PetRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import co.edu.usco.petcotas.dto.AdoptionSummaryDto;
import co.edu.usco.petcotas.dto.AdoptionStatusUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import java.util.List;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;


import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AdoptionService {

    private final AdoptionRepository adoptionRepository;
    private final PetRepository petRepository;
    private final UserRepository userRepository;

    // --------------------------------------------------------
    // LISTAR TODAS (ADMIN)
    // --------------------------------------------------------
    public Page<AdoptionSummaryDto> listAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("requestDate").descending());
        Page<Adoption> adoptionPage = adoptionRepository.findAll(pageable);

        return adoptionPage.map(this::mapToSummaryDto);
    }
    
    public List<AdoptionSummaryDto> getAllAdoptions(Long userId) {
        List<Adoption> adoptions;

        if (userId == null) {
            // Todas (admin)
            adoptions = adoptionRepository.findAll(Sort.by("requestDate").descending());
        } else {
            // Solo las del usuario
            adoptions = adoptionRepository.findByUserId(userId); // coincide con el Repository
        }

        return adoptions.stream()
                .map(this::mapToSummaryDto)
                .toList();
    }


    // --------------------------------------------------------
    // MAPEO A RESUMEN
    // --------------------------------------------------------
    private AdoptionSummaryDto mapToSummaryDto(Adoption adoption) {

        Pet pet = adoption.getPet();
        UserEntity user = adoption.getUser();

        PetSummaryDto petDto = null;
        if (pet != null) {
            petDto = new PetSummaryDto(
                    pet.getId(),
                    pet.getName(),
                    pet.getType(),
                    pet.getSize(),
                    pet.getAge(),
                    pet.getMainImage(),
                    pet.getShortDescription(),
                    pet.getStatus() == null ? null : pet.getStatus().toString()
            );
        }

        UserSummaryDto userDto = null;
        if (user != null) {
            userDto = new UserSummaryDto(
                    user.getId(),
                    user.getUsername(),
                    user.getEmail(),
                    user.getProfileImageUrl()
            );
        }

        return new AdoptionSummaryDto(
                adoption.getId(),
                adoption.getStatus(),
                adoption.getRequestDate() == null ? null : adoption.getRequestDate().toString(),
                petDto,
                userDto
        );
    }


    // --------------------------------------------------------
    // DETALLES
    // --------------------------------------------------------
    public AdoptionDetailDto getDetails(Long id) {
        Optional<Adoption> opt = adoptionRepository.findById(id);

        if (opt.isEmpty()) {
            return null;
        }

        return mapToDetailDto(opt.get());
    }

    private AdoptionDetailDto mapToDetailDto(Adoption adoption) {
        UserEntity user = adoption.getUser();
        Pet pet = adoption.getPet();

        return new AdoptionDetailDto(
                adoption.getId(),
                adoption.getStatus(),
                adoption.getRequestDate() == null ? null : adoption.getRequestDate().toString(),
                adoption.getResolutionDate() == null ? null : adoption.getResolutionDate().toString(),
                adoption.getAdminNote(),

                pet == null ? null : new PetSummaryDto(
                        pet.getId(),
                        pet.getName(),
                        pet.getType(),
                        pet.getSize(),
                        pet.getAge(),
                        pet.getMainImage(),
                        pet.getShortDescription(),
                        pet.getStatus() == null ? null : pet.getStatus().toString()
                ),

                user == null ? null : new UserSummaryDto(
                        user.getId(),
                        user.getUsername(),
                        user.getEmail(),
                        user.getProfileImageUrl()
                )
        );
    }
    
    /**
     * Obtener detalle con control de acceso:
     * - si isAdmin == true devuelve cualquier adopción
     * - si isAdmin == false verifica que requesterId sea el dueño
     */
    public AdoptionDetailDto getAdoptionById(Long id, Long requesterId, boolean isAdmin) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (!isAdmin) {
            if (adoption.getUser() == null || !adoption.getUser().getId().equals(requesterId)) {
                throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tienes acceso a esta solicitud");
            }
        }

        return mapToDetailDto(adoption);
    }

    /**
     * Borrar una adopción (acción de admin).
     * No añade lógica extra: solo borra la entidad.
     */
    public void deleteAdoptionByAdmin(Long id) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        adoptionRepository.delete(adoption);
    }

    /**
     * Cancelar adopción por parte del usuario dueño (solo si está en 'pending').
     * - Verifica que requester sea el dueño
     * - Verifica estado 'pending' (sensible para evitar inconsistencias)
     * - Borra la adopción
     *
     * Nota: aquí no cambiamos el estado de la mascota para evitar añadir lógica extra.
     * Si quieres que el pet vuelva a 'available', lo agregamos después con statusRepository.
     */
    public void cancelAdoptionByUser(Long id, Long userId) {
        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));

        if (adoption.getUser() == null || !adoption.getUser().getId().equals(userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No puedes cancelar esta solicitud");
        }

        if (!"pending".equalsIgnoreCase(adoption.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo se pueden cancelar solicitudes en estado 'pending'");
        }

        adoptionRepository.delete(adoption);
    }


    // --------------------------------------------------------
    // SOLICITAR ADOPCIÓN (USUARIO)
    // --------------------------------------------------------
    public AdoptionDetailDto requestAdoption(Long petId, Long userId) {

        Pet pet = petRepository.findById(petId)
                .orElseThrow(() -> new RuntimeException("Mascota no encontrada"));

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Adoption adoption = new Adoption();
        adoption.setUser(user);
        adoption.setPet(pet);
        adoption.setStatus("pending");
        adoption.setRequestDate(LocalDateTime.now());

        adoptionRepository.save(adoption);

        return mapToDetailDto(adoption);
    }

    // --------------------------------------------------------
    // ACTUALIZAR ESTADO (ACEPTAR | RECHAZAR)
    // --------------------------------------------------------
    public AdoptionDetailDto updateStatus(Long id, AdoptionStatusUpdateDto dto) {

        Adoption adoption = adoptionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        Pet pet = adoption.getPet();
        String currentStatus = adoption.getStatus();
        String newStatus = dto.getNewStatus();

        // Solo se puede actualizar si está pendiente
        if (!"pending".equalsIgnoreCase(currentStatus)) {
            throw new RuntimeException("Solo se pueden actualizar solicitudes pendientes");
        }

        // ---------------- ACCEPTED ----------------
        if ("accepted".equalsIgnoreCase(newStatus) || "approved".equalsIgnoreCase(newStatus)) {
            adoption.setStatus("accepted");
            adoption.setResolutionDate(LocalDateTime.now());
            adoption.setAdminNote(dto.getNotes());

            adoptionRepository.save(adoption);
            return mapToDetailDto(adoption);
        }

        // ---------------- REJECTED ----------------
        if ("rejected".equalsIgnoreCase(newStatus)) {
            adoption.setStatus("rejected");
            adoption.setResolutionDate(LocalDateTime.now());
            adoption.setAdminNote(dto.getNotes());

            // Construimos el DTO antes de borrar
            AdoptionDetailDto responseDto = mapToDetailDto(adoption);

            // Eliminamos la adopción
            adoptionRepository.delete(adoption);
            return responseDto;
        }

        throw new RuntimeException("Estado inválido");
    }

}
