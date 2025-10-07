package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import rootmaker.rootmakerbackend.domain.habit.Habit;

public interface HabitRepository extends JpaRepository<Habit, Long> {

    @Query(value = "SELECT * FROM habit ORDER BY RAND() LIMIT 1", nativeQuery = true)
    Habit findRandomHabit();
}
