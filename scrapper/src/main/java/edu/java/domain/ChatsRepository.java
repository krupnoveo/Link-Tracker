package edu.java.domain;

import edu.java.models.Chat;
import java.util.List;


public interface ChatsRepository {
    void add(long id);

    void remove(long id);

    List<Chat> findAll();
}
