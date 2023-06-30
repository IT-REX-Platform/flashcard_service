package de.unistuttgart.iste.gits.flashcard_service.api.mutation;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;

import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSetEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardRepository;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardSetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@GraphQlApiTest
@TablesToDelete({"flashcard_side", "flashcard", "flashcard_set"})
public class MutationDeleteFlashcardSetTest {
    @Autowired
    private FlashcardRepository flashcardRepository;
    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Test
    @Transactional
    @Commit
    void testDeleteFlashcard(GraphQlTester graphQlTester) {
        // Create and save the flashcard to be deleted
        FlashcardSetEntity flashcardSet = new FlashcardSetEntity();
        flashcardSet.setAssessmentId(UUID.randomUUID());


        flashcardSet= flashcardSetRepository.save(flashcardSet);

        UUID flashcardSetId = flashcardSet.getAssessmentId();

        // Perform the delete operation
        String query = """
          mutation ($input: UUID!) {
            deleteFlashcardSet(input: $input)
          }
        """;

        // Execute the delete mutation query
        UUID deletedFlashcardSetId = graphQlTester.document(query)
                .variable("input", flashcardSetId)
                .execute()
                .path("deleteFlashcardSet")
                .entity(UUID.class)
                .get();

        // Assert that the flashcard ID matches the deleted ID
        assertThat(deletedFlashcardSetId, is(flashcardSetId));


        // Verify that the flashcard no longer exists in the repository
        Optional<FlashcardSetEntity> deletedFlashcardSet = flashcardSetRepository.findById(flashcardSetId);
        assertThat(deletedFlashcardSet.isPresent(), is(false));
    }
}
