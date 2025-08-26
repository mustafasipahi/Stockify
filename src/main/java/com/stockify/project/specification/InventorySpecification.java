package com.stockify.project.specification;

import com.stockify.project.enums.InventoryStatus;
import com.stockify.project.model.entity.InventoryEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class InventorySpecification {

    public static Specification<InventoryEntity> filter(List<InventoryStatus> statusList) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(statusList)) {
                predicates.add(root.get("status").in(statusList));
            }
            predicates.add(criteriaBuilder.equal(root.get("tenantId"), getTenantId()));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
