package com.project.envantra.service;

import com.project.envantra.converter.BrokerConverter;
import com.project.envantra.enums.BrokerStatus;
import com.project.envantra.exception.BrokerDiscountRateException;
import com.project.envantra.exception.BrokerIdException;
import com.project.envantra.exception.BrokerNotFoundException;
import com.project.envantra.exception.BrokerOrderException;
import com.project.envantra.model.dto.BrokerDto;
import com.project.envantra.model.entity.BrokerEntity;
import com.project.envantra.model.entity.UserEntity;
import com.project.envantra.model.request.BrokerCreateRequest;
import com.project.envantra.model.request.BrokerOrderUpdateRequest;
import com.project.envantra.model.request.BrokerUpdateRequest;
import com.project.envantra.model.request.DiscountUpdateRequest;
import com.project.envantra.repository.BrokerRepository;
import com.project.envantra.validator.BrokerCreateValidator;
import com.project.envantra.validator.BrokerUpdateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.project.envantra.converter.BrokerConverter.toEntity;
import static com.project.envantra.util.LoginContext.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerPostService {

    private final BrokerRepository brokerRepository;
    private final UserPostService userPostService;

    @Transactional
    public BrokerDto save(BrokerCreateRequest request) {
        BrokerCreateValidator.validate(request);
        UserEntity brokerUser = userPostService.createNewUser(request);
        Integer nextOrder = getNextOrder();
        BrokerEntity savedBrokerEntity = brokerRepository.save(toEntity(request, brokerUser.getId(), nextOrder));
        log.info("User {} saved to broker {}", getUsername(), savedBrokerEntity);
        return BrokerConverter.toIdDto(savedBrokerEntity);
    }

    @Transactional
    public BrokerDto update(BrokerUpdateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        Long brokerId = request.getBrokerId();
        BrokerEntity broker = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        if (request.getDiscountRate() != null) {
            BrokerUpdateValidator.validateDiscountRate(request.getDiscountRate());
            broker.setDiscountRate(request.getDiscountRate());
        }
        if (request.getTargetDayOfWeek() != null) {
            broker.setTargetDayOfWeek(request.getTargetDayOfWeek());
        }
        BrokerEntity updatedBrokerEntity = brokerRepository.save(broker);
        log.info("User {} updated to broker {}", getUsername(), updatedBrokerEntity);
        return BrokerConverter.toIdDto(updatedBrokerEntity);
    }

    @Transactional
    public BrokerDto updateOrder(BrokerOrderUpdateRequest request) {
        List<BrokerEntity> allBrokers = brokerRepository.findByCreatorUserIdOrderByOrderNoAsc(getUserId());
        BrokerEntity brokerToMove = allBrokers.stream()
                .filter(broker -> broker.getId().equals(request.getBrokerId()))
                .findFirst()
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        Integer oldOrder = brokerToMove.getOrderNo();
        Integer newOrder = request.getOrderNo();
        if (Objects.equals(oldOrder, newOrder)) {
            return BrokerConverter.toIdDto(brokerToMove);
        }
        if (newOrder < 1 || newOrder > allBrokers.size()) {
            throw new BrokerOrderException(request.getBrokerId());
        }
        allBrokers.remove(brokerToMove);
        allBrokers.add(newOrder - 1, brokerToMove);
        for (int i = 0; i < allBrokers.size(); i++) {
            allBrokers.get(i).setOrderNo(i + 1);
        }
        brokerRepository.saveAll(allBrokers);
        return BrokerConverter.toIdDto(brokerToMove);
    }

    @Transactional
    public BrokerDto delete(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        brokerEntity.setStatus(BrokerStatus.PASSIVE);
        BrokerEntity deletedBrokerEntity = brokerRepository.save(brokerEntity);
        log.info("User {} deleted to broker {}", getUsername(), deletedBrokerEntity);
        return BrokerConverter.toIdDto(deletedBrokerEntity);
    }

    @Transactional
    public void updateDiscountRate(DiscountUpdateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        if (request.getDiscountRate() == null) {
            throw new BrokerDiscountRateException();
        }
        if (request.getDiscountRate().compareTo(BigDecimal.ZERO) < 0) {
            throw new BrokerDiscountRateException();
        }
        BrokerEntity brokerEntity = brokerRepository.findById(request.getBrokerId())
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        brokerEntity.setDiscountRate(request.getDiscountRate());
        brokerRepository.save(brokerEntity);
    }

    @Transactional
    public BrokerDto activate(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findById(brokerId)
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        brokerEntity.setStatus(BrokerStatus.ACTIVE);
        BrokerEntity deletedBrokerEntity = brokerRepository.save(brokerEntity);
        log.info("User {} activated to broker {}", getUsername(), deletedBrokerEntity);
        return BrokerConverter.toIdDto(deletedBrokerEntity);
    }

    private Integer getNextOrder() {
        return Optional.ofNullable(brokerRepository.findMaxOrderNoByCreatorUserId(getUserId()))
                .orElse(0) + 1;
    }
}
