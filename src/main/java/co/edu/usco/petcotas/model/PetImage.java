package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad PetImage — representa una imagen adicional de una mascota.
 * Cada mascota puede tener varias imágenes asociadas.
 */
@Entity
@Table(name = "pet_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PetImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * URL o ruta local del archivo de imagen.
     * Ejemplo: "assets/img/pet-3-2.jpg"
     */
    @Column(nullable = false)
    private String url;

    /**
     * Relación con la mascota a la que pertenece la imagen.
     * Muchas imágenes pueden pertenecer a una misma mascota.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;
}
