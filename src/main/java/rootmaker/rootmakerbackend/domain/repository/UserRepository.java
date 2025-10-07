package rootmaker.rootmakerbackend.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rootmaker.rootmakerbackend.domain.user.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
