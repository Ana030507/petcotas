package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad UserEntity con Lombok.
 * Versión simplificada: solo username, passwordHash, profileImageUrl, role e id.
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    // PK
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Username obligatorio (será el identificador para login)
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    // Hash de la contraseña (BCrypt u otro). Excluido de toString por seguridad.
    @ToString.Exclude
    @Column(nullable = false, length = 200)
    private String passwordHash;

    // URL o ruta a imagen de perfil (opcional)
    @Column(length = 500)
    private String profileImageUrl;

    // Relación con role (ROLE_USER, ROLE_ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private Role role;

    /*
     * Nota: eliminé createdAt, email, fullName y su @PrePersist según lo solicitaste.
     * Si quieres mantener una fecha de creación, podemos reintroducir createdAt más adelante.
     */
}
