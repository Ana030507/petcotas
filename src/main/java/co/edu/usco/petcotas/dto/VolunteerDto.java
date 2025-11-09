package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VolunteerDto {
    private Long id;
    private String name;
    private String description;
    private String imageUrl;
    private LocalDateTime date;
    private LocalDateTime createdAt; // opcionalmente LocalDateTime pero string es más flexible para respuestas

    private boolean active;          // 🔹 Campo calculado: true si no ha pasado
    private boolean finished;
}
