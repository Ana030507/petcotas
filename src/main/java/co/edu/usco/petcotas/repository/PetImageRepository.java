package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.PetImage;

import java.util.List;

@Repository
public interface PetImageRepository extends JpaRepository<PetImage, Long> {
    List<PetImage> findByPet_Id(Long petId);
}
