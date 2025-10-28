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
import com.stockify.project.model.request.UserCreationEmailRequest;
import com.stockify.project.repository.BrokerRepository;
import com.stockify.project.service.email.UserCreationEmailService;
import com.stockify.project.validator.BrokerCreateValidator;
import com.stockify.project.validator.BrokerUpdateValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static com.stockify.project.converter.EmailConverter.toEmailRequest;
import static com.stockify.project.converter.BrokerConverter.toEntity;
import static com.stockify.project.util.DateUtil.getLocalDate;
import static com.stockify.project.util.UserInfoGenerator.generatePassword;
import static com.stockify.project.util.TenantContext.*;
import static com.stockify.project.util.UserInfoGenerator.generateUsername;

@Slf4j
@Service
@RequiredArgsConstructor
public class BrokerPostService {

    private final BrokerRepository brokerRepository;
    private final BrokerCreateValidator brokerCreateValidator;
    private final BrokerUpdateValidator brokerUpdateValidator;
    private final UserPostService userPostService;
    private final UserGetService userGetService;
    private final UserCreationEmailService userCreationEmailService;

    @Transactional
    public BrokerDto save(BrokerCreateRequest request) {
        brokerCreateValidator.validate(request);
        String username = generateUsername(request.getFirstName(), request.getLastName());
        String password = generatePassword();
        UserEntity creatorUser = getUser();
        UserEntity brokerUser = userPostService.saveBrokerUser(request, username, password);
        BrokerEntity brokerEntity = toEntity(request, brokerUser.getId());
        BrokerEntity savedBrokerEntity = brokerRepository.save(brokerEntity);
        UserCreationEmailRequest emailRequest = toEmailRequest(username, password, creatorUser, brokerUser);
        userCreationEmailService.sendUserCreationNotification(emailRequest);
        return BrokerConverter.toIdDto(savedBrokerEntity);
    }

    @Transactional
    public BrokerDto update(BrokerUpdateRequest request) {
        if (request.getBrokerId() == null) {
            throw new BrokerIdException();
        }
        BrokerEntity broker = brokerRepository.findByIdAndTenantId(request.getBrokerId(), getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        UserEntity user = userGetService.findById(broker.getBrokerUserId());
        if (StringUtils.isNotBlank(request.getFirstName()) && StringUtils.isNotBlank(request.getLastName())) {
            brokerUpdateValidator.validateFirstNameAndLastName(request.getFirstName(), request.getLastName());
            user.setFirstName(request.getFirstName());
            user.setLastName(request.getLastName());
        } else if (StringUtils.isNotBlank(request.getFirstName())) {
            brokerUpdateValidator.validateFirstName(request.getFirstName(), user.getLastName());
            user.setFirstName(request.getFirstName());
        } else if (StringUtils.isNotBlank(request.getLastName())) {
            brokerUpdateValidator.validateLastName(user.getFirstName(), request.getLastName());
            user.setLastName(request.getLastName());
        }
        if (request.getDiscountRate() != null) {
            brokerUpdateValidator.validateDiscountRate(request.getDiscountRate());
            broker.setDiscountRate(request.getDiscountRate());
        }
        if (StringUtils.isNotBlank(request.getEmail())) {
            user.setEmail(request.getEmail());
        }
        if (StringUtils.isNotBlank(request.getVkn())) {
            broker.setVkn(request.getVkn());
        }
        if (request.getTargetDay() != null) {
            broker.setTargetDay(getLocalDate(request.getTargetDay()));
        }
        BrokerEntity updatedBrokerEntity = brokerRepository.save(broker);
        userPostService.save(user);
        return BrokerConverter.toIdDto(updatedBrokerEntity);
    }

    @Transactional
    public BrokerDto delete(Long brokerId) {
        BrokerEntity brokerEntity = brokerRepository.findByIdAndTenantId(brokerId, getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(brokerId));
        brokerEntity.setStatus(BrokerStatus.PASSIVE);
        BrokerEntity deletedBrokerEntity = brokerRepository.save(brokerEntity);
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
        BrokerEntity brokerEntity = brokerRepository.findByIdAndTenantId(request.getBrokerId(), getTenantId())
                .orElseThrow(() -> new BrokerNotFoundException(request.getBrokerId()));
        brokerEntity.setDiscountRate(request.getDiscountRate());
        brokerRepository.save(brokerEntity);
    }
}
