package at.ac.tuwien.sepm.ss16.qse18.service.merge;

import at.ac.tuwien.sepm.ss16.qse18.dao.QuestionDao;
import at.ac.tuwien.sepm.ss16.qse18.domain.Answer;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static at.ac.tuwien.sepm.ss16.qse18.DummyEntityFactory.createDummyAnswer;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

/**
 * @author Hans-Joerg Schroedl
 */
@RunWith(MockitoJUnitRunner.class) public class AnswerConflictDetectionTest {


    private AnswerConflictDetection answerConflictDetection;

    @Mock private QuestionDao questionDaoMock;

    @Before public void setUp() {
        answerConflictDetection = new AnswerConflictDetection();
        answerConflictDetection.setQuestionDao(questionDaoMock);
    }

    @Test public void test_areAnswersEqual_equalAnswersReturnsTrue() throws Exception {
        List<Answer> existingAnswers = new ArrayList<>();
        existingAnswers.add(createDummyAnswer());
        when(questionDaoMock.getRelatedAnswers(any())).thenReturn(existingAnswers);

        List<Answer> importedAnswers = existingAnswers;

        answerConflictDetection.initialize(null, importedAnswers);
        boolean result = answerConflictDetection.areAnswersEqual();

        assertTrue(result);
    }

    @Test public void test_areAnswersEqual_notSameNumberOfAnswers() throws Exception {
        List<Answer> existingAnswers = new ArrayList<>();
        existingAnswers.add(createDummyAnswer());
        when(questionDaoMock.getRelatedAnswers(any())).thenReturn(existingAnswers);

        List<Answer> importedAnswers = new ArrayList<>();
        importedAnswers.add(createDummyAnswer());
        importedAnswers.add(createDummyAnswer());

        answerConflictDetection.initialize(null, importedAnswers);
        boolean result = answerConflictDetection.areAnswersEqual();

        assertFalse(result);
    }

    @Test public void test_areAnswersEqual_differentText() throws Exception {
        List<Answer> existingAnswers = new ArrayList<>();
        existingAnswers.add(createDummyAnswer());
        when(questionDaoMock.getRelatedAnswers(any())).thenReturn(existingAnswers);

        List<Answer> importedAnswers = new ArrayList<>();
        Answer importedAnswer = createDummyAnswer();
        importedAnswer.setAnswer("Something else");
        importedAnswers.add(importedAnswer);

        answerConflictDetection.initialize(null, importedAnswers);
        boolean result = answerConflictDetection.areAnswersEqual();

        assertFalse(result);
    }


}
