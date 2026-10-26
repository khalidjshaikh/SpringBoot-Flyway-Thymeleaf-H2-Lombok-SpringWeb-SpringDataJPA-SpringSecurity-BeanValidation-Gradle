package com.artificialintelligence.banking.web.controller;

import com.artificialintelligence.banking.domain.AccountStatus;
import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.service.AccountService;
import com.artificialintelligence.banking.service.UserService;
import com.artificialintelligence.banking.service.TransactionService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final AccountService accountService;
    private final TransactionService transactionService;

    public AdminController(UserService userService, AccountService accountService, TransactionService transactionService) {
        this.userService = userService;
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("userCount", userService.findAll().size());
        model.addAttribute("accountCount", accountService.findAll().size());
        return "admin/dashboard";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @GetMapping("/accounts")
    public String accounts(Model model) {
        model.addAttribute("accounts", accountService.findAll());
        return "admin/accounts";
    }

    @PostMapping("/accounts/{id}/status")
    public String updateStatus(@PathVariable Long id,
                               @RequestParam AccountStatus status,
                               RedirectAttributes redirect) {
        accountService.updateStatus(id, status);
        redirect.addFlashAttribute("message", "Account status updated.");
        return "redirect:/admin/accounts";
    }

    @GetMapping("/transactions")
    public String transactions(@RequestParam(defaultValue = "0") int page, Model model) {
        model.addAttribute("transactions", transactionService.findAll(PageRequest.of(page, 20)));
        return "admin/transactions";
    }
}