package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.UserEntity;
import co.edu.usco.petcotas.model.Volunteer;
import co.edu.usco.petcotas.model.VolunteerInscription;

import java.util.List;

/**
 * Repositorio para las inscripciones de usuarios a voluntariados.
 */
@Repository
public interface VolunteerInscriptionRepository extends JpaRepository<VolunteerInscription, Long> {

    // Devuelve todas las inscripciones de un usuario específico
    List<VolunteerInscription> findByUser(UserEntity user);

    // Devuelve todas las inscripciones para un voluntariado específico
    List<VolunteerInscription> findByVolunteer(Volunteer volunteer);

    // Devuelve todas las inscripciones con un estado específico
    List<VolunteerInscription> findByStatus(String status);
}
