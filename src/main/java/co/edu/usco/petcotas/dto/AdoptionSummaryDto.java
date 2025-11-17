package co.edu.usco.petcotas.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionSummaryDto {
    private Long id;
    private String status;
    private String createdAt;
    private PetSummaryDto pet;     // ahora embebido
    private UserSummaryDto user;
}
