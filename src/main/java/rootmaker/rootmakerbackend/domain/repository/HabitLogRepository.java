package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.habit.HabitLog;

import java.time.LocalDateTime;
import java.util.List;

public interface HabitLogRepository extends JpaRepository<HabitLog, Long> {
    List<HabitLog> findByUserIdAndCreatedAtBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
