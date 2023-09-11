package de.unistuttgart.iste.gits.flashcard_service.api;

import de.unistuttgart.iste.gits.common.event.UserProgressLogEvent;
import de.unistuttgart.iste.gits.common.testutil.GraphQlApiTest;
import de.unistuttgart.iste.gits.common.testutil.TablesToDelete;
import de.unistuttgart.iste.gits.common.user_handling.LoggedInUser;
import de.unistuttgart.iste.gits.flashcard_service.dapr.TopicPublisher;
import de.unistuttgart.iste.gits.flashcard_service.persistence.dao.FlashcardSetEntity;
import de.unistuttgart.iste.gits.flashcard_service.persistence.repository.FlashcardSetRepository;
import de.unistuttgart.iste.gits.flashcard_service.test_config.MockTopicPublisherConfiguration;
import de.unistuttgart.iste.gits.flashcard_service.test_utils.TestUtils;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@GraphQlApiTest
@ContextConfiguration(classes = MockTopicPublisherConfiguration.class)
@TablesToDelete({"flashcard_progress_data_log", "flashcard_progress_data", "flashcard_side", "flashcard", "flashcard_set"})
class MutationLogFlashcardProgressTest {

    @Autowired
    private FlashcardSetRepository flashcardSetRepository;
    @Autowired
    private TopicPublisher topicPublisher;

    @Test
    @Transactional
    @Commit
    void testLogFlashcardProgress(HttpGraphQlTester graphQlTester) {
        List<FlashcardSetEntity> flashcardSet = new TestUtils().populateFlashcardSetRepository(flashcardSetRepository);


        UUID userId1 = UUID.randomUUID();
        FlashcardSetEntity flashcardSetEntity = flashcardSet.get(0);
        UUID flashcardSetId = flashcardSetEntity.getAssessmentId();
        UUID flashcardId1 = flashcardSetEntity.getFlashcards().get(0).getId();
        UUID flashcardId2 = flashcardSetEntity.getFlashcards().get(1).getId();


        String mutation = """
                mutation logFlashcardProgress($id: UUID!, $successful: Boolean!) {
                    logFlashcardLearned(
                       input: {
                        flashcardId: $id,
                        successful: $successful
                       }
                    ) {
                        id
                    }
                    
                }
                """;
        String currentUser = """
                {
                    "id": "%s",
                    "userName": "MyUserName",
                    "firstName": "John",
                    "lastName": "Doe"
                }
                """.formatted(userId1.toString());

        graphQlTester.mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(mutation)
                .variable("id", flashcardId1)
                .variable("successful", true)
                .execute()
                .path("logFlashcardLearned.id").entity(UUID.class).isEqualTo(flashcardId1);

        verify(topicPublisher, never()).notifyFlashcardSetLearned(any());
        flashcardSetEntity = flashcardSetRepository.findById(flashcardSetId).orElseThrow();
        assertThat(flashcardSetEntity.getLastLearned().isPresent(), is(false));

        graphQlTester.mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(mutation)
                .variable("id", flashcardId2)
                .variable("successful", false)
                .execute()
                .path("logFlashcardLearned.id").entity(UUID.class).isEqualTo(flashcardId2);

        UserProgressLogEvent expectedEvent = UserProgressLogEvent.builder()

                .contentId(flashcardSetId)
                .correctness(0.5)
                .success(true)
                .timeToComplete(null)
                .hintsUsed(0)
                .build();

        verify(topicPublisher).notifyFlashcardSetLearned(expectedEvent);
        flashcardSetEntity = flashcardSetRepository.findById(flashcardSetId).orElseThrow();
        assertThat(flashcardSetEntity.getLastLearned().isPresent(), is(true));

        // do another run of the same flashcard set, with 100% correctness
        reset(topicPublisher);
        expectedEvent.setCorrectness(1.0);

        graphQlTester.mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(mutation)
                .variable("id", flashcardId1)
                .variable("successful", true)
                .execute()
                .path("logFlashcardLearned.id").entity(UUID.class).isEqualTo(flashcardId1);

        verify(topicPublisher, never()).notifyFlashcardSetLearned(any());

        graphQlTester.mutate()
                .header("CurrentUser", currentUser)
                .build()
                .document(mutation)
                .variable("id", flashcardId2)
                .variable("successful", true)
                .execute()
                .path("logFlashcardLearned.id").entity(UUID.class).isEqualTo(flashcardId2);

        verify(topicPublisher).notifyFlashcardSetLearned(expectedEvent);
    }
}
