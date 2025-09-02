package com.stockify.project.specification;

import com.stockify.project.model.entity.InventoryEntity;
import com.stockify.project.model.request.InventorySearchRequest;
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

    public static Specification<InventoryEntity> filter(InventorySearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(request.getStatusList())) {
                predicates.add(root.get("status").in(request.getStatusList()));
            }
            predicates.add(criteriaBuilder.equal(root.get("tenantId"), getTenantId()));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
