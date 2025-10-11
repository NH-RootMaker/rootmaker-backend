package rootmaker.rootmakerbackend.user.dto;

import java.util.List;

public record HousingProfileUpdateRequest(
    String birthDate,
    String maritalStatus,
    String marriageDate,
    String homelessStartDate,
    String subscriptionStartDate,
    List<DependentRequest> dependents
) {
    public record DependentRequest(
        String relationship,
        String birthDate,
        boolean cohabiting
    ) {}
}
