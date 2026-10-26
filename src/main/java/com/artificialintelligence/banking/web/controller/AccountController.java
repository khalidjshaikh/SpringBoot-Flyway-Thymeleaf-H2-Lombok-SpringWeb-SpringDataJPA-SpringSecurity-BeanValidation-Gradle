package com.artificialintelligence.banking.web.controller;

import com.artificialintelligence.banking.domain.AccountType;
import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.service.AccountService;
import com.artificialintelligence.banking.service.TransactionService;
import com.artificialintelligence.banking.web.dto.AccountForm;
import com.artificialintelligence.banking.web.dto.DepositForm;
import com.artificialintelligence.banking.web.dto.WithdrawForm;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/accounts")
public class AccountController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public AccountController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping
    public String list(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("accounts", accountService.findByOwner(user));
        return "accounts/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("form", new AccountForm());
        model.addAttribute("types", AccountType.values());
        return "accounts/create";
    }

    @PostMapping
    public String create(@AuthenticationPrincipal User user,
                         @Valid @ModelAttribute("form") AccountForm form,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("types", AccountType.values());
            return "accounts/create";
        }
        accountService.create(user, form.getAccountType(), form.getInitialDeposit());
        redirect.addFlashAttribute("message", "Account created successfully.");
        return "redirect:/accounts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        BankAccount account = accountService.findById(id);
        model.addAttribute("account", account);
        return "accounts/detail";
    }

    @GetMapping("/{id}/deposit")
    public String depositForm(@PathVariable Long id, Model model) {
        model.addAttribute("account", accountService.findById(id));
        model.addAttribute("form", new DepositForm());
        return "accounts/deposit";
    }

    @PostMapping("/{id}/deposit")
    public String deposit(@PathVariable Long id,
                          @Valid @ModelAttribute("form") DepositForm form,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirect) {
        BankAccount account = accountService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "accounts/deposit";
        }
        transactionService.deposit(account, form.getAmount(), form.getDescription());
        redirect.addFlashAttribute("message", "Deposit successful.");
        return "redirect:/accounts/" + id;
    }

    @GetMapping("/{id}/withdraw")
    public String withdrawForm(@PathVariable Long id, Model model) {
        model.addAttribute("account", accountService.findById(id));
        model.addAttribute("form", new WithdrawForm());
        return "accounts/withdraw";
    }

    @PostMapping("/{id}/withdraw")
    public String withdraw(@PathVariable Long id,
                          @Valid @ModelAttribute("form") WithdrawForm form,
                          BindingResult result,
                          Model model,
                          RedirectAttributes redirect) {
        BankAccount account = accountService.findById(id);
        if (result.hasErrors()) {
            model.addAttribute("account", account);
            return "accounts/withdraw";
        }
        try {
            transactionService.withdraw(account, form.getAmount(), form.getDescription());
        } catch (IllegalStateException ex) {
            model.addAttribute("account", account);
            model.addAttribute("error", ex.getMessage());
            return "accounts/withdraw";
        }
        redirect.addFlashAttribute("message", "Withdrawal successful.");
        return "redirect:/accounts/" + id;
    }
}