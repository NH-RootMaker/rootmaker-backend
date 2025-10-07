package rootmaker.rootmakerbackend.domain.subscription;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import rootmaker.rootmakerbackend.domain.user.User;

import java.math.BigDecimal;

@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BufferAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder.Default
    private BigDecimal balance = BigDecimal.ZERO; // 잔액

    public void deposit(BigDecimal amount) {
        this.balance = this.balance.add(amount);
    }
}
