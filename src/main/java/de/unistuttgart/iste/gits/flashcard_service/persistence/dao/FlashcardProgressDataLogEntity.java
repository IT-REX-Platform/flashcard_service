package de.unistuttgart.iste.gits.flashcard_service.persistence.dao;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity(name = "FlashcardProgressDataLog")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardProgressDataLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private boolean success;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumns({
            @JoinColumn(name = "flashcard_id", referencedColumnName = "flashcardID", nullable = false),
            @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false)
    })
    private FlashcardProgressDataEntity flashcardProgressData;



}
