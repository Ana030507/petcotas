package co.edu.usco.petcotas.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DonationDto {
    private Long id;
    private UserSummaryDto user;
    private BigDecimal amount;
    private String message;
    private LocalDateTime createdAt;
}
