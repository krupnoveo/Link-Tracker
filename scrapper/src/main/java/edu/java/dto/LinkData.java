package edu.java.dto;

import java.net.URL;
import java.time.OffsetDateTime;

public record LinkData(URL url, OffsetDateTime lastUpdated) {}
