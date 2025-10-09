package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.subscription.BufferAccount;

import java.util.Optional;

public interface BufferAccountRepository extends JpaRepository<BufferAccount, Long> {
    Optional<BufferAccount> findByUserId(Long userId);
}
