package com.example.bankapp.controller;

import com.example.bankapp.dto.TransferRequest;
import com.example.bankapp.exception.BankException;
import com.example.bankapp.model.Customer;
import com.example.bankapp.service.BankService;
import com.example.bankapp.service.TransactionAuditService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Map;

@Controller
@RequiredArgsConstructor
public class BankController {

    private final BankService bankService;
    private final TransactionAuditService auditService;

    // Dashboard
    @GetMapping("/")
    public String dashboard(
            @RequestParam(required = false) String keyword,
            Model model) {

        model.addAttribute("customers",
                bankService.searchCustomers(keyword));

        model.addAttribute("allCustomers",
                bankService.getAllCustomers());

        model.addAttribute("transactions",
                bankService.getLatestTransactions());

        model.addAttribute("totalCustomers",
                bankService.getAllCustomers().size());

        model.addAttribute("totalBalance",
                bankService.getTotalBalance());

        model.addAttribute("successCount",
                bankService.getSuccessCount());

        model.addAttribute("failedCount",
                bankService.getFailedCount());

        model.addAttribute("keyword", keyword);

        if (!model.containsAttribute("transferRequest")) {
            model.addAttribute("transferRequest",
                    new TransferRequest());
        }

        if (!model.containsAttribute("customer")) {
            model.addAttribute("customer", new Customer());
        }

        return "bank";
    }

    // Add customer
    @PostMapping("/customers")
    public String addCustomer(
            @Valid @ModelAttribute("customer") Customer customer,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "org.springframework.validation.BindingResult.customer",
                    result);

            redirectAttributes.addFlashAttribute("customer", customer);

            return "redirect:/";
        }

        try {
            bankService.createCustomer(customer);

            redirectAttributes.addFlashAttribute(
                    "success", "Customer added successfully!");

        } catch (BankException ex) {
            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/";
    }

    // Transfer money - HTML Form
    @PostMapping(
            value = "/transfer",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE
    )
    public String transferForm(
            @Valid @ModelAttribute("transferRequest")
            TransferRequest request,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute(
                    "error", "Please enter valid transfer details.");

            return "redirect:/";
        }

        try {
            bankService.transfer(
                    request.getFromAccount(),
                    request.getToAccount(),
                    request.getAmount(),
                    request.isSimulateFailure()
            );

            redirectAttributes.addFlashAttribute(
                    "success", "Transfer completed successfully!");

        } catch (Exception ex) {

            // Record failure after the transfer transaction rolls back.
            try {
                auditService.recordFailure(
                        request.getFromAccount(),
                        request.getToAccount(),
                        request.getAmount(),
                        ex.getMessage()
                );

            } catch (Exception auditException) {
                redirectAttributes.addFlashAttribute(
                        "error",
                        ex.getMessage() + ". Failed to save audit record.");

                return "redirect:/";
            }

            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/";
    }

    // Transfer money - Raw JSON (Postman)
    @PostMapping(
            value = "/transfer",
            consumes = MediaType.APPLICATION_JSON_VALUE
    )
    @ResponseBody
    public ResponseEntity<Map<String, String>> transferJson(
            @Valid @RequestBody TransferRequest request,
            BindingResult result) {

        // Validate JSON request
        if (result.hasErrors()) {
            String errorMessage = result.getFieldErrors()
                    .get(0)
                    .getDefaultMessage();

            return ResponseEntity.badRequest()
                    .body(Map.of("error", errorMessage));
        }

        try {
            bankService.transfer(
                    request.getFromAccount(),
                    request.getToAccount(),
                    request.getAmount(),
                    request.isSimulateFailure()
            );

            return ResponseEntity.ok(
                    Map.of("message", "Transfer completed successfully!")
            );

        } catch (Exception ex) {

            // Record failure after the transfer transaction rolls back.
            try {
                auditService.recordFailure(
                        request.getFromAccount(),
                        request.getToAccount(),
                        request.getAmount(),
                        ex.getMessage()
                );

            } catch (Exception auditException) {
                return ResponseEntity
                        .status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(Map.of(
                                "error",
                                ex.getMessage() + ". Failed to save audit record."
                        ));
            }

            return ResponseEntity.badRequest()
                    .body(Map.of("error", ex.getMessage()));
        }
    }

    // Account history
    @GetMapping("/accounts/{accountNumber}/history")
    public String history(
            @PathVariable String accountNumber,
            Model model) {

        Customer customer = bankService.getCustomer(accountNumber);

        model.addAttribute("customer", customer);

        model.addAttribute("transactions",
                bankService.getAccountHistory(accountNumber));

        return "history";
    }
}
