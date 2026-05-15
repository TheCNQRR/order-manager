package by.java.enterprise.userservice.controller;

import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.request.LoginRequest;
import by.java.enterprise.userservice.dto.response.AuthResult;
import by.java.enterprise.userservice.dto.response.LoginResponse;
import by.java.enterprise.userservice.dto.response.UserResponse;
import by.java.enterprise.userservice.entity.AuthStatus;
import by.java.enterprise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        UserResponse user = userService.createUser(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = userService.login(request);

        return result.status() == AuthStatus.SUCCESS ?
                ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(result.token(), null)) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new LoginResponse(null, result.errorMessage()));
    }
}
