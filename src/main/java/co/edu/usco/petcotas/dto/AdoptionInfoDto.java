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
public class AdoptionInfoDto {
    private Long adoptionId;
    private Long petId;
    private String petName;
    private String status;         // pending / approved
    private LocalDateTime requestDate; // requestDate en tu entidad
}
