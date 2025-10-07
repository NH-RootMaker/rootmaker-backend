package rootmaker.rootmakerbackend.account.dto;

import java.math.BigDecimal;

public record AutoDebitSetupRequest(BigDecimal amount, int transferDay) {
}
