package edu.java.clients.dataHandling;

import java.util.Map;

public interface ClientDataHandler {
    String getMessageByDescriptionAndHost(Map<String, String> description);

    boolean isHostSupported(String host);
}
