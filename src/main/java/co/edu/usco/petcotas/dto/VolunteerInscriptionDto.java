package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerInscriptionDto {
    private Long id;
    private Long volunteerId;
    private String volunteerName;
    private Long userId;
    private String username;
    private String userEmail;
    private String userProfileImageUrl;
    private String status; // pending / accepted / rejected
    private LocalDateTime createdAt;
    private LocalDateTime respondedAt;
    private Long reviewedById;
    private String reviewedByUsername;
    private String notes;
}
