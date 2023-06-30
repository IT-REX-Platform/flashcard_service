package de.unistuttgart.iste.gits.flashcard_service.api.mutation;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardRepository;
import de.unistuttgart.iste.gits.generated.dto.Flashcard;
import de.unistuttgart.iste.gits.generated.dto.FlashcardSet;
import de.unistuttgart.iste.gits.generated.dto.FlashcardSide;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.annotation.Commit;
import java.util.UUID;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.is;
import java.util.List;
@GraphQlApiTest
@TablesToDelete({"flashcard_side", "flashcard", "flashcard_set"})
class MutationCreateFlashcardSetTest {
    @Autowired
    private FlashcardRepository flashcardRepository;
    @Test
    @Transactional
    @Commit
    void testCreateFlashcardSet(GraphQlTester graphQlTester) {
        UUID assessmentId = UUID.randomUUID();
        String query = """
                
            mutation ($assessmentId: UUID!){
              createFlashcardSet(input: {
                 assessmentId: $assessmentId
                 flashcards: [
                              {
                                sides: [
                                  {
                                    label: "Side 1",
                                    isQuestion: true,
                                    text: "Question 1"
                                  },
                                  {
                                    label: "Side 2",
                                    isQuestion: false,
                                    text: "Answer 1"
                                  }
                                ]
                              },
                              {
                                sides: [
                                  {
                                    label: "Side 1",
                                    isQuestion: true,
                                    text: "Question 2"
                                  },
                                  {
                                    label: "Side 2",
                                    isQuestion: false,
                                    text: "Answer 2"
                                  }
                                ]
                              }
                 
                 ]
              }) 
              {
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

        FlashcardSet createdFlashcardSet = graphQlTester.document(query)
                .variable("assessmentId", assessmentId)
                .execute()
                .path("createFlashcardSet").entity(FlashcardSet.class).get();

        // check that returned Flashcard is correct
        assertThat(createdFlashcardSet.getAssessmentId(), is(notNullValue()));

        // Get the list of flashcards from the createdFlashcardSet
        List<Flashcard> flashcards = createdFlashcardSet.getFlashcards();

        // Assert that the flashcards list is not null and has at least one flashcard
        assertThat(flashcards, is(notNullValue()));
        assertThat(flashcards.size(), is(greaterThan(0)));

        // Iterate over each flashcard and assert its properties
        for (Flashcard flashcard : flashcards) {
            // Assert that the flashcard's sides list is not null and has at least one side
            List<FlashcardSide> sides = flashcard.getSides();
            assertThat(sides, is(notNullValue()));
            assertThat(sides.size(), is(greaterThan(0)));

            // Iterate over each side and assert its properties
            for (FlashcardSide side : sides) {
                assertThat(side.getLabel(), is(notNullValue()));
                assertThat(side.getIsQuestion(), is(notNullValue()));
                assertThat(side.getText(), is(notNullValue()));
            }
        }
    }
}
