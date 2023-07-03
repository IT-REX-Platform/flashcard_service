package de.unistuttgart.iste.gits.flashcard_service.persistence.dao;

import de.unistuttgart.iste.gits.generated.dto.FlashcardProgressLogItem;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity(name = "FlashcardProgressData")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardProgressDataEntity {

    @EmbeddedId
    private PrimaryKey primaryKey;

    @Column
    private FlashcardProgressLogItem log;

    @Column
    private int learningInterval;

    @Column
    private OffsetDateTime nextLearn;

    @Data
    @Embeddable
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrimaryKey implements Serializable {
        private UUID flashcardID;
        private UUID userId;

    }
}
