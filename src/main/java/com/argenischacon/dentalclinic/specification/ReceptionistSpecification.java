package com.argenischacon.dentalclinic.specification;

import com.argenischacon.dentalclinic.model.Receptionist;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ReceptionistSpecification {

    private ReceptionistSpecification() {}

    public static Specification<Receptionist> build(String search, Boolean active) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search: ILIKE across name, lastName, employeeNumber, email
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("lastName")), pattern),
                        cb.like(cb.lower(root.get("employeeNumber")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)
                ));
            }

            // Active status
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
