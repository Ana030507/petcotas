package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para actualización parcial de mascota. Todos los campos son opcionales.
 * Permite cambiar status por id o por nombre.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetUpdateDto {
    private String name;
    private String type;
    private String size;
    private String age;
    private String shortDescription;
    private String fullDescription;
    private String mainImage;
    private Long statusId;     // opcional
    private String statusName; // opcional
}
