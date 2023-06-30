package de.unistuttgart.iste.gits.flashcard_service.api.mutation.query;

import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSetEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSideEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardSetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

@Component
@GraphQlApiTest
public class QueryByAssessmentIdTest {


    @Autowired
    private FlashcardSetRepository flashcardSetRepository;

    @Test
    void testFlashcardSetsByAssessmentIds(GraphQlTester graphQlTester) {
        // Create test data
        UUID setId1 = UUID.randomUUID();
        UUID setId2 = UUID.randomUUID();
        UUID setId3 = UUID.randomUUID();

        UUID assessmentId1 = UUID.randomUUID();
        UUID assessmentId2 = UUID.randomUUID();

        FlashcardSetEntity set1 = createFlashcardSet(assessmentId1);
        FlashcardSetEntity set2 = createFlashcardSet(assessmentId2);
        FlashcardSetEntity set3 = createFlashcardSet(assessmentId1);

        FlashcardEntity flashcard1 = createFlashcard(setId1);
        FlashcardEntity flashcard2 = createFlashcard(setId2);
        FlashcardEntity flashcard3 = createFlashcard(setId3);

        FlashcardSideEntity side1 = createFlashcardSide(flashcard1, "Side 1", true, "Question 1");
        FlashcardSideEntity side2 = createFlashcardSide(flashcard1, "Side 2", false, "Answer 1");
        FlashcardSideEntity side3 = createFlashcardSide(flashcard2, "Side 1", true, "Question 2");
        FlashcardSideEntity side4 = createFlashcardSide(flashcard2, "Side 2", false, "Answer 2");
        FlashcardSideEntity side5 = createFlashcardSide(flashcard3, "Side 1", true, "Question 3");
        FlashcardSideEntity side6 = createFlashcardSide(flashcard3, "Side 2", false, "Answer 3");

        set1.setFlashcards(Arrays.asList(flashcard1));
        set2.setFlashcards(Arrays.asList(flashcard2));
        set3.setFlashcards(Arrays.asList(flashcard3));

        flashcard1.setSides(Arrays.asList(side1, side2));
        flashcard2.setSides(Arrays.asList(side3, side4));
        flashcard3.setSides(Arrays.asList(side5, side6));

        flashcardSetRepository.saveAll(Arrays.asList(set1, set2, set3));

        // Prepare input assessment IDs
        List<UUID> assessmentIds = Arrays.asList(assessmentId1, assessmentId2);

        // Prepare expected result
        List<FlashcardSetEntity> expectedResult = Arrays.asList(set1, set2, set3);

        // Execute the query
        String query = """
            query($assessmentIds: [UUID!]!) {
              flashcardSetsByAssessmentIds(assessmentIds: $assessmentIds) {
                assessmentId
                flashcards {
                  sides {
                    label
                    isQuestion
                    text
                  }
                }
              }
            }
        """;

        List<FlashcardSetEntity> actualResult = graphQlTester.document(query)
                .variable("assessmentIds", assessmentIds)
                .execute()
                .path("flashcardSetsByAssessmentIds")
                .entityList(FlashcardSetEntity.class)
                .get();

        // Assert the result
        assertThat(actualResult, containsInAnyOrder(expectedResult.toArray()));
    }

    private FlashcardSetEntity createFlashcardSet(UUID assessmentId) {
        FlashcardSetEntity set = new FlashcardSetEntity();

        set.setAssessmentId(assessmentId);
        return set;
    }

    private FlashcardEntity createFlashcard(UUID setId) {
        FlashcardEntity flashcard = new FlashcardEntity();
        flashcard.setId(UUID.randomUUID());
        flashcard.setSetId(setId);

        return flashcard;
    }

    private FlashcardSideEntity createFlashcardSide(FlashcardEntity flashcard, String label, boolean isQuestion, String text) {
        FlashcardSideEntity side = new FlashcardSideEntity();
        side.setId(UUID.randomUUID());
        side.setFlashcard(flashcard);
        side.setLabel(label);
        side.setQuestion(isQuestion);
        side.setText(text);
        return side;
    }
}
