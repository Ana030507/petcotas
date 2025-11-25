package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.UserProfileDto;
import co.edu.usco.petcotas.dto.UserUpdateDto;
import co.edu.usco.petcotas.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserProfileController {

    private final UserProfileService userProfileService;

    // Perfil propio
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserProfileDto> myProfile(Principal principal) {
        UserProfileDto dto = userProfileService.getProfileByUsername(principal.getName());
        return ResponseEntity.ok(dto);
    }

    // Editar perfil propio (parcial)
    @PatchMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<UserProfileDto> updateMyProfile(Principal principal, @RequestBody UserUpdateDto update) {
        UserProfileDto dto = userProfileService.updateMyProfile(principal.getName(), update);
        return ResponseEntity.ok(dto);
    }
    
 // Obtener perfil de cualquier usuario (solo admin)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserProfileDto> getUserProfileById(@PathVariable Long id) {
        UserProfileDto dto = userProfileService.getProfileById(id);
        return ResponseEntity.ok(dto);
    }

}
