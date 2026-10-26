package com.artificialintelligence.banking.service;

import com.artificialintelligence.banking.domain.AccountStatus;
import com.artificialintelligence.banking.domain.AccountType;
import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.repository.BankAccountRepository;
import java.math.BigDecimal;
import java.security.SecureRandom;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AccountService {

    private final BankAccountRepository accountRepository;
    private final SecureRandom random = new SecureRandom();

    public AccountService(BankAccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    public List<BankAccount> findByOwner(User owner) {
        return accountRepository.findByOwnerOrderByCreatedAtDesc(owner);
    }

    @Transactional(readOnly = true)
    public BankAccount findById(Long id) {
        return accountRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + id));
    }

    @Transactional(readOnly = true)
    public BankAccount findByNumber(String number) {
        return accountRepository.findByAccountNumber(number)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + number));
    }

    public BankAccount create(User owner, AccountType type, BigDecimal initialDeposit) {
        String number = generateAccountNumber();
        BankAccount account = new BankAccount(number, owner, type,
                initialDeposit == null ? BigDecimal.ZERO : initialDeposit);
        return accountRepository.save(account);
    }

    public void updateStatus(Long id, AccountStatus status) {
        BankAccount account = findById(id);
        account.setStatus(status);
        accountRepository.save(account);
    }

    public void updateBalance(BankAccount account, BigDecimal newBalance) {
        account.setBalance(newBalance);
        accountRepository.save(account);
    }

    public List<BankAccount> findAll() {
        return accountRepository.findAll();
    }

    private String generateAccountNumber() {
        String number;
        do {
            StringBuilder sb = new StringBuilder("BK");
            for (int i = 0; i < 10; i++) {
                sb.append(random.nextInt(10));
            }
            number = sb.toString();
        } while (accountRepository.existsByAccountNumber(number));
        return number;
    }
}