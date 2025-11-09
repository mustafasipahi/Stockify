package com.project.envantra.specification;

import com.project.envantra.model.entity.TransactionEntity;
import com.project.envantra.model.request.TransactionSearchRequest;
import jakarta.persistence.criteria.Predicate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.project.envantra.util.DateUtil.getLocalDateTime;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TransactionSpecification {

    public static Specification<TransactionEntity> filter(TransactionSearchRequest request) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("creatorUserId"), getUserId()));
            predicates.add(criteriaBuilder.equal(root.get("brokerId"), request.getBrokerId()));
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = Optional.ofNullable(getLocalDateTime(request.getStartDate()))
                    .orElse(now.minusMonths(1));
            LocalDateTime endDate = Optional.ofNullable(getLocalDateTime(request.getEndDate()))
                    .orElse(now);
            predicates.add(criteriaBuilder.between(root.get("createdDate"), startDate, endDate));
            query.orderBy(criteriaBuilder.desc(root.get("createdDate")));
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
