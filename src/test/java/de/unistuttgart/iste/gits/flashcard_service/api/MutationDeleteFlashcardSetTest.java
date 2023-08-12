package de.unistuttgart.iste.gits.flashcard_service.api;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;

import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSetEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardRepository;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardSetRepository;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardSideRepository;
import de.unistuttgart.iste.gits.flashcard_service.test_utils.TestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;

@GraphQlApiTest
@TablesToDelete({"flashcard_side", "flashcard", "flashcard_set"})
public class MutationDeleteFlashcardSetTest {

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;
    @Autowired
    private FlashcardSideRepository flashcardSideRepository;
    @Autowired
    private FlashcardRepository flashcardRepository;

    @Autowired
    private TestUtils testUtils;

    @Test
    @Transactional
    @Commit
    void testDeleteFlashcardSet(GraphQlTester tester) {
        // put some data into the database
        List<FlashcardSetEntity> sets = testUtils.populateFlashcardSetRepository(flashcardSetRepository);

        UUID setToDelete = sets.get(0).getAssessmentId();

        String query = """
                mutation($assessmentId: UUID!) {
                    deleteFlashcardSet(input: $assessmentId)
                }
                """;

        tester.document(query)
                .variable("assessmentId", setToDelete)
                .execute()
                .path("deleteFlashcardSet")
                .entity(UUID.class)
                .isEqualTo(setToDelete);

        assertThat(flashcardSetRepository.count()).isEqualTo(1);
        assertThat(flashcardSetRepository.findById(setToDelete)).isEmpty();

        assertThat(flashcardRepository.count()).isEqualTo(2);
        assertThat(flashcardSideRepository.count()).isEqualTo(4);
    }
}
