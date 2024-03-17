package edu.java.configuration;

import java.util.Properties;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeansConfiguration {
    @Bean
    @SneakyThrows
    public Properties properties() {
        Properties properties = new Properties();
        properties.load(getClass().getResourceAsStream("/messages.properties"));
        return properties;
    }
}
