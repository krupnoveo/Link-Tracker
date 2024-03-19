package edu.java.clients.dataHandling;

import java.util.Map;
import java.util.Properties;
import org.springframework.stereotype.Component;

@Component
public class StackOverflowClientDataHandler implements ClientDataHandler {
    private final String host;

    private final Properties properties;

    public StackOverflowClientDataHandler(Properties properties) {
        this.properties = properties;
        this.host = properties.getProperty("stackoverflow.host");
    }

    @Override
    public String getMessageByDescription(Map<String, String> description) {
        return properties.getProperty("stackoverflow.updateMessage");
    }

    @Override
    public boolean isHostSupported(String host) {
        return host.equals(this.host);
    }
}
