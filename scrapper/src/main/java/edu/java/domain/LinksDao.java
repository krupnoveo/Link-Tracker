package edu.java.domain;

import java.net.URI;
import java.util.List;
import javafx.util.Pair;

public interface LinksDao {
    long add(URI uri);

    URI remove(long id);

    List<Pair<Long, URI>> findAll();
}
