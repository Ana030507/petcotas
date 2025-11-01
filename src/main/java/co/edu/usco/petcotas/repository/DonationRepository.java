package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.usco.petcotas.model.Donation;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * Repositorio para la tabla donations.
 */
@Repository
public interface DonationRepository extends JpaRepository<Donation, Long> {

    // Buscar donaciones de un usuario específico
    List<Donation> findByUserId(Long userId);
}
