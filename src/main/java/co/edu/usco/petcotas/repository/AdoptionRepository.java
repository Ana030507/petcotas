package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.Query;

import co.edu.usco.petcotas.model.Adoption;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdoptionRepository extends JpaRepository<Adoption, Long> {

	List<Adoption> findByUser_Id(Long userId);

    /**
     * Todas las adopciones solicitadas por un usuario.
     */
    List<Adoption> findByUserId(Long userId); // CAMBIO AQUÍ (Opción A)

    /**
     * Todas las adopciones relacionadas con una mascota (normalmente 0 o 1,
     * porque usamos OneToOne desde Adoption -> Pet).
     */
    List<Adoption> findByPet_Id(Long petId);

    /**
     * Buscar una adopción específica por usuario y mascota (si existe).
     * Útil para evitar duplicados (mismo usuario pidiendo la misma mascota).
     */
    Optional<Adoption> findByUser_IdAndPet_Id(Long userId, Long petId);

    /**
     * Obtener adopciones por estado (pending, approved, rejected).
     */
    List<Adoption> findByStatus(String status);

    /**
     * Obtener adopciones por estado y/o por usuario (combinación útil para panel de admin o perfil).
     */
    List<Adoption> findByStatusAndUser_Id(String status, Long userId);

    /**
     * Comprobar si hay una adopción aprobada para una mascota (útil antes de aprobar otra).
     */
    boolean existsByPet_IdAndStatus(Long petId, String status);
    
    @Query("SELECT a FROM Adoption a JOIN FETCH a.pet p JOIN FETCH a.user u")
    List<Adoption> findAllWithPetAndUser(); // si necesitas sin paginar

    // Para soportar pageable con count separado (forma compatible)
    @Query(value = "SELECT a FROM Adoption a JOIN FETCH a.pet p JOIN FETCH a.user u",
           countQuery = "SELECT count(a) FROM Adoption a")
    Page<Adoption> findAllWithPetAndUser(Pageable pageable);
}
