package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/** DTO ligero para listar mascotas */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetSummaryDto {
    private Long id;
    private String name;
    private String type;
    private String size;
    private String age;
    private String mainImage;
    private String shortDescription;
    private String status;
}
