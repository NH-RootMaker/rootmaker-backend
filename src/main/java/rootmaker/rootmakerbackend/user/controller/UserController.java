package rootmaker.rootmakerbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.user.dto.UserTypeUpdateRequest;
import rootmaker.rootmakerbackend.user.service.UserService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/{userId}/type")
    public ResponseEntity<Void> updateUserType(@PathVariable Long userId, @RequestBody UserTypeUpdateRequest request) {
        userService.updateUserType(userId, request.userType());
        return ResponseEntity.ok().build();
    }
}
