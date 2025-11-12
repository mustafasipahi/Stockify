package com.project.envantra.converter;

import com.project.envantra.model.dto.BrokerVisitDto;
import com.project.envantra.model.entity.BrokerVisitEntity;
import com.project.envantra.model.request.BrokerVisitRequest;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.project.envantra.util.DateUtil.getTime;
import static com.project.envantra.util.LoginContext.getUserId;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class BrokerVisitConverter {

    public static BrokerVisitEntity toEntity(BrokerVisitRequest request) {
        return BrokerVisitEntity.builder()
                .creatorUserId(getUserId())
                .brokerId(request.getBrokerId())
                .visitDate(LocalDateTime.now())
                .status(request.getStatus())
                .note(request.getNote())
                .build();
    }

    public static BrokerVisitDto toDto(BrokerVisitEntity visitEntity) {
        return BrokerVisitDto.builder()
                .creatorUserId(visitEntity.getCreatorUserId())
                .brokerId(visitEntity.getBrokerId())
                .visitDate(getTime(visitEntity.getVisitDate()))
                .status(visitEntity.getStatus())
                .note(visitEntity.getNote())
                .build();
    }
}
