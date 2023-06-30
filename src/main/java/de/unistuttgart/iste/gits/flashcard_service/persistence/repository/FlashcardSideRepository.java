package de.unistuttgart.iste.gits.flashcard_service.persistence.repository;


import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSideEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

public interface FlashcardSideRepository extends JpaRepository<FlashcardSideEntity, UUID>,
        JpaSpecificationExecutor<FlashcardSideEntity> {
}
