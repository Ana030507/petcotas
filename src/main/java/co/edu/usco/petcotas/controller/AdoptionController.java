package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.AdoptionDetailDto;
import co.edu.usco.petcotas.dto.AdoptionRequestDto;
import co.edu.usco.petcotas.dto.AdoptionStatusUpdateDto;
import co.edu.usco.petcotas.dto.AdoptionSummaryDto;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.model.Adoption;
import co.edu.usco.petcotas.repository.AdoptionRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import co.edu.usco.petcotas.service.AdoptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Controller para gestionar solicitudes de adopción.
 *
 * - POST /api/adoptions            -> crear solicitud (usuario autenticado)
 * - GET  /api/adoptions            -> admin: todas | user: solo las suyas
 * - GET  /api/adoptions/{id}       -> admin o dueño
 * - PATCH /api/adoptions/{id}/status -> admin aprueba/rechaza
 * - DELETE /api/adoptions/{id}     -> admin borra / user cancela (si pending)
 */
@RestController
@RequestMapping("/api/adoptions")
@RequiredArgsConstructor
public class AdoptionController {

    private final AdoptionService adoptionService;
    private final UserRepository userRepository;
    private final AdoptionRepository adoptionRepository;

    // ---------- Helpers ----------
    private Authentication currentAuth() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    private UserEntity currentUserOrThrow() {
        Authentication auth = currentAuth();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }
        String username = auth.getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
    }

    // ---------- Endpoints ----------

    @PostMapping
    public ResponseEntity<AdoptionDetailDto> createAdoption(@RequestBody AdoptionRequestDto dto) {
        UserEntity user = currentUserOrThrow();
        AdoptionDetailDto created = adoptionService.requestAdoption(dto.getPetId(), user.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping
    public ResponseEntity<List<AdoptionSummaryDto>> listAdoptions() {
        Authentication auth = currentAuth();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }

        if (isAdmin(auth)) {
            List<AdoptionSummaryDto> all = adoptionService.getAllAdoptions(null);
            return ResponseEntity.ok(all);
        } else {
            UserEntity user = currentUserOrThrow();
            List<AdoptionSummaryDto> mine = adoptionService.getAllAdoptions(user.getId());
            return ResponseEntity.ok(mine);
        }
    }


    @GetMapping("/{id}")
    public ResponseEntity<AdoptionDetailDto> getById(@PathVariable Long id) {
        Authentication auth = currentAuth();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }

        boolean admin = isAdmin(auth);
        Long requesterId = admin ? null : currentUserOrThrow().getId();
        AdoptionDetailDto dto = adoptionService.getAdoptionById(id, requesterId, admin);
        return ResponseEntity.ok(dto);
    }

    @PatchMapping("/{id}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<AdoptionDetailDto> changeStatus(@PathVariable Long id, @RequestBody AdoptionStatusUpdateDto dto) {
        AdoptionDetailDto updated = adoptionService.updateStatus(id, dto);
        return ResponseEntity.ok(updated);
    }

    /**
     * DELETE /api/adoptions/{id}
     * - Admin: puede eliminar cualquier solicitud (adoptionService.deleteAdoptionByAdmin)
     * - Usuario: puede cancelar su propia solicitud SOLO si está en 'pending' (adoptionService.cancelAdoptionByUser)
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdoption(@PathVariable Long id) {
        Authentication auth = currentAuth();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Debes iniciar sesión");
        }

        boolean admin = isAdmin(auth);
        if (admin) {
            adoptionService.deleteAdoptionByAdmin(id);
            return ResponseEntity.noContent().build();
        } else {
            UserEntity user = currentUserOrThrow();
            adoptionService.cancelAdoptionByUser(id, user.getId());
            return ResponseEntity.noContent().build();
        }
    }
}
