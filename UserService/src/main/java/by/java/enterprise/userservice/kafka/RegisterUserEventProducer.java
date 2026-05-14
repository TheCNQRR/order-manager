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

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-created}")
    private String topic;

    public void sendCreateUserEvent(User user) {
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
                            log.info("create user event sent successfully: id={}, email={}, firstName={}, lastName={}, phone={}, role={}, registeredAt={}",
                                    user.getId(),
                                    user.getEmail(),
                                    user.getFirstName(),
                                    user.getLastName(),
                                    user.getPhone(),
                                    user.getUserRole(),
                                    user.getRegisteredAt());
                        } else {
                            log.error("failed to send create user event: id={}, error={}", user.getId(), e.getMessage());
                        }
                    });
        } catch (Exception e) {
            log.error("unexpected error while sending create user event: id={}, error={}", user.getId(), e.getMessage());
        }
    }
}
