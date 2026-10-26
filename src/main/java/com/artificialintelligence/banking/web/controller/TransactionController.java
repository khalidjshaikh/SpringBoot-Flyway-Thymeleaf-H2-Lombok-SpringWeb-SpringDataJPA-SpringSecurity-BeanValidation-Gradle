package com.artificialintelligence.banking.web.controller;

import com.artificialintelligence.banking.domain.BankAccount;
import com.artificialintelligence.banking.domain.TransactionType;
import com.artificialintelligence.banking.domain.User;
import com.artificialintelligence.banking.service.AccountService;
import com.artificialintelligence.banking.service.TransactionService;
import com.artificialintelligence.banking.web.dto.TransferForm;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TransactionController {

    private final AccountService accountService;
    private final TransactionService transactionService;

    public TransactionController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @GetMapping("/transfer")
    public String transferForm(@AuthenticationPrincipal User user, Model model) {
        model.addAttribute("accounts", accountService.findByOwner(user));
        model.addAttribute("form", new TransferForm());
        return "transactions/transfer";
    }

    @PostMapping("/transfer")
    public String transfer(@AuthenticationPrincipal User user,
                           @Valid @ModelAttribute("form") TransferForm form,
                           BindingResult result,
                           Model model,
                           RedirectAttributes redirect) {
        if (result.hasErrors()) {
            model.addAttribute("accounts", accountService.findByOwner(user));
            return "transactions/transfer";
        }
        try {
            BankAccount from = accountService.findById(form.getFromAccountId());
            transactionService.transfer(from, form.getToAccountNumber(), form.getAmount(), form.getDescription());
        } catch (IllegalArgumentException | IllegalStateException ex) {
            model.addAttribute("accounts", accountService.findByOwner(user));
            model.addAttribute("error", ex.getMessage());
            return "transactions/transfer";
        }
        redirect.addFlashAttribute("message", "Transfer completed successfully.");
        return "redirect:/transactions";
    }

    @GetMapping("/transactions")
    public String history(@AuthenticationPrincipal User user,
                          @RequestParam(defaultValue = "0") int page,
                          @RequestParam(required = false) String type,
                          Model model) {
        var pageable = PageRequest.of(page, 10);
        if (type != null && !type.isBlank()) {
            try {
                TransactionType t = TransactionType.valueOf(type);
                model.addAttribute("transactions", transactionService.findByUserAndType(user, t, pageable));
            } catch (IllegalArgumentException e) {
                model.addAttribute("transactions", transactionService.findByUser(user, pageable));
            }
        } else {
            model.addAttribute("transactions", transactionService.findByUser(user, pageable));
        }
        model.addAttribute("accounts", accountService.findByOwner(user));
        model.addAttribute("selectedType", type);
        return "transactions/history";
    }

    @GetMapping("/accounts/{id}/transactions")
    public String accountHistory(@PathVariable Long id,
                                 @RequestParam(defaultValue = "0") int page,
                                 Model model) {
        BankAccount account = accountService.findById(id);
        model.addAttribute("account", account);
        model.addAttribute("transactions", transactionService.findByAccount(account, PageRequest.of(page, 10)));
        return "transactions/account-history";
    }
}