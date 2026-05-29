package com.argenischacon.dentalclinic.specification;

import com.argenischacon.dentalclinic.model.Service;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class ServiceSpecification {

    private ServiceSpecification() {}

    public static Specification<Service> build(String search, Boolean active) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search: ILIKE across name, serviceCode
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("serviceCode")), pattern)
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
