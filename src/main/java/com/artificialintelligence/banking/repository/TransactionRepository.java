package com.artificialintelligence.banking.repository;

import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.Transaction;
import com.artificialintelligence.banking.domain.TransactionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    Page<Transaction> findByAccountOrderByCreatedAtDesc(BankAccount account, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE t.account.owner = :user OR t.relatedAccount.owner = :user ORDER BY t.createdAt DESC")
    Page<Transaction> findByUser(@Param("user") com.artificialintelligence.banking.domain.User user, Pageable pageable);

    @Query("SELECT t FROM Transaction t WHERE (t.account.owner = :user OR t.relatedAccount.owner = :user) AND t.type = :type ORDER BY t.createdAt DESC")
    Page<Transaction> findByUserAndType(@Param("user") com.artificialintelligence.banking.domain.User user, @Param("type") TransactionType type, Pageable pageable);

    Page<Transaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}