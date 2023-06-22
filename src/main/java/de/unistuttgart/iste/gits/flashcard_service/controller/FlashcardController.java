package de.unistuttgart.iste.gits.flashcard_service.controller;

import de.unistuttgart.iste.gits.flashcard_service.service.FlashcardService;
import de.unistuttgart.iste.gits.generated.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;
import java.util.List;
import java.util.UUID;


@Slf4j
@Controller
public class FlashcardController {

    private final FlashcardService flashcardService;


    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }

    @QueryMapping
    public List<Flashcard> flashcardsByIds(@Argument(name = "ids") List<UUID> ids) {
        return flashcardService.getFlashcardsById(ids);
    }

    @QueryMapping
    public List<FlashcardSet> flashcardSetsByAssessmentIds(@Argument(name = "assessmentIds") List<UUID> ids) {
        return flashcardService.getFlashcardSetsByAssessmentId(ids);
    }

    @MutationMapping
    public Flashcard createFlashcard(@Argument(name = "input") CreateFlashcardInput input) {
        return flashcardService.createFlashcard(input);
    }

    @MutationMapping
    public Flashcard updateFlashcard(@Argument(name = "input") UpdateFlashcardInput input) {
        return flashcardService.updateFlashcard(input);
    }

    @MutationMapping
    public UUID deleteFlashcard(@Argument(name = "input") UUID id) {
        return flashcardService.deleteFlashcard(id);
    }

    @MutationMapping
    public FlashcardSet createFlashcardSet(@Argument(name = "input") CreateFlashcardSetInput input) {
        return flashcardService.createFlashcardSet(input);
    }

    @MutationMapping
    public UUID deleteFlashcardSet(@Argument(name = "input") UUID id) {
        return flashcardService.deleteFlashcardSet(id);
    }


}