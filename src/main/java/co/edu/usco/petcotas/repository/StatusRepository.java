package co.edu.usco.petcotas.repository;

import co.edu.usco.petcotas.model.Status;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StatusRepository extends JpaRepository<Status, Long> {
    Optional<Status> findByNameIgnoreCase(String name);
}
