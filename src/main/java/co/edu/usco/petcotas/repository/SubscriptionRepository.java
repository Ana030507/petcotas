package co.edu.usco.petcotas.repository;

import co.edu.usco.petcotas.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    /**
     * Buscar suscripción por email (útil para verificar duplicados o para dar de baja).
     */
    Optional<Subscription> findByEmail(String email);

    /**
     * Comprobar existencia por email.
     */
    boolean existsByEmail(String email);
}
