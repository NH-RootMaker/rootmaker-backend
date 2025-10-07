package rootmaker.rootmakerbackend.domain.subscription;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AutoDebit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subscription_account_id")
    private SubscriptionAccount subscriptionAccount;

    private BigDecimal amount; // 자동이체 금액

    private int transferDay; // 매월 이체일

    private boolean isActive; // 활성화 여부
}
