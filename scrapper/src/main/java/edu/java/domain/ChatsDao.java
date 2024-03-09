package edu.java.domain;

import java.util.List;


public interface ChatsDao {
    void add(long id);

    void remove(long id);

    List<Long> findAll();
}
