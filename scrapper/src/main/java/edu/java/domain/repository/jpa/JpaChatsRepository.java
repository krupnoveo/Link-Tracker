package edu.java.domain.repository.jpa;

import edu.java.domain.repository.jpa.entities.ChatEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaChatsRepository extends JpaRepository<ChatEntity, Long> {}
