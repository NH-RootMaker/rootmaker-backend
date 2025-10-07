package rootmaker.rootmakerbackend.habit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.habit.dto.HabitDto;
import rootmaker.rootmakerbackend.habit.dto.HabitLogRequest;
import rootmaker.rootmakerbackend.habit.service.HabitService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HabitController {

    private final HabitService habitService;

    @GetMapping("/habits/today")
    public ResponseEntity<HabitDto> getTodayHabit() {
        return ResponseEntity.ok(habitService.getTodayHabit());
    }

    @PostMapping("/users/{userId}/habit-logs")
    public ResponseEntity<Void> logHabit(@PathVariable Long userId, @RequestBody HabitLogRequest request) {
        habitService.logHabit(userId, request);
        return ResponseEntity.ok().build();
    }
}
