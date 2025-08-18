package com.stockify.project.specification;

import com.stockify.project.model.entity.ProductEntity;
import com.stockify.project.model.request.ProductSearchRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSpecification {

    public static Specification<ProductEntity> searchByText(ProductSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (StringUtils.isNotBlank(request.getSearchText())) {
                String likePattern = "%" + request.getSearchText().toLowerCase() + "%";
                Predicate stockCodePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("stockCode")), likePattern);
                Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), likePattern);
                predicates.add(criteriaBuilder.or(stockCodePredicate, namePredicate));
            }
            if (request.getStatus() != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), request.getStatus()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
