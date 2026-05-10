package com.argenischacon.dentalclinic.specification;

import com.argenischacon.dentalclinic.enums.DentalSpecialty;
import com.argenischacon.dentalclinic.model.Dentist;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class DentistSpecification {

    private DentistSpecification() {}

    public static Specification<Dentist> build(String search, String specialty, Boolean active) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Search: ILIKE across name, lastName, licenseNumber, email
            if (search != null && !search.isBlank()) {
                String pattern = "%" + search.trim().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")),          pattern),
                        cb.like(cb.lower(root.get("lastName")),      pattern),
                        cb.like(cb.lower(root.get("licenseNumber")), pattern),
                        cb.like(cb.lower(root.get("email")),         pattern)
                ));
            }

            // Specialty: exact enum match
            if (specialty != null && !specialty.isBlank()) {
                try {
                    DentalSpecialty specialtyEnum = DentalSpecialty.valueOf(specialty.trim().toUpperCase());
                    predicates.add(cb.equal(root.get("specialty"), specialtyEnum));
                } catch (IllegalArgumentException ignored) {
                    // Unknown specialty value — ignore filter silently
                }
            }

            // Active status
            if (active != null) {
                predicates.add(cb.equal(root.get("active"), active));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
