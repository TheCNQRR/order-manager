package by.java.enterprise.userservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class DeleteUserEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${kafka.topics.user-deleted}")
    String topic;

    public void sendDeleteUserEvent(UUID id) {
        try {
            DeleteUserEvent event = new DeleteUserEvent(id);

            kafkaTemplate.send(topic, event).whenComplete((result, e) -> {
                if (e == null) {
                    log.info("deleted user event sent successfully: id={}", id);
                } else {
                    log.error("failed to send delete user event: id={}, error={}", id, e.getMessage());
                }
            });
        } catch (Exception e) {
            log.error("unexpected error while sending create user event: id={}, error={}", id, e.getMessage());
        }
    }
}
