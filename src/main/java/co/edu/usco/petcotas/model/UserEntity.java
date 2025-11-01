package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entidad UserEntity — versión con campo email (opcional) y username.
 */
@Entity
@Table(name = "users", indexes = {
    @Index(columnList = "username", name = "idx_users_username"),
    @Index(columnList = "email", name = "idx_users_email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    // Username obligatorio (identificador para login clásico)
    @Column(nullable = false, unique = true, length = 100)
    private String username;

    // Email opcional (se usará para OAuth y notificaciones)
    @Column(nullable = true, unique = true, length = 150)
    private String email;

    // Hash de la contraseña (BCrypt). No mostrar en toString.
    @ToString.Exclude
    @Column(nullable = false, length = 200)
    private String passwordHash;

    // URL o ruta a imagen de perfil (opcional)
    @Column(length = 500)
    private String profileImageUrl;

    // Rol del usuario (ROLE_USER, ROLE_ADMIN)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id")
    @ToString.Exclude
    private Role role;
}
