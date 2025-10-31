package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.Pet;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {

    /**
     * Obtiene todas las mascotas disponibles para adopción.
     */
    List<Pet> findByStatus(String status);

    /**
     * Busca mascotas por tipo (por ejemplo, "Perro" o "Gato").
     */
    List<Pet> findByTypeIgnoreCase(String type);

    /**
     * Busca mascotas por tamaño (Pequeño, Mediano, Grande).
     */
    List<Pet> findBySizeIgnoreCase(String size);

    /**
     * Busca mascotas por nombre (o parte del nombre).
     */
    List<Pet> findByNameContainingIgnoreCase(String name);

    /**
     * Busca todas las mascotas adoptadas por un usuario específico.
     */
    List<Pet> findByAdoptedBy_Id(Long userId);
}

