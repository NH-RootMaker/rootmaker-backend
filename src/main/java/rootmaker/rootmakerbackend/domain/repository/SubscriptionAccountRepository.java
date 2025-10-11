package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;
import rootmaker.rootmakerbackend.domain.user.User;

import java.util.Optional;

public interface SubscriptionAccountRepository extends JpaRepository<SubscriptionAccount, Long> {
    Optional<SubscriptionAccount> findByAccountNumber(String accountNumber);
    Optional<SubscriptionAccount> findByUserId(Long userId);
    Optional<SubscriptionAccount> findByUser(User user);
}
