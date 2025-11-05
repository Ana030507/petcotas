package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO para creación de mascotas (usado por admin).
 * Puedes pasar statusId o statusName; si ninguno es provisto, el servicio
 * asignará un status por defecto (p.ej. "Disponible" o el primer status disponible).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetCreateDto {
    private String name;
    private String type;
    private String size;
    private String age;
    private String shortDescription;
    private String fullDescription;
    private String mainImage; // url o path principal
    private Long statusId;     // opcional: id del status en la tabla status
    private String statusName; // opcional: nombre del status (ej. "Disponible")
}
