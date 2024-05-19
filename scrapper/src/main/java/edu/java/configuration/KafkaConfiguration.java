package edu.java.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfiguration {
    @Bean
    public NewTopic linkUpdateTopic(ApplicationConfig config) {
        return TopicBuilder
            .name(config.kafka().topics().get("link-update"))
            .build();
    }
}
