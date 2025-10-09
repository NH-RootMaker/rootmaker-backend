package rootmaker.rootmakerbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rootmaker.rootmakerbackend.domain.habit.Habit;
import rootmaker.rootmakerbackend.domain.repository.AutoDebitRepository;
import rootmaker.rootmakerbackend.domain.repository.BufferAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.HabitRepository;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;
import rootmaker.rootmakerbackend.domain.subscription.DepositHistory;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;
    private final HabitRepository habitRepository;
    private final BufferAccountRepository bufferAccountRepository;
    private final AutoDebitRepository autoDebitRepository;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            // 시나리오 1: 신규 사용자
            User newUser = User.builder().username("김신규").ageBand("20s").regionCode("11").incomeBand("3k-4k").payday(10).build();
            userRepository.save(newUser);

            // 시나리오 2: 청약 계좌만 있는 사용자
            createSubscriptionOnlyUser("이청약", "302-1111-2222-01");

            // 시나리오 3: 모든 설정이 완료된 사용자
            createFullySetupUser("박완성", "302-3333-4444-01");
        }

        if (habitRepository.count() == 0) {
            habitRepository.save(Habit.builder().content("오늘 점심은 도시락 먹고 아낀 돈 저축하기").savingAmount(new BigDecimal("8000")).build());
            habitRepository.save(Habit.builder().content("택시 대신 대중교통 이용하고 차액 저축하기").savingAmount(new BigDecimal("5000")).build());
            habitRepository.save(Habit.builder().content("하루 커피 한 잔 값 아껴서 저축하기").savingAmount(new BigDecimal("4500")).build());
        }
    }

    private void createSubscriptionOnlyUser(String username, String accountNumber) {
        User user = User.builder().username(username).ageBand("30s").regionCode("41").incomeBand("5k-6k").typeCode("CHERRY").payday(25).build();
        userRepository.save(user);
        createSubscriptionAccount(user, accountNumber);
    }

    private void createFullySetupUser(String username, String accountNumber) {
        User user = User.builder().username(username).ageBand("40s").regionCode("28").incomeBand("7k-8k").typeCode("MAPLE").payday(15).build();
        userRepository.save(user);
        SubscriptionAccount subAccount = createSubscriptionAccount(user, accountNumber);

        BufferAccount bufferAccount = BufferAccount.builder().user(user).balance(new BigDecimal("50000")).build();
        bufferAccountRepository.save(bufferAccount);

        AutoDebit autoDebit = AutoDebit.builder().subscriptionAccount(subAccount).amount(new BigDecimal("10000")).transferDay(15).isActive(true).build();
        autoDebitRepository.save(autoDebit);
    }

    private SubscriptionAccount createSubscriptionAccount(User user, String accountNumber) {
        SubscriptionAccount account = SubscriptionAccount.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType("YOUTH_DREAM")
                .build();

        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");
        for (int i = 1; i <= 12; i++) {
            account.addDepositHistory(DepositHistory.builder()
                    .month(YearMonth.now().minusMonths(i).format(formatter))
                    .amount(100000.0 + (random.nextDouble() * 50000))
                    .count(random.nextInt(3) + 1)
                    .auto(random.nextBoolean())
                    .build());
        }
        return subscriptionAccountRepository.save(account);
    }
}
