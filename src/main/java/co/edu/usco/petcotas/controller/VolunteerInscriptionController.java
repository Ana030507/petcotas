package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.VolunteerInscriptionDto;
import co.edu.usco.petcotas.service.VolunteerInscriptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/volunteers/inscriptions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VolunteerInscriptionController {

    private final VolunteerInscriptionService inscriptionService;

    // Usuario autenticado se inscribe a un voluntariado
    @PostMapping("/{volunteerId}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<VolunteerInscriptionDto> create(
            @PathVariable Long volunteerId,
            @RequestParam(required = false) String notes, // <-- nuevo
            Principal principal) {

        VolunteerInscriptionDto dto = inscriptionService.createInscription(principal.getName(), volunteerId, notes);
        return ResponseEntity.status(201).body(dto);
    }


    // Listar mis inscripciones (usuario)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<VolunteerInscriptionDto>> myInscriptions(Principal principal) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsForUser(principal.getName()));
    }

    // Listar inscripciones por voluntariado (admin)
    @GetMapping("/volunteer/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<VolunteerInscriptionDto>> byVolunteer(@PathVariable Long id) {
        return ResponseEntity.ok(inscriptionService.getInscriptionsForVolunteer(id));
    }

    // Procesar inscripción (aceptar / rechazar) — admin
    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<VolunteerInscriptionDto> updateStatus(
            @PathVariable Long id,
            @RequestParam String newStatus,
            Principal principal) {

        VolunteerInscriptionDto dto = inscriptionService.updateStatus(id, newStatus, principal.getName());
        return ResponseEntity.ok(dto);
    }
}
