package edu.java.domain.repository.jpa;

import edu.java.domain.repository.jpa.entities.LinkEntity;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaLinksRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUri(String uri);

    List<LinkEntity> getAllByCheckedAtBeforeOrderByCheckedAtDesc(OffsetDateTime criteria);
}
