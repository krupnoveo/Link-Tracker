package edu.java.domain.repository;

import edu.java.domain.LinksDao;
import java.net.URI;
import java.util.List;
import javafx.util.Pair;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class LinksRepository implements LinksDao {
    @Override
    @Transactional
    public long add(URI uri) {
        return 0;
    }

    @Override
    @Transactional
    public URI remove(long id) {
        return null;
    }

    @Override
    @Transactional
    public List<Pair<Long, URI>> findAll() {
        return null;
    }
}
