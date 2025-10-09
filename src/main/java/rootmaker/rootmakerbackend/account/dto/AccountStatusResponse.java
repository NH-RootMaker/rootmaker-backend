package rootmaker.rootmakerbackend.account.dto;

public record AccountStatusResponse(Boolean hasSubscriptionAccount, String accountType, Boolean hasBufferAccount) {
}
