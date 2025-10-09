package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.subscription.AutoDebit;

import java.util.Optional;

public interface AutoDebitRepository extends JpaRepository<AutoDebit, Long> {
    Optional<AutoDebit> findBySubscriptionAccount_UserId(Long userId);
}
