package de.unistuttgart.iste.gits.flashcard_service.service;

import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardProgressDataEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardProgressDataRepository;
import de.unistuttgart.iste.gits.generated.dto.Flashcard;
import de.unistuttgart.iste.gits.generated.dto.FlashcardProgressData;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FlashcardUserProgressDataService {

    private final FlashcardProgressDataRepository flashcardProgressDataRepository;
    private final ModelMapper modelMapper;
    private final FlashcardService flashcardService;

    public FlashcardProgressData getProgressData(UUID flashcardId, UUID userId) {
        var entity =  getProgressDataEntity(flashcardId, userId);
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

   public Flashcard logFlashCardWorkedOn(UUID flashcardId, UUID userId) {
        var flashcard = flashcardService.getFlashCardById(flashcardId);
        var progressData = getProgressDataEntity(flashcardId, userId);

        return flashcard;

    }



    private FlashcardProgressData mapToDto(FlashcardProgressDataEntity entity) {
        return modelMapper.map(entity, FlashcardProgressData.class);
    }
}
