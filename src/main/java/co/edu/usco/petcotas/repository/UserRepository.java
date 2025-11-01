package co.edu.usco.petcotas.repository;

import co.edu.usco.petcotas.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsername(String username);
    boolean existsByUsername(String username);

    Optional<UserEntity> findByEmail(String email);
    boolean existsByEmail(String email);
}
