package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Adoption — representa una solicitud o registro de adopción de una mascota.
 * Conecta a un usuario con una mascota y permite que un administrador apruebe o rechace.
 */
@Entity
@Table(name = "adoptions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Adoption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Relación con la mascota adoptada o solicitada.
     * Una mascota solo puede tener una adopción asociada.
     */
    @OneToOne
    @JoinColumn(name = "pet_id", nullable = false)
    private Pet pet;

    /**
     * Usuario que realiza la solicitud de adopción.
     * Un usuario puede tener muchas adopciones (a lo largo del tiempo).
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private UserEntity user;

    /**
     * Estado de la solicitud: pendiente, aprobada o rechazada.
     */
    @Column(nullable = false, length = 20)
    private String status; // pending | approved | rejected

    /**
     * Fecha en que se creó la solicitud.
     */
    @Column(nullable = false)
    private LocalDateTime requestDate;

    /**
     * Fecha en que se aprobó o rechazó.
     */
    private LocalDateTime resolutionDate;

    /**
     * Notas opcionales del administrador (por ejemplo, motivo de rechazo).
     */
    @Column(length = 500)
    private String adminNote;

    @PrePersist
    protected void onCreate() {
        this.requestDate = LocalDateTime.now();
        if (this.status == null) {
            this.status = "pending";
        }
    }
}
