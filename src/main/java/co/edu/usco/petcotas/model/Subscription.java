package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad Subscription — versión mínima: solo guarda email.
 */
@Entity
@Table(name = "subscriptions", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"email"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Email suscrito. Un único registro por email.
     */
    @Column(nullable = false, length = 150)
    private String email;
}

