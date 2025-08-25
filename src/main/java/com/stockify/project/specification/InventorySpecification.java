package com.stockify.project.specification;

import com.stockify.project.model.entity.tenant.InventoryEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventorySpecification {

    public static Specification<InventoryEntity> filter() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
