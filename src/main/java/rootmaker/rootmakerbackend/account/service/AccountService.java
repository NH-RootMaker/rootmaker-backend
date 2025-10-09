package rootmaker.rootmakerbackend.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.account.dto.AccountStatusResponse;
import rootmaker.rootmakerbackend.account.dto.AutoDebitSetupRequest;
import rootmaker.rootmakerbackend.account.dto.DepositRequest;
import rootmaker.rootmakerbackend.account.dto.RoadmapStepDto;
import rootmaker.rootmakerbackend.domain.repository.AutoDebitRepository;
import rootmaker.rootmakerbackend.domain.repository.BufferAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;
    private final BufferAccountRepository bufferAccountRepository;
    private final AutoDebitRepository autoDebitRepository;

    private User findUserByNameAndAccountNumber(String name, String accountNumber) {
        SubscriptionAccount account = subscriptionAccountRepository.findByAccountNumber(accountNumber)
                .orElseThrow(() -> new IllegalArgumentException("Account not found: " + accountNumber));
        if (!Objects.equals(account.getUser().getUsername(), name)) {
            throw new IllegalArgumentException("User name does not match account owner");
        }
        return account.getUser();
    }

    @Transactional(readOnly = true)
    public AccountStatusResponse getAccountStatus(String name, String accountNumber) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        Optional<SubscriptionAccount> subscriptionAccountOpt = subscriptionAccountRepository.findByUserId(user.getId());
        boolean hasBufferAccount = bufferAccountRepository.findByUserId(user.getId()).isPresent();

        return new AccountStatusResponse(
                subscriptionAccountOpt.isPresent(),
                subscriptionAccountOpt.map(SubscriptionAccount::getAccountType).orElse(null),
                hasBufferAccount
        );
    }

    @Transactional(readOnly = true)
    public List<RoadmapStepDto> getRoadmap(String name, String accountNumber) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        AccountStatusResponse status = getAccountStatus(name, accountNumber);
        Optional<AutoDebit> autoDebitOpt = autoDebitRepository.findBySubscriptionAccount_UserId(user.getId());

        List<RoadmapStepDto> roadmap = new ArrayList<>();
        boolean isCurrentSet = false;

        boolean step1Done = user.getUserType() != null && !user.getUserType().isEmpty();
        roadmap.add(new RoadmapStepDto("TYPE_TEST", "내 청약 유형 찾기", step1Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step1Done;

        boolean step2Done = status.hasSubscriptionAccount();
        roadmap.add(new RoadmapStepDto("CREATE_SUB_ACCOUNT", "청약 계좌 만들기", step2Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step2Done;

        boolean step3Done = status.hasBufferAccount();
        roadmap.add(new RoadmapStepDto("CREATE_BUFFER_ACCOUNT", "버퍼 계좌 만들기", step3Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step3Done;

        boolean step4Done = autoDebitOpt.isPresent();
        roadmap.add(new RoadmapStepDto("SETUP_AUTO_DEBIT", "자동이체 설정하기", step4Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));

        return roadmap;
    }

    @Transactional
    public BufferAccount createBufferAccount(String name, String accountNumber) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        if (bufferAccountRepository.findByUserId(user.getId()).isPresent()) {
            throw new IllegalStateException("Buffer account already exists");
        }
        BufferAccount bufferAccount = BufferAccount.builder().user(user).build();
        return bufferAccountRepository.save(bufferAccount);
    }

    @Transactional
    public BigDecimal depositToBufferAccount(String name, String accountNumber, DepositRequest request) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        BufferAccount bufferAccount = bufferAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Buffer account not found"));
        bufferAccount.deposit(request.amount());
        bufferAccountRepository.save(bufferAccount);
        return bufferAccount.getBalance();
    }

    @Transactional
    public AutoDebit setupAutoDebit(String name, String accountNumber, AutoDebitSetupRequest request) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        SubscriptionAccount subscriptionAccount = subscriptionAccountRepository.findByUserId(user.getId())
                .orElseThrow(() -> new IllegalArgumentException("Subscription account not found"));

        AutoDebit autoDebit = AutoDebit.builder()
                .subscriptionAccount(subscriptionAccount)
                .amount(request.amount())
                .transferDay(request.transferDay())
                .isActive(true)
                .build();
        return autoDebitRepository.save(autoDebit);
    }

    @Transactional(readOnly = true)
    public BigDecimal getBufferAccountBalance(String name, String accountNumber) {
        User user = findUserByNameAndAccountNumber(name, accountNumber);
        return bufferAccountRepository.findByUserId(user.getId())
                .map(BufferAccount::getBalance)
                .orElse(BigDecimal.ZERO);
    }
}