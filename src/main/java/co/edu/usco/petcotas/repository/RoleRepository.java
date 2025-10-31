package co.edu.usco.petcotas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import co.edu.usco.petcotas.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

