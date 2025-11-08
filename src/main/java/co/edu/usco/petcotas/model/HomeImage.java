package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "home_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HomeImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    private String url;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Evita el warning de Lombok @Builder ignorando el default
    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
