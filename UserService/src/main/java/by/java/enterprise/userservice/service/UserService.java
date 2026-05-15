package by.java.enterprise.userservice.service;

import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.request.LoginRequest;
import by.java.enterprise.userservice.dto.response.AuthResult;
import by.java.enterprise.userservice.dto.response.GetUserResponse;
import by.java.enterprise.userservice.dto.response.UserResponse;
import by.java.enterprise.userservice.entity.AuthStatus;
import by.java.enterprise.userservice.entity.User;
import by.java.enterprise.userservice.entity.UserRole;
import by.java.enterprise.userservice.kafka.RegisterUserEventProducer;
import by.java.enterprise.userservice.repository.UserRepository;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RegisterUserEventProducer registerUserEventProducer;
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

    public GetUserResponse findById(String token, UUID targetId) {
        Claims claims = jwtService.parseToken(token);
        UUID userId = UUID.fromString(claims.get("id", String.class));
        String role = claims.get("role", String.class);

        boolean access;
        if (role.equals(UserRole.ADMIN.toString()) || role.equals(UserRole.SUPPORT.toString())) {
            access = true;
        } else if (role.equals(UserRole.CUSTOMER.toString())) {
            access = userId.equals(targetId);
        } else {
            access = false;
        }

        if (!access) {
            return new GetUserResponse(null, "access denied");
        }

        Optional<User> userResult = userRepository.findById(targetId);

        return userResult
                .map(user -> new GetUserResponse(mapToResponse(user), null))
                .orElseGet(() -> new GetUserResponse(null, "user with id = {" + targetId + "} doesn't exists"));
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
