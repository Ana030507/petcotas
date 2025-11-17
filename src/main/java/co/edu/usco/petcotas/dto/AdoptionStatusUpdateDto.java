package co.edu.usco.petcotas.dto;

import lombok.*;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdoptionStatusUpdateDto {
    private String NewStatus; // "approved" o "rejected"
    private String notes;     // opcional
}
