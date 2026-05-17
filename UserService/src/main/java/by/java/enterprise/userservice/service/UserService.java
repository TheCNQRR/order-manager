package by.java.enterprise.userservice.service;

import by.java.enterprise.jwtservice.service.JwtService;
import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.request.LoginRequest;
import by.java.enterprise.userservice.dto.request.UpdateUserRequest;
import by.java.enterprise.userservice.dto.response.*;
import by.java.enterprise.userservice.entity.AuthStatus;
import by.java.enterprise.userservice.entity.User;
import by.java.enterprise.userservice.entity.UserRole;
import by.java.enterprise.userservice.kafka.DeleteUserEventProducer;
import by.java.enterprise.userservice.kafka.RegisterUserEventProducer;
import by.java.enterprise.userservice.kafka.UpdateUserEventProducer;
import by.java.enterprise.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RegisterUserEventProducer registerUserEventProducer;
    private final UpdateUserEventProducer updateUserEventProducer;
    private final DeleteUserEventProducer deleteUserEventProducer;
    private final JwtService jwtService;

    BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UserResponse createUser(CreateUserRequest request) {
        User user = User.builder()
                .email(request.email())
                .passwordHash(encoder.encode(request.password()))
                .firstName(request.firstName())
                .lastName(request.lastName())
                .phone(request.phone())
                .userRole(request.role())
                .build();

        userRepository.save(user);
        registerUserEventProducer.sendCreateUserEvent(user);

        return mapToResponse(user);
    }

    public AuthResult login(LoginRequest request) {
        Optional<User> userResult = userRepository.findByEmail(request.email());

        if (userResult.isEmpty()) {
            return new AuthResult(AuthStatus.FAIL, null, "invalid email");
        }

        User user = userResult.get();

        if (!encoder.matches(request.password(), user.getPasswordHash())) {
            return new AuthResult(AuthStatus.FAIL, null, "invalid password");
        }

        String token = jwtService.generateToken(user.getId(), user.getEmail(), user.getUserRole().toString());

        log.info("user with id={} successfully logged in", user.getId());
        return new AuthResult(AuthStatus.SUCCESS, token, null);
    }

    public GetUserResponse findById(UUID userId, String userRole, UUID targetId) {
        boolean access = true;
        if (userRole.equals(UserRole.CUSTOMER.toString())) {
            if (!userId.equals(targetId)) {
                access = false;
            }
        }

        if (!access) {
            return new GetUserResponse(null, "you can't view other profiles");
        }

        Optional<User> userResult = userRepository.findById(targetId);

        return userResult
                .map(user -> new GetUserResponse(mapToResponse(user), null))
                .orElseGet(() -> new GetUserResponse(null, "user with id = {" + targetId + "} doesn't exists"));
    }

    public GetAllUsersResponse findAll(String token) {
        String role = jwtService.parseToken(token).get("role", String.class);

        if (!role.equals(UserRole.ADMIN.toString())) {
            return new GetAllUsersResponse(null, "access denied");
        }

        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(this::mapToResponse)
                .toList();

        return new GetAllUsersResponse(userResponses, null);
    }


    public UpdateUserResponse updateUser(String token, UUID targetId, UpdateUserRequest request) {
        UUID userId = UUID.fromString(jwtService.parseToken(token).get("id", String.class));
        String userRole = jwtService.parseToken(token).get("role", String.class);

        if (!userId.equals(targetId) && !userRole.equals(UserRole.ADMIN.toString())) {
            return new UpdateUserResponse(null, "access denied");
        }

        Optional<User> userResult = userRepository.findById(targetId);
        if (userResult.isEmpty()) {
            return new UpdateUserResponse(null, "user with id = {" + targetId + "} doesn't exists");
        }

        User user = userResult.get();

        if (request.email() != null) {
            user.setEmail(request.email());
        }

        if (request.newPassword() != null) {
            boolean shouldChangePassword = userRole.equals(UserRole.ADMIN.toString()) ||
                    (request.oldPassword() != null && encoder.matches(request.oldPassword(), user.getPasswordHash()));

            if (shouldChangePassword) {
                user.setPasswordHash(encoder.encode(request.newPassword()));
            } else {
                return new UpdateUserResponse(null, "passwords don't match");
            }
        }

        if (request.firstName() != null) {
            user.setFirstName(request.firstName());
        }

        if (request.lastName() != null) {
            user.setLastName(request.lastName());
        }

        if (request.phone() != null) {
            user.setPhone(request.phone());
        }

        if (request.role() != null) {
            if (userRole.equals(UserRole.ADMIN.toString()) && !user.getUserRole().equals(UserRole.ADMIN)) {
                user.setUserRole(request.role());
            } else if (user.getUserRole().equals(UserRole.ADMIN)) {
                return new UpdateUserResponse(null, "user has ADMIN role, you can't change it");
            } else {
                return new UpdateUserResponse(null, "you don't have permission to change roles");
            }
        }

        userRepository.save(user);
        updateUserEventProducer.sendUpdateUserEvent(user);
        UserResponse userResponse = mapToResponse(user);

        return new UpdateUserResponse(userResponse, null);
    }

    public Optional<String> deleteUser(String token, UUID targetId) {
        String role = jwtService.parseToken(token).get("role", String.class);

        if (!role.equals(UserRole.ADMIN.toString())) {
            return Optional.of("you don't have permission to delete profile");
        }

        Optional<User> userResult = userRepository.findById(targetId);
        if (userResult.isEmpty()) {
            return Optional.empty();
        }


        userRepository.delete(userResult.get());
        deleteUserEventProducer.sendDeleteUserEvent(targetId);

        return Optional.empty();
    }

    private UserResponse mapToResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getPhone(),
                user.getUserRole(),
                user.getRegisteredAt()
        );
    }
}
