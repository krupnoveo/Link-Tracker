package edu.java.clientsHolder;

import edu.java.clients.BaseClient;
import edu.java.models.LinkData;
import java.net.URI;
import java.net.URL;
import java.util.List;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@Component
@ComponentScan(basePackages = "edu.java.clients")
public class ClientsHolder {
    private final List<BaseClient> clientList;

    @Autowired
    public ClientsHolder(List<BaseClient> clientList) {
        this.clientList = clientList;
    }

    @SneakyThrows
    public LinkData checkURl(String stringUrl) {
        URL url = new URI(stringUrl).toURL();
        for (BaseClient client : clientList) {
            if (client.isUrlSupported(url)) {
                return client.checkURL(url);
            }
        }
        return null;
    }
}
