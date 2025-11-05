package co.edu.usco.petcotas.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionDetailDto {
    private Long id;
    private String status;
    private String createdAt;
    private String reviewedAt;
    private String notes;
    private String petName;
    private String userEmail;
}
