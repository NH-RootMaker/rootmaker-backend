package rootmaker.rootmakerbackend.user.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import rootmaker.rootmakerbackend.user.dto.UserTypeUpdateRequest;
import rootmaker.rootmakerbackend.user.service.UserService;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PutMapping("/type")
    public ResponseEntity<Void> updateUserType(@RequestParam String name, @RequestBody UserTypeUpdateRequest request) {
        userService.updateUserType(name, request.userType());
        return ResponseEntity.ok().build();
    }
}
