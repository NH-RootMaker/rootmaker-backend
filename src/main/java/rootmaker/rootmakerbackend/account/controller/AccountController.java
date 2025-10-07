package rootmaker.rootmakerbackend.account.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.account.dto.AccountStatusResponse;
import rootmaker.rootmakerbackend.account.service.AccountService;

import rootmaker.rootmakerbackend.account.dto.AutoDebitSetupRequest;
import rootmaker.rootmakerbackend.account.dto.DepositRequest;
import rootmaker.rootmakerbackend.account.dto.RoadmapStepDto;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping("/users/{userId}/roadmap")
    public ResponseEntity<List<RoadmapStepDto>> getRoadmap(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getRoadmap(userId));
    }

    @GetMapping("/users/{userId}/account-status")
    public ResponseEntity<AccountStatusResponse> getAccountStatus(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.getAccountStatus(userId));
    }

    @PostMapping("/users/{userId}/buffer-account")
    public ResponseEntity<BufferAccount> createBufferAccount(@PathVariable Long userId) {
        return ResponseEntity.ok(accountService.createBufferAccount(userId));
    }

    @PostMapping("/users/{userId}/buffer-account/deposit")
    public ResponseEntity<Void> depositToBufferAccount(@PathVariable Long userId, @RequestBody DepositRequest request) {
        accountService.depositToBufferAccount(userId, request);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/users/{userId}/auto-debit")
    public ResponseEntity<AutoDebit> setupAutoDebit(@PathVariable Long userId, @RequestBody AutoDebitSetupRequest request) {
        return ResponseEntity.ok(accountService.setupAutoDebit(userId, request));
    }

    // 로드맵 API 추가 예정
}
