package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad VolunteerInscription — representa la inscripción de un usuario
 * a un voluntariado específico.
 */
@Entity
@Table(name = "volunteer_inscriptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerInscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación con el usuario inscrito.
     * Muchos registros pueden pertenecer a un mismo usuario.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Relación con el voluntariado al que se inscribió.
     * Muchos usuarios pueden inscribirse a un mismo voluntariado.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "volunteer_id", nullable = false)
    private Volunteer volunteer;

    /**
     * Estado de la inscripción: “pending”, “accepted” o “rejected”.
     */
    @Column(nullable = false, length = 20)
    private String status;

    /**
     * Fecha de creación de la solicitud.
     */
    private LocalDateTime createdAt;

    /**
     * Fecha en la que fue respondida (aceptada o rechazada) por un admin.
     */
    private LocalDateTime respondedAt;

    /**
     * Admin que revisó la solicitud (puede ser null si está pendiente).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reviewed_by_id")
    private UserEntity reviewedBy;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.status == null) this.status = "pending";
    }
}
