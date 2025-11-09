package com.stockify.project.service;

import com.stockify.project.converter.BrokerConverter;
import com.stockify.project.enums.BrokerStatus;
import com.stockify.project.exception.BrokerDiscountRateException;
import com.stockify.project.exception.BrokerIdException;
import com.stockify.project.exception.BrokerNotFoundException;
import com.stockify.project.model.dto.BrokerDto;
import com.stockify.project.model.entity.BrokerEntity;
import com.stockify.project.model.entity.UserEntity;
import com.stockify.project.model.request.BrokerCreateRequest;
import com.stockify.project.model.request.BrokerUpdateRequest;
import com.stockify.project.model.request.DiscountUpdateRequest;
import com.stockify.project.repository.BrokerRepository;
import com.stockify.project.validator.BrokerCreateValidator;
import com.stockify.project.validator.BrokerUpdateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.stockify.project.converter.BrokerConverter.toEntity;
import static com.stockify.project.util.LoginContext.*;

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
        BrokerEntity savedBrokerEntity = brokerRepository.save(toEntity(request, brokerUser.getId()));
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
}
