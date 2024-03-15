package edu.java.models;

import java.net.URL;
import java.time.OffsetDateTime;

public record LinkData(URL url, OffsetDateTime lastUpdated) {}
