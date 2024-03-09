package edu.java.domain.repository;

import edu.java.domain.ChatsDao;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public class ChatRepository implements ChatsDao {
    private final JdbcClient client;

    @Autowired
    public ChatRepository(JdbcClient client) {
        this.client = client;
    }

    @Override
    @Transactional
    public void add(long id) {

    }

    @Override
    @Transactional
    public void remove(long id) {

    }

    @Override
    @Transactional
    public List<Long> findAll() {
        return null;
    }
}
