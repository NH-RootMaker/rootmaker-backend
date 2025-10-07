package rootmaker.rootmakerbackend.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rootmaker.rootmakerbackend.domain.repository.UserRepository;
import rootmaker.rootmakerbackend.domain.user.User;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public void updateUserType(String name, String userType) {
        User user = userRepository.findByUsername(name)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + name));
        user.setUserType(userType);
    }
}
