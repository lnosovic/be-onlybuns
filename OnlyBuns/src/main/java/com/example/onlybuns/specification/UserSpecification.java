package com.example.onlybuns.specification;

import com.example.onlybuns.dto.UserSearchCriteria;
import com.example.onlybuns.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class UserSpecification {

    public static Specification<User> filterByCriteria(UserSearchCriteria criteria) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (criteria.getUsername() != null && !criteria.getUsername().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("username")), "%" + criteria.getUsername().toLowerCase() + "%"));
            }

            if (criteria.getName() != null && !criteria.getName().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + criteria.getName().toLowerCase() + "%"));
            }

            if (criteria.getSurname() != null && !criteria.getSurname().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("surname")), "%" + criteria.getSurname().toLowerCase() + "%"));
            }

            if (criteria.getEmail() != null && !criteria.getEmail().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("email")), "%" + criteria.getEmail().toLowerCase() + "%"));
            }

            if (criteria.getMinPostCount() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("postCount"), criteria.getMinPostCount()));
            }

            if (criteria.getMaxPostCount() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("postCount"), criteria.getMaxPostCount()));
            }

            // Kombinujemo sve uslove sa AND
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}