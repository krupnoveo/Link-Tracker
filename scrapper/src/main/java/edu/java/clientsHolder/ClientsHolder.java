package edu.java.clientsHolder;

import edu.java.api.exceptions.InvalidUrlFormatException;
import edu.java.api.exceptions.UnsupportedUrlException;
import edu.java.clients.BaseClient;
import edu.java.models.LinkData;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;
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

    public LinkData checkURl(URI uri) {
        try {
            URL url = uri.toURL();
            for (BaseClient client : clientList) {
                if (client.isUrlSupported(url)) {
                    return client.checkURL(url);
                }
            }
            throw new UnsupportedUrlException(uri.toString());
        } catch (MalformedURLException e) {
            throw new InvalidUrlFormatException(uri.toString());
        }
    }
}
