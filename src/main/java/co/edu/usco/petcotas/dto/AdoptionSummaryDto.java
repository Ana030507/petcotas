package co.edu.usco.petcotas.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionSummaryDto {
    private Long id;
    private String petName;
    private String username;
    private String status;
    private String createdAt;
}
