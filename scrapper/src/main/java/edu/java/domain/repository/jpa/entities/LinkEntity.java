package edu.java.domain.repository.jpa.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "link")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class LinkEntity {
    @Id
    @Column(name = "id", columnDefinition = "BIGINT")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "url", unique = true)
    private String uri;

    @Column(name = "updated_at")
    private OffsetDateTime updatedAt;

    @Column(name = "checked_at")
    private OffsetDateTime checkedAt;

    @ManyToMany(mappedBy = "links", fetch = FetchType.LAZY)
    private Set<ChatEntity> chats = new HashSet<>();

    public LinkEntity(
        String uri,
        OffsetDateTime updatedAt,
        OffsetDateTime checkedAt
    ) {
        this.uri = uri;
        this.updatedAt = updatedAt;
        this.checkedAt = checkedAt;
    }
}
