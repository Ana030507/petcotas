package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnore;



/**
 * Entidad Pet — representa una mascota disponible o adoptada.
 */
@Entity
@Table(name = "pets")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name; // Nombre de la mascota

    @Column(nullable = false, length = 50)
    private String type; // Ejemplo: "Perro", "Gato"

    @Column(length = 50)
    private String size; // Pequeño, Mediano, Grande

    @Column(nullable = false, length = 20)
    private String age; // "2 años", "6 meses"

   
    /**
     * Ahora es relación ManyToOne con la tabla statuses.
     * Fetch EAGER para poder leer status.getName() sin LazyInitialization problems.
     */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "status_id")
    private Status status;
    
    
    @Column(length = 255)
    private String shortDescription;

    @Column(columnDefinition = "TEXT", length = 2000)
    private String fullDescription;

    @Column(length = 255, nullable = false)
    private String mainImage; // ruta de imagen principal

    private LocalDateTime createdAt;

    /**
     * Relación 1:N con PetImage — galería de fotos de la mascota.
     */
    @OneToMany(mappedBy = "pet", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<PetImage> images = new ArrayList<>();

    
    // Usuario que adoptó la mascota (puede ser null)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "adopted_by_id")
    @JsonIgnore
    private UserEntity adoptedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
