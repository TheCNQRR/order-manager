package by.java.enterprise.userservice.service;

import by.java.enterprise.userservice.dto.request.CreateUserRequest;
import by.java.enterprise.userservice.dto.response.UserResponse;
import by.java.enterprise.userservice.entity.User;
import by.java.enterprise.userservice.kafka.RegisterUserEventProducer;
import by.java.enterprise.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RegisterUserEventProducer registerUserEventProducer;

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
