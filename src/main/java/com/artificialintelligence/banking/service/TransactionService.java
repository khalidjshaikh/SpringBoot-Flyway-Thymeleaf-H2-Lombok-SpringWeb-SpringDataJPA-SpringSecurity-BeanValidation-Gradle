package com.artificialintelligence.banking.service;

import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.Transaction;
import com.artificialintelligence.banking.domain.TransactionStatus;
import com.artificialintelligence.banking.domain.TransactionType;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.repository.TransactionRepository;
import java.math.BigDecimal;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository, AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountService = accountService;
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByAccount(BankAccount account, Pageable pageable) {
        return transactionRepository.findByAccountOrderByCreatedAtDesc(account, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByUser(User user, Pageable pageable) {
        return transactionRepository.findByUser(user, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findByUserAndType(User user, TransactionType type, Pageable pageable) {
        return transactionRepository.findByUserAndType(user, type, pageable);
    }

    @Transactional(readOnly = true)
    public Page<Transaction> findAll(Pageable pageable) {
        return transactionRepository.findAllByOrderByCreatedAtDesc(pageable);
    }

    public Transaction deposit(BankAccount account, BigDecimal amount, String description) {
        if (account.getStatus() != com.artificialintelligence.banking.domain.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        BigDecimal newBalance = account.getBalance().add(amount);
        account.setBalance(newBalance);
        accountService.updateBalance(account, newBalance);

        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setType(TransactionType.DEPOSIT);
        txn.setAmount(amount);
        txn.setBalanceAfter(newBalance);
        txn.setDescription(description == null || description.isBlank() ? "Deposit" : description);
        txn.setReferenceNumber("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txn.setStatus(TransactionStatus.COMPLETED);
        return transactionRepository.save(txn);
    }

    public Transaction withdraw(BankAccount account, BigDecimal amount, String description) {
        if (account.getStatus() != com.artificialintelligence.banking.domain.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Account is not active");
        }
        if (account.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        BigDecimal newBalance = account.getBalance().subtract(amount);
        account.setBalance(newBalance);
        accountService.updateBalance(account, newBalance);

        Transaction txn = new Transaction();
        txn.setAccount(account);
        txn.setType(TransactionType.WITHDRAWAL);
        txn.setAmount(amount);
        txn.setBalanceAfter(newBalance);
        txn.setDescription(description == null || description.isBlank() ? "Withdrawal" : description);
        txn.setReferenceNumber("TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        txn.setStatus(TransactionStatus.COMPLETED);
        return transactionRepository.save(txn);
    }

    public void transfer(BankAccount from, String toAccountNumber, BigDecimal amount, String description) {
        if (from.getStatus() != com.artificialintelligence.banking.domain.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Source account is not active");
        }
        BankAccount to = accountService.findByNumber(toAccountNumber);
        if (to.getStatus() != com.artificialintelligence.banking.domain.AccountStatus.ACTIVE) {
            throw new IllegalStateException("Destination account is not active");
        }
        if (from.getId().equals(to.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        if (from.getBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient funds");
        }
        String ref = "TRF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal fromNew = from.getBalance().subtract(amount);
        from.setBalance(fromNew);
        accountService.updateBalance(from, fromNew);

        BigDecimal toNew = to.getBalance().add(amount);
        to.setBalance(toNew);
        accountService.updateBalance(to, toNew);

        Transaction out = new Transaction();
        out.setAccount(from);
        out.setType(TransactionType.TRANSFER_OUT);
        out.setAmount(amount);
        out.setBalanceAfter(fromNew);
        out.setDescription(description == null || description.isBlank() ? "Transfer to " + to.getAccountNumber() : description);
        out.setReferenceNumber(ref);
        out.setRelatedAccount(to);
        transactionRepository.save(out);

        Transaction in = new Transaction();
        in.setAccount(to);
        in.setType(TransactionType.TRANSFER_IN);
        in.setAmount(amount);
        in.setBalanceAfter(toNew);
        in.setDescription("Transfer from " + from.getAccountNumber());
        in.setReferenceNumber(ref + "-IN");
        in.setRelatedAccount(from);
        transactionRepository.save(in);
    }
}