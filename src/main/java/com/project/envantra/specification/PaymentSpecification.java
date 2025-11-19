package com.project.envantra.specification;

import com.project.envantra.controller.PaymentSearchRequest;
import com.project.envantra.model.entity.PaymentEntity;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

import static com.project.envantra.util.DateUtil.*;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class PaymentSpecification {

    public static Specification<PaymentEntity> filter(PaymentSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("creatorUserId"), getUserId()));
            predicates.add(criteriaBuilder.equal(root.get("brokerId"), request.getBrokerId()));
            predicates.add(criteriaBuilder.between(root.get("createdDate"), getTodayStartDate(request.getStartDate()), getTodayEndDate(request.getEndDate())));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
