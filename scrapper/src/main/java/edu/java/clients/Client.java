package edu.java.clients;

import edu.java.models.LinkData;

import java.net.URL;

public interface Client {

    LinkData checkURL(URL url);

    boolean isUrlSupported(URL url);
}
