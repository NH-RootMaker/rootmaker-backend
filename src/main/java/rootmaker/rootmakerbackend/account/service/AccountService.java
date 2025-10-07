package rootmaker.rootmakerbackend.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.account.dto.AccountStatusResponse;
import rootmaker.rootmakerbackend.account.dto.AutoDebitSetupRequest;
import rootmaker.rootmakerbackend.account.dto.DepositRequest;
import rootmaker.rootmakerbackend.domain.repository.AutoDebitRepository;
import rootmaker.rootmakerbackend.domain.repository.BufferAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.account.dto.RoadmapStepDto;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;
    private final BufferAccountRepository bufferAccountRepository;
    private final AutoDebitRepository autoDebitRepository;

    @Transactional(readOnly = true)
    public AccountStatusResponse getAccountStatus(Long userId) {
        Optional<SubscriptionAccount> subscriptionAccountOpt = subscriptionAccountRepository.findByUserId(userId);
        boolean hasBufferAccount = bufferAccountRepository.findByUserId(userId).isPresent();

        return new AccountStatusResponse(
                subscriptionAccountOpt.isPresent(),
                subscriptionAccountOpt.map(SubscriptionAccount::getAccountType).orElse(null),
                hasBufferAccount
        );
    }

    @Transactional(readOnly = true)
    public List<RoadmapStepDto> getRoadmap(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        AccountStatusResponse status = getAccountStatus(userId);
        Optional<AutoDebit> autoDebitOpt = autoDebitRepository.findBySubscriptionAccount_UserId(userId);

        List<RoadmapStepDto> roadmap = new ArrayList<>();
        boolean isCurrentSet = false;

        // Step 1: 유형 테스트
        boolean step1Done = user.getUserType() != null && !user.getUserType().isEmpty();
        roadmap.add(new RoadmapStepDto("TYPE_TEST", "내 청약 유형 찾기", step1Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step1Done;

        // Step 2: 청약 계좌 만들기
        boolean step2Done = status.hasSubscriptionAccount();
        roadmap.add(new RoadmapStepDto("CREATE_SUB_ACCOUNT", "청약 계좌 만들기", step2Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step2Done;

        // Step 3: 버퍼 계좌 만들기
        boolean step3Done = status.hasBufferAccount();
        roadmap.add(new RoadmapStepDto("CREATE_BUFFER_ACCOUNT", "버퍼 계좌 만들기", step3Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));
        isCurrentSet = isCurrentSet || !step3Done;

        // Step 4: 자동이체 설정하기
        boolean step4Done = autoDebitOpt.isPresent();
        roadmap.add(new RoadmapStepDto("SETUP_AUTO_DEBIT", "자동이체 설정하기", step4Done ? "COMPLETED" : (!isCurrentSet ? "CURRENT" : "PENDING")));

        return roadmap;
    }

    @Transactional
    public BufferAccount createBufferAccount(Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (bufferAccountRepository.findByUserId(userId).isPresent()) {
            throw new IllegalStateException("Buffer account already exists");
        }
        BufferAccount bufferAccount = BufferAccount.builder().user(user).build();
        return bufferAccountRepository.save(bufferAccount);
    }

    @Transactional
    public void depositToBufferAccount(Long userId, DepositRequest request) {
        BufferAccount bufferAccount = bufferAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Buffer account not found"));
        bufferAccount.deposit(request.amount());
        bufferAccountRepository.save(bufferAccount);
    }

    @Transactional
    public AutoDebit setupAutoDebit(Long userId, AutoDebitSetupRequest request) {
        SubscriptionAccount subscriptionAccount = subscriptionAccountRepository.findByUserId(userId)
                .orElseThrow(() -> new IllegalArgumentException("Subscription account not found"));

        AutoDebit autoDebit = AutoDebit.builder()
                .subscriptionAccount(subscriptionAccount)
                .amount(request.amount())
                .transferDay(request.transferDay())
                .isActive(true)
                .build();
        return autoDebitRepository.save(autoDebit);
    }
}