package de.unistuttgart.iste.gits.flashcard_service.service;

import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardProgressDataEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardProgressDataLogEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashCardProgressDataLogRepository;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardProgressDataRepository;
import de.unistuttgart.iste.gits.generated.dto.Flashcard;
import de.unistuttgart.iste.gits.generated.dto.FlashcardProgressData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashcardUserProgressDataService {

    private final FlashcardProgressDataRepository flashcardProgressDataRepository;
    private final FlashCardProgressDataLogRepository flashCardProgressDataLogRepository;
    private final ModelMapper modelMapper;
    private final FlashcardService flashcardService;

    public FlashcardProgressData getProgressData(UUID flashcardId, UUID userId) {
        var entity = getProgressDataEntity(flashcardId, userId);
        return mapToDto(entity);
    }

    private FlashcardProgressDataEntity getProgressDataEntity(UUID flashcardId, UUID userId) {
        var primaryKey = new FlashcardProgressDataEntity.PrimaryKey(flashcardId, userId);
        return flashcardProgressDataRepository.findById(primaryKey)
                .orElseGet(() -> initializeProgressDate(flashcardId, userId));
    }

    private FlashcardProgressDataEntity initializeProgressDate(UUID flashcardId, UUID userId) {
        var primaryKey = new FlashcardProgressDataEntity.PrimaryKey(flashcardId, userId);
        var progressData = FlashcardProgressDataEntity.builder()
                .primaryKey(primaryKey)
                .build();
        return flashcardProgressDataRepository.save(progressData);
    }

    public Flashcard logFlashCardLearned(UUID flashcardId, UUID userId, boolean successful) {
        var flashcard = flashcardService.getFlashCardById(flashcardId);
        var progressData = getProgressDataEntity(flashcardId, userId);
        var logData = new FlashcardProgressDataLogEntity();
        logData.setSuccess(successful);

        updateProgressDataEntity(progressData, successful);
        flashCardProgressDataLogRepository.save(logData);

        return flashcard;

    }

    private void updateProgressDataEntity(FlashcardProgressDataEntity progressData, boolean success) {
        var lastLearn = OffsetDateTime.now();
        progressData.setLastLearned(lastLearn);
        var learningInterval = progressData.getLearningInterval();
        if (success) {
            learningInterval *= 2;
        } else {
            learningInterval /= 2;
            if (learningInterval < 1) {
                learningInterval = 1;
            }
        }
        progressData.setLearningInterval(learningInterval);
        progressData.setNextLearn(lastLearn.plusDays(learningInterval));

        flashcardProgressDataRepository.save(progressData);
    }


    private FlashcardProgressData mapToDto(FlashcardProgressDataEntity entity) {
        return modelMapper.map(entity, FlashcardProgressData.class);
    }
}
