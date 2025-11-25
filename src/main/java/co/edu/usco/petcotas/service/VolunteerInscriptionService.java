package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.VolunteerInscriptionDto;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.model.Volunteer;
import co.edu.usco.petcotas.model.VolunteerInscription;
import co.edu.usco.petcotas.repository.UserRepository;
import co.edu.usco.petcotas.repository.VolunteerInscriptionRepository;
import co.edu.usco.petcotas.repository.VolunteerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class VolunteerInscriptionService {

    private final VolunteerInscriptionRepository inscriptionRepository;
    private final VolunteerRepository volunteerRepository;
    private final UserRepository userRepository;

 
 @Transactional
 public VolunteerInscriptionDto createInscription(String username, Long volunteerId, String notes) {
     UserEntity user = userRepository.findByUsername(username)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

     Volunteer volunteer = volunteerRepository.findById(volunteerId)
             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntariado no encontrado"));
     
  // después de obtener Volunteer volunteer
     if (volunteer.getDate() != null && volunteer.getDate().isBefore(LocalDateTime.now())) {
         throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No es posible inscribirse: voluntariado finalizado");
     }

  // Buscar si ya existe inscripción de este usuario a este voluntariado
     List<VolunteerInscription> existingList = inscriptionRepository.findByUser(user).stream()
             .filter(i -> i.getVolunteer().getId().equals(volunteerId))
             .toList();

     if (!existingList.isEmpty()) {
         VolunteerInscription existing = existingList.get(0);

         // Si está pending o accepted → bloquear reinscripción
         if (!existing.getStatus().equalsIgnoreCase("rejected")) {
             throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ya estás inscrito a este voluntariado");
         }

         // Si está rejected → permitir reinscribirse eliminando la anterior
         inscriptionRepository.delete(existing);
     }


     VolunteerInscription insc = VolunteerInscription.builder()
             .user(user)
             .volunteer(volunteer)
             .status("pending")
             .notes(notes)  // <-- guardamos la nota
             .createdAt(LocalDateTime.now())
             .build();

     VolunteerInscription saved = inscriptionRepository.save(insc);
     return mapToDto(saved);
 }


    public List<VolunteerInscriptionDto> getInscriptionsForVolunteer(Long volunteerId) {
        Volunteer v = volunteerRepository.findById(volunteerId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Voluntariado no encontrado"));

        return inscriptionRepository.findByVolunteer(v).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    public List<VolunteerInscriptionDto> getInscriptionsForUser(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        return inscriptionRepository.findByUser(user).stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public VolunteerInscriptionDto updateStatus(Long inscriptionId, String newStatus, String adminUsername) {
        VolunteerInscription insc = inscriptionRepository.findById(inscriptionId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Inscripción no encontrada"));

        if (!"pending".equalsIgnoreCase(insc.getStatus())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Solo solicitudes en estado 'pending' pueden procesarse");
        }

        String normalized = newStatus == null ? "" : newStatus.trim().toLowerCase();
        if (!"accepted".equals(normalized) && !"rejected".equals(normalized)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Estado inválido (accepted o rejected)");
        }

        UserEntity admin = userRepository.findByUsername(adminUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Admin no encontrado"));

        insc.setStatus(normalized);
        insc.setRespondedAt(LocalDateTime.now());
        insc.setReviewedBy(admin);

        // Si necesitas alguna acción extra al aceptar (notificar) puedes ponerla aquí

        VolunteerInscription saved = inscriptionRepository.save(insc);
        return mapToDto(saved);
    }

    /* ---------- helpers ---------- */
    private VolunteerInscriptionDto mapToDto(VolunteerInscription i) {
        return VolunteerInscriptionDto.builder()
                .id(i.getId())
                .volunteerId(i.getVolunteer().getId())
                .volunteerName(i.getVolunteer().getName())
                .userId(i.getUser().getId())
                .username(i.getUser().getUsername())
                .userEmail(i.getUser().getEmail())                      // <-- nuevo
                .userProfileImageUrl(i.getUser().getProfileImageUrl())  // <-- nuevo
                .status(i.getStatus())
                .createdAt(i.getCreatedAt())
                .respondedAt(i.getRespondedAt())
                .reviewedById(i.getReviewedBy() == null ? null : i.getReviewedBy().getId())
                .reviewedByUsername(i.getReviewedBy() == null ? null : i.getReviewedBy().getUsername())
                .notes(i.getNotes())                                     // <-- nuevo
                .build();
    }

}
