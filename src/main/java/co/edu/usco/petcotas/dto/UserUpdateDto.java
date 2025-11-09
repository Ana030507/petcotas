package co.edu.usco.petcotas.dto;

import lombok.Data;

@Data
public class UserUpdateDto {
    private String username;
    private String email;
    private String profileImageUrl;
}
