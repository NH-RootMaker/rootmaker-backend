package rootmaker.rootmakerbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.domain.repository.SubscriptionAccountRepository;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.Dependent;
import rootmaker.rootmakerbackend.domain.user.User;
import rootmaker.rootmakerbackend.user.dto.HousingProfileUpdateRequest;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SubscriptionAccountRepository subscriptionAccountRepository;

    @Transactional
    public void updateUserType(String name, String userType) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + name));
        user.setUserType(userType);
    }

    @Transactional
    public void updateHousingProfile(String name, HousingProfileUpdateRequest request) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + name));

        // User 엔티티 필드 업데이트
        user.setBirthDate(request.birthDate());
        user.setMaritalStatus(request.maritalStatus());
        user.setMarriageDate(request.marriageDate());
        user.setHomelessStartDate(request.homelessStartDate());

        // 부양가족 정보 업데이트 (기존 정보 삭제 후 새로 추가)
        user.clearDependents();
        if (request.dependents() != null) {
            request.dependents().forEach(req -> {
                Dependent dependent = Dependent.builder()
                        .relationship(req.relationship())
                        .birthDate(req.birthDate())
                        .cohabiting(req.cohabiting())
                        .build();
                user.addDependent(dependent);
            });
        }

        // 청약 계좌 정보 업데이트
        SubscriptionAccount account = subscriptionAccountRepository.findByUser(user)
                .orElseThrow(() -> new IllegalArgumentException("Subscription account not found for user: " + name));
        account.setSubscriptionStartDate(request.subscriptionStartDate());
    }
}
