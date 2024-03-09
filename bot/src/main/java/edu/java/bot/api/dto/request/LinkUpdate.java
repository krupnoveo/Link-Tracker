package edu.java.bot.api.dto.request;

import java.net.URI;
import java.util.List;

public record LinkUpdate(long urlId, URI url, String description, List<Long> chatIds) {
}
