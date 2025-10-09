package rootmaker.rootmakerbackend.account.dto;

public record RoadmapStepDto(String step, String title, String status) {
    // status: COMPLETED, CURRENT, PENDING
}
