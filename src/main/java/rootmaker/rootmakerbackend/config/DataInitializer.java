package rootmaker.rootmakerbackend.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import rootmaker.rootmakerbackend.domain.habit.Habit;
import rootmaker.rootmakerbackend.domain.repository.*;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;
import rootmaker.rootmakerbackend.domain.subscription.DepositHistory;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Random;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;
    private final HabitRepository habitRepository;
    private final BufferAccountRepository bufferAccountRepository;
    private final AutoDebitRepository autoDebitRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("DataInitializer running...");
        if (userRepository.count() == 0) {
            log.info("User table is empty. Creating initial data for mingyu...");
            User mingyu = User.builder()
                    .username("mingyu")
                    .ageBand("20s")
                    .regionCode("11") // Seoul
                    .incomeBand("4k-5k")
                    .payday(25)
                    .birthDate("1999-01-01")
                    .maritalStatus("SINGLE")
                    .homelessStartDate("2022-01-01")
                    .typeCode("MAPLE")
                    .build();
            userRepository.save(mingyu);

            SubscriptionAccount subAccount = createSubscriptionAccount(mingyu, "1234567890");

            BufferAccount bufferAccount = BufferAccount.builder().user(mingyu).balance(new BigDecimal("50000")).build();
            bufferAccountRepository.save(bufferAccount);

            AutoDebit autoDebit = AutoDebit.builder().subscriptionAccount(subAccount).amount(new BigDecimal("10000")).transferDay(25).isActive(true).build();
            autoDebitRepository.save(autoDebit);
        }

        if (habitRepository.count() == 0) {
            habitRepository.save(Habit.builder().content("오늘 점심은 도시락 먹고 아낀 돈 저축하기").savingAmount(new BigDecimal("8000")).build());
            habitRepository.save(Habit.builder().content("택시 대신 대중교통 이용하고 차액 저축하기").savingAmount(new BigDecimal("5000")).build());
            habitRepository.save(Habit.builder().content("하루 커피 한 잔 값 아껴서 저축하기").savingAmount(new BigDecimal("4500")).build());
        }
    }

    private SubscriptionAccount createSubscriptionAccount(User user, String accountNumber) {
        SubscriptionAccount account = SubscriptionAccount.builder()
                .user(user)
                .accountNumber(accountNumber)
                .accountType("YOUTH_DREAM")
                .subscriptionStartDate("2020-02-29") // 청약 계좌 생성일 추가
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
