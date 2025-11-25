package co.edu.usco.petcotas.service;

import co.edu.usco.petcotas.dto.*;
import co.edu.usco.petcotas.dto.UserProfileDto;
import co.edu.usco.petcotas.model.*;
import co.edu.usco.petcotas.mapper.*;
import co.edu.usco.petcotas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Optional;


import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserProfileService {

    private final UserRepository userRepository;
    private final PetRepository petRepository;
    private final AdoptionRepository adoptionRepository;
    private final VolunteerInscriptionRepository inscriptionRepository;
    private final DonationRepository donationRepository;

    // Devuelve el perfil para el username (para /me)
    public UserProfileDto getProfileByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));
        return buildProfileDto(user);
    }

    // Actualiza parcialmente el perfil del usuario autenticado
    public UserProfileDto updateMyProfile(String username, UserUpdateDto update) {
        UserEntity u = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        if (update.getUsername() != null && !update.getUsername().isBlank()) u.setUsername(update.getUsername());
        if (update.getEmail() != null) u.setEmail(update.getEmail());
        if (update.getProfileImageUrl() != null) u.setProfileImageUrl(update.getProfileImageUrl());

        UserEntity saved = userRepository.save(u);
        return buildProfileDto(saved);
    }

    /* ---------- Mapping / builders ---------- */
    private UserProfileDto buildProfileDto(UserEntity user) {
        Long uid = user.getId();

        // Adopted pets
        List<Pet> adopted = petRepository.findByAdoptedBy_Id(uid);
        List<PetSummaryDto> adoptedDtos = adopted.stream()
                .map(PetMapper::toSummary)
                .collect(Collectors.toList());

        // Adoptions by user (use your repo method findByUser_Id(Long userId))
        List<Adoption> adoptions = adoptionRepository.findByUser_Id(uid);

        List<AdoptionInfoDto> pendingAdoptions = adoptions.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("pending"))
                .map(a -> AdoptionInfoDto.builder()
                        .adoptionId(a.getId())
                        .petId(a.getPet().getId())
                        .petName(a.getPet().getName())
                        .status(a.getStatus())
                        .requestDate(a.getRequestDate()) // usas requestDate en tu entidad
                        .build())
                .collect(Collectors.toList());

        List<AdoptionInfoDto> approvedAdoptions = adoptions.stream()
                .filter(a -> a.getStatus() != null && a.getStatus().equalsIgnoreCase("approved"))
                .map(a -> AdoptionInfoDto.builder()
                        .adoptionId(a.getId())
                        .petId(a.getPet().getId())
                        .petName(a.getPet().getName())
                        .status(a.getStatus())
                        .requestDate(a.getRequestDate())
                        .build())
                .collect(Collectors.toList());

        // Volunteer inscriptions by user
        List<VolunteerInscription> inscriptions = inscriptionRepository.findByUser(user);

        List<VolunteerInscriptionDto> pendingIns = inscriptions.stream()
                .filter(i -> i.getStatus() != null && i.getStatus().equalsIgnoreCase("pending"))
                .map(this::mapInscriptionToDto)
                .collect(Collectors.toList());

        List<VolunteerInscriptionDto> acceptedIns = inscriptions.stream()
                .filter(i -> i.getStatus() != null && i.getStatus().equalsIgnoreCase("accepted"))
                .map(this::mapInscriptionToDto)
                .collect(Collectors.toList());

        // Donations
        List<Donation> donations = donationRepository.findByUserId(uid);
        List<DonationDto> donationDtos = donations.stream()
                .map(d -> DonationDto.builder()
                        .id(d.getId())
                        .user(UserSummaryDto.builder()
                              .id(user.getId())
                              .username(user.getUsername())
                              .email(user.getEmail())
                              .profileImageUrl(user.getProfileImageUrl())
                              .build())
                        .amount(d.getAmount())
                        .message(d.getMessage())
                        .createdAt(d.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return UserProfileDto.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .profileImageUrl(user.getProfileImageUrl())
                .role(user.getRole() == null ? null : user.getRole().getName())
                .adoptedPets(adoptedDtos)
                .adoptionPending(pendingAdoptions)
                .adoptionApproved(approvedAdoptions)
                .volunteerPending(pendingIns)
                .volunteerAccepted(acceptedIns)
                .donations(donationDtos)
                .build();
    }

    private VolunteerInscriptionDto mapInscriptionToDto(VolunteerInscription i) {
        return VolunteerInscriptionDto.builder()
                .id(i.getId())
                .volunteerId(i.getVolunteer().getId())
                .volunteerName(i.getVolunteer().getName())
                .userId(i.getUser().getId())
                .username(i.getUser().getUsername())
                .status(i.getStatus())
                .createdAt(i.getCreatedAt())
                .respondedAt(i.getRespondedAt())
                .reviewedById(i.getReviewedBy() == null ? null : i.getReviewedBy().getId())
                .reviewedByUsername(i.getReviewedBy() == null ? null : i.getReviewedBy().getUsername())
                .notes(i.getNotes())
                .build();
    }
    
    public UserProfileDto getProfileById(Long id) {
        UserEntity user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        return buildProfileDto(user); 
    }

}
