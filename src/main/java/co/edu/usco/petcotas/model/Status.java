package co.edu.usco.petcotas.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "statuses")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Status {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 60)
    private String name; // ejemplo: "available", "adopted", "pending"
}
