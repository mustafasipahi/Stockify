package com.stockify.project.specification;

import com.stockify.project.enums.ProductStatus;
import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductSearchRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.stockify.project.util.TenantContext.getTenantId;
import static com.stockify.project.util.TenantContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecification {

    public static Specification<ProductEntity> filter(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getProductText())) {
                String likePattern = "%" + request.getProductText().toLowerCase() + "%";
                Predicate inventoryCodePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("inventoryCode")), likePattern);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                predicates.add(criteriaBuilder.or(inventoryCodePredicate, namePredicate));
            }
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            } else {
                predicates.add(criteriaBuilder.equal(root.get("status"), ProductStatus.ACTIVE));
            }
            predicates.add(criteriaBuilder.equal(root.get("tenantId"), getTenantId()));
            predicates.add(criteriaBuilder.equal(root.get("creatorUserId"), getUserId()));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<ProductEntity> filterAllPassive() {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("status"), ProductStatus.PASSIVE));
            predicates.add(criteriaBuilder.equal(root.get("creatorUserId"), getUserId()));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
