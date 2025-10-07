package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.subscription.SubscriptionAccount;

import java.util.Optional;

public interface SubscriptionAccountRepository extends JpaRepository<SubscriptionAccount, Long> {
    Optional<SubscriptionAccount> findByUserId(Long userId);
}
