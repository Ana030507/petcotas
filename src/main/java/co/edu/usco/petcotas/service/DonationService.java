package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.DonationDto;
import co.edu.usco.petcotas.dto.UserSummaryDto;
import co.edu.usco.petcotas.model.Donation;
import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.repository.DonationRepository;
import co.edu.usco.petcotas.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DonationService {

    private final DonationRepository donationRepository;
    private final UserRepository userRepository;

    /**
     * Crear una nueva donación asociada al username (obtenido del token).
     */
    @Transactional
    public DonationDto createDonation(String username, BigDecimal amount, String message) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        Donation donation = Donation.builder()
                .user(user)
                .amount(amount)
                .message(message)
                .build();

        Donation saved = donationRepository.save(donation);
        return mapToDto(saved);
    }

    /**
     * Listar todas las donaciones (admin).
     */
    public List<DonationDto> getAllDonations() {
        return donationRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Listar donaciones del usuario autenticado (/me).
     */
    public List<DonationDto> getDonationsForAuthenticatedUser(Authentication authentication) {
        String username = authentication.getName();
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return donationRepository.findByUserId(user.getId())
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    /**
     * Total donado (suma de todos los montos).
     */
    public BigDecimal getTotalDonatedAmount() {
        return donationRepository.findAll().stream()
                .map(Donation::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /* ---------- helper: map entity -> DTO ---------- */
    private DonationDto mapToDto(Donation donation) {
        UserEntity u = donation.getUser();
        UserSummaryDto userDto = null;
        if (u != null) {
            userDto = UserSummaryDto.builder()
                    .id(u.getId())
                    .username(u.getUsername())
                    .email(u.getEmail())
                    .profileImageUrl(u.getProfileImageUrl())
                    .build();
        }

        return DonationDto.builder()
                .id(donation.getId())
                .user(userDto)
                .amount(donation.getAmount())
                .message(donation.getMessage())
                .createdAt(donation.getCreatedAt())
                .build();
    }
}
