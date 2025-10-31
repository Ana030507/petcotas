package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.Volunteer;

/**
 * Repositorio para acceder a los datos de la entidad Volunteer.
 * Permite CRUD completo y consultas automáticas de Spring Data.
 */
@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    // Ejemplo: podrías agregar métodos personalizados si lo necesitas, como:
    // List<Volunteer> findByActiveTrue();  // voluntariados activos
}
