package co.edu.usco.petcotas.controller;

import co.edu.usco.petcotas.dto.DonationDto;
import co.edu.usco.petcotas.service.DonationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DonationController {

    private final DonationService donationService;

    // Crear donación (usuario autenticado) — params: amount + message opcional
    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<DonationDto> createDonation(
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String message,
            Authentication authentication) {

        DonationDto created = donationService.createDonation(authentication.getName(), amount, message);
        return ResponseEntity.status(201).body(created);
    }

    // Listar todas las donaciones (solo ADMIN)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<DonationDto>> getAllDonations() {
        return ResponseEntity.ok(donationService.getAllDonations());
    }

    // Mis donaciones (usuario autenticado)
    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public ResponseEntity<List<DonationDto>> getMyDonations(Authentication authentication) {
        return ResponseEntity.ok(donationService.getDonationsForAuthenticatedUser(authentication));
    }

    // Total donado (solo ADMIN)
    @GetMapping("/total")
    public ResponseEntity<Map<String, BigDecimal>> getTotal() {
        BigDecimal total = donationService.getTotalDonatedAmount();
        return ResponseEntity.ok(Map.of("totalDonated", total));
    }
}
