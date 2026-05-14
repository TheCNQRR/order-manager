package by.java.enterprise.userservice.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JacksonJsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("${kafka.topics.register-user}")
    private String userCreatedTopic;

    @Value("${kafka.topics.update-user}")
    private String userUpdatedTopic;

    @Value("${kafka.topics.delete-user}")
    private String userDeletedTopic;

    @Bean
    public NewTopic userRegisteredTopic() {
        return new NewTopic(userCreatedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic userUpdatedTopic() {
        return new NewTopic(userUpdatedTopic, 1, (short) 1);
    }

    @Bean
    public NewTopic userDeletedTopic() {
        return new NewTopic(userDeletedTopic, 1, (short) 1);
    }

    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> config = new HashMap<>();

        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JacksonJsonSerializer.class);

        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
