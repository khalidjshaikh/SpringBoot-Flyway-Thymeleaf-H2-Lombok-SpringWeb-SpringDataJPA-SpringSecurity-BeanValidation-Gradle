package com.artificialintelligence.banking.repository;

import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BankAccountRepository extends JpaRepository<BankAccount, Long> {
    List<BankAccount> findByOwner(User owner);
    Optional<BankAccount> findByAccountNumber(String accountNumber);
    boolean existsByAccountNumber(String accountNumber);
    List<BankAccount> findByOwnerOrderByCreatedAtDesc(User owner);
}