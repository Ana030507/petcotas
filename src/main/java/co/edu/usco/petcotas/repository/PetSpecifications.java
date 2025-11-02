package co.edu.usco.petcotas.repository;

import co.edu.usco.petcotas.model.Pet;
import org.springframework.data.jpa.domain.Specification;

public final class PetSpecifications {

    private PetSpecifications() {}

    public static Specification<Pet> hasStatusName(String statusName) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.join("status").get("name")), statusName.toLowerCase());
    }

    public static Specification<Pet> hasType(String type) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("type")), type.toLowerCase());
    }

    public static Specification<Pet> hasSize(String size) {
        return (root, query, cb) -> cb.equal(cb.lower(root.get("size")), size.toLowerCase());
    }

    public static Specification<Pet> matchesQuery(String q) {
        return (root, query, cb) -> {
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), like),
                    cb.like(cb.lower(root.get("shortDescription")), like)
            );
        };
    }
}
