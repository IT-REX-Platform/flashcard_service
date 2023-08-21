package de.unistuttgart.iste.gits.flashcard_service.persistence.dao;

import de.unistuttgart.iste.gits.common.resource_markdown.ResourceMarkdownEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity(name = "FlashcardSide")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardSideEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Embedded
    private ResourceMarkdownEntity text;

    @Column(nullable = false, length = 255)
    private String label;

    @Column(nullable = false)
    private boolean isQuestion;

    @Column
    private boolean isAnswer;

    @ManyToOne
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private FlashcardEntity flashcard;
}
