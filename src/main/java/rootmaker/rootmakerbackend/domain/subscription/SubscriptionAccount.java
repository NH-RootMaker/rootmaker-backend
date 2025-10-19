package rootmaker.rootmakerbackend.domain.subscription;

import jakarta.persistence.*;
import lombok.*;
import rootmaker.rootmakerbackend.domain.user.User;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SubscriptionAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    private String accountNumber; // 가상 계좌번호
    private String accountType; // 계좌 유형 (예: REGULAR, YOUTH_DREAM)

    // --- 청약 가점 계산용 상세 정보 (nullable) ---
    private String subscriptionStartDate;

    @Builder.Default
    @OneToMany(mappedBy = "subscriptionAccount", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DepositHistory> depositHistories = new ArrayList<>();

    public void addDepositHistory(DepositHistory history) {
        depositHistories.add(history);
        history.setSubscriptionAccount(this);
    }

    public void setSubscriptionStartDate(String subscriptionStartDate) {
        this.subscriptionStartDate = subscriptionStartDate;
    }
}
