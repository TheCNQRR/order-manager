package by.java.enterprise.userservice.controller;

import by.java.enterprise.jwtservice.annotation.CurrentUserId;
import by.java.enterprise.jwtservice.annotation.CurrentUserRole;
import by.java.enterprise.jwtservice.annotation.RequiredRole;
import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.request.LoginRequest;
import by.java.enterprise.userservice.dto.request.UpdateUserRequest;
import by.java.enterprise.userservice.dto.response.*;
import by.java.enterprise.userservice.entity.AuthStatus;
import by.java.enterprise.userservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

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
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest request) {
        AuthResult result = userService.login(request);

        return result.status() == AuthStatus.SUCCESS ?
                ResponseEntity.status(HttpStatus.OK).body(new LoginResponse(result.token())) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse(result.errorMessage()));
    }

    @GetMapping("/{id}")
    @RequiredRole({"ADMIN", "SUPPORT", "CUSTOMER"})
    public ResponseEntity<?> getUserById(@CurrentUserId UUID userId,
                                         @CurrentUserRole String userRole,
                                         @PathVariable("id") UUID targetId) {
        GetUserResponse result = userService.findById(userId, userRole, targetId);

        return result.errorMessage() == null ?
                ResponseEntity.status(HttpStatus.OK).body(result.user()) :
                result.errorMessage().equals("access denied") ?
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(result.errorMessage())) :
                        ResponseEntity.status(HttpStatus.NOT_FOUND).body(result.errorMessage());
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers(@RequestHeader("Authorization") String token) {
        GetAllUsersResponse result = userService.findAll(token);

        return result.errorMessage() == null ?
                ResponseEntity.status(HttpStatus.OK).body(result.users()) :
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(result.errorMessage()));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> updateUser(@RequestHeader("Authorization") String token,
                                        @PathVariable UUID id,
                                        @RequestBody UpdateUserRequest request) {
        UpdateUserResponse result = userService.updateUser(token, id, request);

        return result.errorMessage() == null ?
                ResponseEntity.status(HttpStatus.OK).body(result.user()) :
                result.errorMessage().equals("access denied") ?
                        ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(result.errorMessage())) :
                        result.errorMessage().contains("doesn't exists") ?
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponse(result.errorMessage())) :
                                ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(result.errorMessage()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        Optional<String> result = userService.deleteUser(token, id);

        return result.isEmpty() ? ResponseEntity.status(HttpStatus.NO_CONTENT).build() :
                ResponseEntity.status(HttpStatus.FORBIDDEN).body(new ErrorResponse(result.get()));
    }
}
