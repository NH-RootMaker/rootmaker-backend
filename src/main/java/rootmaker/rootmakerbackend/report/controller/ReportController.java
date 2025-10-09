package rootmaker.rootmakerbackend.report.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.report.dto.MonthlyReportResponse;
import rootmaker.rootmakerbackend.report.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam String name,
            @RequestParam String accountNumber,
            @RequestParam int year,
            @RequestParam int month) {
        return ResponseEntity.ok(reportService.generateMonthlyReport(name, accountNumber, year, month));
    }
}
