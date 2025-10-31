package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.PetImage;

import java.util.List;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {

    /**
     * Busca todas las imágenes asociadas a una mascota específica.
     * @param petId identificador de la mascota
     * @return lista de imágenes pertenecientes a esa mascota
     */
    List<PetImage> findByPet_Id(Long petId);
}
