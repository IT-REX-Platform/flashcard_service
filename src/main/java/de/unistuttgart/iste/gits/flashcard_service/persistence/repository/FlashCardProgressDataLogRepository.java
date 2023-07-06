package de.unistuttgart.iste.gits.flashcard_service.persistence.repository;

import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardProgressDataLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FlashCardProgressDataLogRepository extends JpaRepository<FlashcardProgressDataLogEntity, UUID> {

    List<FlashcardProgressDataLogEntity> findDistinctByFlashcardProgressDataOrderByLearnedAtDesc();
}
