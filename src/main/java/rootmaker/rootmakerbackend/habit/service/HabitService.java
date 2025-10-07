package rootmaker.rootmakerbackend.habit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.domain.habit.Habit;
import rootmaker.rootmakerbackend.domain.habit.HabitLog;
import rootmaker.rootmakerbackend.domain.repository.BufferAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.HabitLogRepository;
import rootmaker.rootmakerbackend.domain.repository.HabitRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;
import rootmaker.rootmakerbackend.domain.user.User;
import rootmaker.rootmakerbackend.habit.dto.HabitDto;
import rootmaker.rootmakerbackend.habit.dto.HabitLogRequest;

@Service
@RequiredArgsConstructor
public class HabitService {

    private final HabitRepository habitRepository;
    private final HabitLogRepository habitLogRepository;
    private final UserRepository userRepository;
    private final BufferAccountRepository bufferAccountRepository;

    public HabitDto getTodayHabit() {
        Habit randomHabit = habitRepository.findRandomHabit();
        return new HabitDto(randomHabit);
    }

    @Transactional
    public void logHabit(Long userId, HabitLogRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Habit habit = habitRepository.findById(request.habitId()).orElseThrow(() -> new IllegalArgumentException("Habit not found"));

        HabitLog habitLog = HabitLog.builder()
                .user(user)
                .habit(habit)
                .isSuccess(request.isSuccess())
                .build();
        habitLogRepository.save(habitLog);

        if (request.isSuccess()) {
            BufferAccount bufferAccount = bufferAccountRepository.findByUserId(userId)
                    .orElseThrow(() -> new IllegalStateException("Buffer account must be created to log a successful habit"));
            bufferAccount.deposit(habit.getSavingAmount());
            bufferAccountRepository.save(bufferAccount);
        }
    }
}
