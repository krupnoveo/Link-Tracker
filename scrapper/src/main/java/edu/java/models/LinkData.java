package edu.java.models;

import java.net.URL;
import java.time.OffsetDateTime;
import java.util.Map;

public record LinkData(URL url, OffsetDateTime lastUpdated, String host, Map<String, String> description) {}
