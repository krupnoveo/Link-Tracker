package edu.java.domain.repository.jpa.entities;

import edu.java.api.exceptions.LinkAlreadyTrackedException;
import edu.java.api.exceptions.LinkNotFoundException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "chat")
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ChatEntity {
    @Id
    @Column(name = "chat_id")
    private Long id;

    public ChatEntity(
        Long id
    ) {
        this.id = id;
    }

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
        name = "chat_to_link",
        joinColumns = {
            @JoinColumn(name = "chat_id")
        },
        inverseJoinColumns = {
            @JoinColumn(name = "link_id")
        }
    )
    private Set<LinkEntity> links = new HashSet<>();

    public void deleteLink(LinkEntity link) {
        if (!links.remove(link)) {
            throw new LinkNotFoundException(link.getUri());
        }
        link.getChats().remove(this);
    }

    public void addLink(LinkEntity link) {
        if (!links.add(link)) {
            throw new LinkAlreadyTrackedException(link.getUri());
        }
        link.getChats().add(this);
    }
}
