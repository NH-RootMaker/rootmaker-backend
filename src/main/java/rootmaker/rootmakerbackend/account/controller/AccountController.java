package rootmaker.rootmakerbackend.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.account.dto.AccountStatusResponse;
import rootmaker.rootmakerbackend.account.dto.AutoDebitSetupRequest;
import rootmaker.rootmakerbackend.account.dto.DepositRequest;
import rootmaker.rootmakerbackend.account.dto.RoadmapStepDto;
import rootmaker.rootmakerbackend.account.service.AccountService;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/roadmap")
    public ResponseEntity<List<RoadmapStepDto>> getRoadmap(@RequestParam String name, @RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.getRoadmap(name, accountNumber));
    }

    @GetMapping("/account-status")
    public ResponseEntity<AccountStatusResponse> getAccountStatus(@RequestParam String name, @RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.getAccountStatus(name, accountNumber));
    }

    @PostMapping("/buffer-account")
    public ResponseEntity<BufferAccount> createBufferAccount(@RequestParam String name, @RequestParam String accountNumber) {
        return ResponseEntity.ok(accountService.createBufferAccount(name, accountNumber));
    }

    @PostMapping("/buffer-account/deposit")
    public ResponseEntity<Map<String, BigDecimal>> depositToBufferAccount(@RequestParam String name, @RequestParam String accountNumber, @RequestBody DepositRequest request) {
        BigDecimal newBalance = accountService.depositToBufferAccount(name, accountNumber, request);
        return ResponseEntity.ok(Map.of("newBalance", newBalance));
    }

    @GetMapping("/buffer-account/balance")
    public ResponseEntity<Map<String, BigDecimal>> getBufferAccountBalance(@RequestParam String name, @RequestParam String accountNumber) {
        BigDecimal balance = accountService.getBufferAccountBalance(name, accountNumber);
        return ResponseEntity.ok(Map.of("balance", balance));
    }

    @PostMapping("/auto-debit")
    public ResponseEntity<AutoDebit> setupAutoDebit(@RequestParam String name, @RequestParam String accountNumber, @RequestBody AutoDebitSetupRequest request) {
        return ResponseEntity.ok(accountService.setupAutoDebit(name, accountNumber, request));
    }
}
