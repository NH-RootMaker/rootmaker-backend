package rootmaker.rootmakerbackend.domain.subscription;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepositHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_account_id")
    private SubscriptionAccount subscriptionAccount;

    private String month; // 납입 월 (YYYY-MM)
    private Double amount; // 총 납입액
    private Integer count; // 납입 횟수
    private Boolean auto; // 자동이체 여부
}
