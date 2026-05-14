package by.java.enterprise.userservice.kafka;

import by.java.enterprise.userservice.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RegisterUserEventProducer {

    private final KafkaTemplate<String, UserRegisterEvent> kafkaTemplate;

    @Value("${kafka.topics.register-user}")
    private String topic;

    public void registerUserEvent(User user) {
        try {
            UserRegisterEvent event = new UserRegisterEvent(
                    user.getId(),
                    user.getEmail(),
                    user.getFirstName(),
                    user.getLastName(),
                    user.getPhone(),
                    user.getUserRole(),
                    user.getRegisteredAt()
            );

            kafkaTemplate.send(topic, event)
                    .whenComplete((result, e) -> {
                        if (e == null) {
                            log.info("register user event sent successfully: id={}, email={}, firstName={}, lastName={}, phone={}, role={}, registeredAt={}",
                                    user.getId(),
                                    user.getEmail(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getPhone(),
                                    user.getUserRole(),
                                    user.getRegisteredAt());
                        } else {
                            log.error("failed to send register user event: id={}, error={}", user.getId(), e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("Unexpected error while sending register user event: id={}, error={}", user.getId(), e.getMessage());
        }
    }
}
