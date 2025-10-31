package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad Role — representa un rol de usuario (ROLE_USER, ROLE_ADMIN).
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include // para que equals/hashCode se basen en id
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name; // ejemplo: "ROLE_USER" o "ROLE_ADMIN"
}

