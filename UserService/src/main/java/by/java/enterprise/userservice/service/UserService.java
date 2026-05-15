package by.java.enterprise.userservice.service;

import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.request.LoginRequest;
import by.java.enterprise.userservice.dto.response.AuthResult;
import by.java.enterprise.userservice.dto.response.UserResponse;
import by.java.enterprise.userservice.entity.AuthStatus;
import by.java.enterprise.userservice.entity.User;
import by.java.enterprise.userservice.kafka.RegisterUserEventProducer;
import by.java.enterprise.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
