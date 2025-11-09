package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDto {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String role;

    private List<PetSummaryDto> adoptedPets;

    private List<AdoptionInfoDto> adoptionPending;
    private List<AdoptionInfoDto> adoptionApproved;

    private List<VolunteerInscriptionDto> volunteerPending;
    private List<VolunteerInscriptionDto> volunteerAccepted;

    private List<DonationDto> donations;
}
