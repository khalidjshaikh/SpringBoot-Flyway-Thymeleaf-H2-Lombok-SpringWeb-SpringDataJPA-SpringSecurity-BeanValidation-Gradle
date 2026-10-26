package com.artificialintelligence.banking.web.controller;

import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.service.AccountService;
import com.artificialintelligence.banking.service.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public DashboardController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("accounts", accountService.findByOwner(user));
        model.addAttribute("transactions", transactionService.findByUser(user, PageRequest.of(0, 5)).getContent());
        return "dashboard";
    }
}