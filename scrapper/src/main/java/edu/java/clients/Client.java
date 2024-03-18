package edu.java.clients;

import edu.java.models.LinkData;
import java.net.URL;
import java.time.OffsetDateTime;
import java.util.List;

public interface Client {


    List<LinkData> checkURL(URL url, OffsetDateTime lastUpdated);

    boolean isUrlSupported(URL url);
}
