package at.ac.tuwien.sepm.ss16.qse18.service.merge;

import at.ac.tuwien.sepm.ss16.qse18.dao.QuestionTopicDao;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepm.ss16.qse18.DummyEntityFactory.createDummyQuestion;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Hans-Joerg Schroedl
 */
@RunWith(MockitoJUnitRunner.class) public class QuestionConflictDetectionTest {

    private QuestionConflictDetection questionConflictDetection;

    @Mock private QuestionTopicDao questionTopicDaoMock;

    @Before public void setUp(){
        questionConflictDetection = new QuestionConflictDetection();
        questionConflictDetection.setQuestionTopicDao(questionTopicDaoMock);
    }

    @Test public void test_getConflictingQuestions_noConflictingQuestions() throws Exception {
        List<Question> exstingQuestions = new ArrayList<>();
        exstingQuestions.add(createDummyQuestion());

        List<QuestionConflict> questionConflicts = questionConflictDetection.getConflictingQuestions();

        assertTrue(questionConflicts.isEmpty());
    }

    @Test public void test_getConflictingQuestions_conflictingQuestionsReturned() throws Exception {
        Question duplicateQuestion = createDummyQuestion();
        Question otherExistingQuestion = createDummyQuestion();
        otherExistingQuestion.setQuestion("SomethingDifferent");
        List<Question> existingQuestions = new ArrayList<>();
        existingQuestions.add(duplicateQuestion);
        existingQuestions.add(otherExistingQuestion);
        when(questionTopicDaoMock.getQuestionToTopic(any())).thenReturn(existingQuestions);

        List<Question> importedQuestions = new ArrayList<>();
        importedQuestions.add(duplicateQuestion);


        questionConflictDetection.initialize(null, importedQuestions);
        List<QuestionConflict> questionConflicts = questionConflictDetection.getConflictingQuestions();

        assertFalse(questionConflicts.isEmpty());
    }

}
