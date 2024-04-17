package edu.java.models;

import java.net.URI;
import java.time.OffsetDateTime;

public record LinkDatabaseInformation(long urlId, URI url, OffsetDateTime lastUpdated) {
}
