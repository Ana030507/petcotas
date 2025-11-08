package co.edu.usco.petcotas.repository;

import co.edu.usco.petcotas.model.HomeImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface HomeImageRepository extends JpaRepository<HomeImage, Long> {
	List<HomeImage> findAllByActiveTrue();
}

