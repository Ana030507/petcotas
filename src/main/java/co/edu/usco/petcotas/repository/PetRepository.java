package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import co.edu.usco.petcotas.model.Pet;

import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long>, JpaSpecificationExecutor<Pet> {

    List<Pet> findByStatus_Name(String name); // ahora busca por status.name

    List<Pet> findByTypeIgnoreCase(String type);

    List<Pet> findBySizeIgnoreCase(String size);

    List<Pet> findByNameContainingIgnoreCase(String name);

    List<Pet> findByAdoptedBy_Id(Long userId);
}

