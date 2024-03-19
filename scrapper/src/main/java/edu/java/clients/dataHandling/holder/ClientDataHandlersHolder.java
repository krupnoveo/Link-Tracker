package edu.java.clients.dataHandling.holder;

import edu.java.clients.dataHandling.ClientDataHandler;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = "edu.java.clients.dataHandling")
public class ClientDataHandlersHolder {
    private final List<ClientDataHandler> dataHandlerList;
    private final Properties properties;

    @Autowired
    public ClientDataHandlersHolder(List<ClientDataHandler> dataHandlerList, Properties properties) {
        this.dataHandlerList = dataHandlerList;
        this.properties = properties;
    }

    public String getMessageByDescriptionAndHost(String host, Map<String, String> description) {
        for (ClientDataHandler dataHandler : dataHandlerList) {
            if (dataHandler.isHostSupported(host)) {
                return dataHandler.getMessageByDescription(description);
            }
        }
        return properties.getProperty("default.updateMessage");
    }
}
