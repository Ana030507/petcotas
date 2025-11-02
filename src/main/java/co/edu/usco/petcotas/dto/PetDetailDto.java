package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/** DTO con detalle completo de la mascota */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetDetailDto {
    private Long id;
    private String name;
    private String type;
    private String size;
    private String age;
    private String status;
    private String shortDescription;
    private String fullDescription;
    private String mainImage;
    private List<String> images;
}
