package com.project.envantra.service;

import com.project.envantra.model.entity.AccountEntity;
import com.project.envantra.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService {

    private final AccountRepository accountRepository;

    @Transactional
    public void updateToBalance(Long brokerId, BigDecimal newBalance) {
        AccountEntity account = getOrCreateAccount(brokerId);
        BigDecimal oldBalance = account.getBalance();
        account.setBalance(newBalance);
        accountRepository.save(account);
        log.info("Balance updated for broker: {} oldBalance: {} newBalance: {}", brokerId, oldBalance, newBalance);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBalance(Long brokerId) {
        return accountRepository.findByBrokerId(brokerId)
                .map(AccountEntity::getBalance)
                .orElse(BigDecimal.ZERO);
    }

    public Map<Long, BigDecimal> getBalanceMap(List<Long> brokerIds) {
        List<AccountEntity> latestTransactions = accountRepository.findAllByBrokerIdIn(brokerIds);
        return latestTransactions.stream()
                .collect(Collectors.toMap(AccountEntity::getBrokerId, AccountEntity::getBalance));
    }

    private AccountEntity getOrCreateAccount(Long brokerId) {
        return accountRepository.findByBrokerId(brokerId)
                .orElseGet(() -> createNewAccount(brokerId));
    }

    private AccountEntity createNewAccount(Long brokerId) {
        AccountEntity account = new AccountEntity();
        account.setBrokerId(brokerId);
        account.setBalance(BigDecimal.ZERO);
        AccountEntity saved = accountRepository.save(account);
        log.info("New account created for broker {}", brokerId);
        return saved;
    }
}
