package rootmaker.rootmakerbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import rootmaker.rootmakerbackend.domain.habit.Habit;
import rootmaker.rootmakerbackend.domain.repository.HabitRepository;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
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

    @Override
    public void run(String... args) throws Exception {
        // DB에 데이터가 없으면 생성
        if (userRepository.count() == 0) {
            createMockUser("김청약", "20s", "11", "3k-4k", "PINE", 25);
            createMockUser("이주택", "30s", "41", "5k-6k", "CHERRY", 10);
            createMockUser("박내집", "40s", "28", "7k-8k", "MAPLE", 15);
        }

        if (habitRepository.count() == 0) {
            habitRepository.save(Habit.builder().content("오늘 점심은 도시락 먹고 아낀 돈 저축하기").savingAmount(new BigDecimal("8000")).build());
            habitRepository.save(Habit.builder().content("택시 대신 대중교통 이용하고 차액 저축하기").savingAmount(new BigDecimal("5000")).build());
            habitRepository.save(Habit.builder().content("하루 커피 한 잔 값 아껴서 저축하기").savingAmount(new BigDecimal("4500")).build());
        }
    }

    private void createMockUser(String username, String ageBand, String regionCode, String incomeBand, String typeCode, int payday) {
        User user = User.builder()
                .username(username)
                .ageBand(ageBand)
                .regionCode(regionCode)
                .incomeBand(incomeBand)
                .typeCode(typeCode)
                .payday(payday)
                .build();
        userRepository.save(user);

        SubscriptionAccount account = SubscriptionAccount.builder()
                .user(user)
                .accountNumber(generateRandomAccountNumber())
                .build();

        Random random = new Random();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 1; i <= 12; i++) {
            String month = YearMonth.now().minusMonths(i).format(formatter);
            DepositHistory history = DepositHistory.builder()
                    .month(month)
                    .amount(100000.0 + (random.nextDouble() * 50000)) // 10만원 ~ 15만원
                    .count(random.nextInt(3) + 1) // 1~3회
                    .auto(random.nextBoolean())
                    .build();
            account.addDepositHistory(history);
        }
        subscriptionAccountRepository.save(account);
    }

    private String generateRandomAccountNumber() {
        Random random = new Random();
        return String.format("302-%04d-%06d-01", random.nextInt(10000), random.nextInt(1000000));
    }
}
