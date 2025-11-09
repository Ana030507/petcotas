package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Volunteer — representa un tipo de voluntariado disponible
 * (por ejemplo, "Cuidado de animales", "Eventos de adopción", etc.)
 */
@Entity
@Table(name = "volunteers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Volunteer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Nombre del voluntariado (por ejemplo: “Cuidado de animales”)
     */
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    /**
     * Descripción del voluntariado
     */
    @Column(length = 1000)
    private String description;
    


    /**
     * Imagen o banner representativo del voluntariado
     */
    @Column(length = 255)
    private String imageUrl;
    
    @Column
    private LocalDateTime date;

    /**
     * Fecha en la que fue creado el voluntariado
     */
    private LocalDateTime createdAt;

    /**
     * Relación 1:N con VolunteerInscription — todas las inscripciones
     * de usuarios a este voluntariado.
     */
    @OneToMany(mappedBy = "volunteer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<VolunteerInscription> inscriptions = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
