package de.unistuttgart.iste.gits.flashcard_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSetEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardRepository;
import de.unistuttgart.iste.gits.flashcard_service.service.FlashcardService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


@GraphQlApiTest
@TablesToDelete({"flashcard_side", "flashcard", "flashcard_set"})
public class MutationDeleteFlashcardTest {

    @Autowired
    private FlashcardService flashcardService;

    @Autowired
    private FlashcardRepository flashcardRepository;

    @Test
    @Transactional
    @Commit
    void testDeleteFlashcard(GraphQlTester graphQlTester) {
        // Create and save the flashcard to be deleted
        FlashcardSetEntity set = new FlashcardSetEntity();
        set.setAssessmentId(UUID.randomUUID());

        set.setFlashcards(List.of(
                new FlashcardEntity(),
                new FlashcardEntity(),
                new FlashcardEntity()
        ));

        flashcard.setSetId(UUID.randomUUID());

        flashcard= flashcardRepository.save(flashcard);

        UUID flashcardId = flashcard.getId();

        // Perform the delete operation
        String query = """
          mutation ($assessmentId: UUID!, $flashcardId: UUID!) {
            mutateFlashcardSet(id: $assessmentId) {
              deleteFlashcard(id: $flashcardId)
            }
          }
        """;

        // Execute the delete mutation query
        UUID deletedFlashcardId = graphQlTester.document(query)
                .variable("input", flashcardId)
                .execute()
                .path("deleteFlashcard")
                .entity(UUID.class)
                .get();

        // Assert that the flashcard ID matches the deleted ID
        assertThat(deletedFlashcardId, is(flashcardId));


        // Verify that the flashcard no longer exists in the repository
        Optional<FlashcardEntity> deletedFlashcard = flashcardRepository.findById(flashcardId);
        assertThat(deletedFlashcard.isPresent(), is(false));
    }
}
