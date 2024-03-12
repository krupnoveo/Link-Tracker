package edu.java.domain;

import edu.java.models.Chat;
import java.util.List;


public interface ChatsDao {
    void add(long id);

    void remove(long id);

    List<Chat> findAll();
}
