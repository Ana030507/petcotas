package co.edu.usco.petcotas.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String profileImageUrl;
    private String role;
}
