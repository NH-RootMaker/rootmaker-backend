package rootmaker.rootmakerbackend.habit.dto;

import lombok.Getter;
import rootmaker.rootmakerbackend.domain.habit.Habit;

import java.math.BigDecimal;

@Getter
public class HabitDto {
    private final Long id;
    private final String content;
    private final BigDecimal savingAmount;

    public HabitDto(Habit habit) {
        this.id = habit.getId();
        this.content = habit.getContent();
        this.savingAmount = habit.getSavingAmount();
    }
}
