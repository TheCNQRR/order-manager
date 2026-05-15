package by.java.enterprise.userservice.controller;

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
    public ResponseEntity<?> getUserById(@RequestHeader("Authorization") String token, @PathVariable UUID id) {
        GetUserResponse result = userService.findById(token, id);

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
}
