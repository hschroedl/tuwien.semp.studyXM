package at.ac.tuwien.sepm.ss16.qse18.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import at.ac.tuwien.sepm.ss16.qse18.dao.DaoException;
import at.ac.tuwien.sepm.ss16.qse18.dao.SubjectTopicDao;
import at.ac.tuwien.sepm.ss16.qse18.dao.impl.ExamDaoJdbc;
import at.ac.tuwien.sepm.ss16.qse18.dao.impl.ExerciseExamDaoJdbc;
import at.ac.tuwien.sepm.ss16.qse18.dao.impl.QuestionTopicDaoJdbc;
import at.ac.tuwien.sepm.ss16.qse18.domain.ExerciseExam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;
import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.service.ExamService;
import at.ac.tuwien.sepm.ss16.qse18.service.ExerciseExamService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.ss16.qse18.service.StatisticService;
import at.ac.tuwien.sepm.ss16.qse18.service.SubjectService;


/**
 * Implementation of the statistics service layer
 *
 * @author Julian Strohmayer
 */

@Service
public class StatisticServiceImpl implements StatisticService {
    private static final Logger logger = LogManager.getLogger();
    private static final String HINT_FINE = "Hint - you are doing fine!";
    private static final String HINT_EXAM_RESULT = "Hint - your exercise exam results " +
            "are not that great, try to do some research first!";
    private static final String HINT_NO_EXAMS = "Hint - you should do more exercise exams!";
    private static final String HINT_TIME = "Hint - try to manage your time more efficient!";
    private static final String HINT_TOPIC = "Hint - create some topics for this subject!";

    private ExamService examService;
    private ExerciseExamService exerciseExamService;
    private ExamDaoJdbc examDaoJdbc;
    private ExerciseExamDaoJdbc exerciseExamDaoJdbc;
    private SubjectService subjectService;
    private SubjectTopicDao subjectTopicDao;
    private QuestionTopicDaoJdbc questionTopicDaoJdbc;
    private Map<Integer, Boolean> questions;
    private Topic topic;
    private static Map<Integer, Deque<String>> hints = new HashMap<>();


    @Autowired
    StatisticServiceImpl(ExamService examService,
                         ExerciseExamService exerciseExamService,
                         ExamDaoJdbc examDaoJdbc,
                         ExerciseExamDaoJdbc exerciseExamDaoJdbc,
                         QuestionTopicDaoJdbc questionTopicDaoJdbc,
                         SubjectService subjectService,
                         SubjectTopicDao subjectTopicDao) {
        this.examService = examService;
        this.exerciseExamService = exerciseExamService;
        this.examDaoJdbc = examDaoJdbc;
        this.exerciseExamDaoJdbc = exerciseExamDaoJdbc;
        this.questionTopicDaoJdbc = questionTopicDaoJdbc;
        this.subjectService = subjectService;
        this.subjectTopicDao = subjectTopicDao;
    }

    @Override
    public double[] getCorrectQuestionsForTopic(Topic topic) throws ServiceException {
        if (topic == null) {
            logger.error("topic is null");
            throw new ServiceException("topic must not be null");
        }

        logger.debug("calculating number of answered questions for ID:" + topic.getTopicId());
        this.topic = topic;
        try {
            getTopicQuestions();
            checkForCorrectQuestions();

        } catch (DaoException e) {
            logger.error("Unable to get questions for this topic ", e);
            throw new ServiceException(e);
        } catch (ServiceException e) {
            logger.error("Unable to get answered questions ", e);
            throw new ServiceException("Unable to get correct questions " + e);
        }

        int correct = 0;
        for (Map.Entry<Integer, Boolean> entry : questions.entrySet()) {
            if (entry.getValue()) {
                correct++;
            }
        }
        return questions.isEmpty() ? new double[]{0, 0, 0}
                : new double[]{correct / questions.size(), correct, questions.size()};
    }

    @Override
    public double[] gradeAllExamsForSubject(Subject subject) throws ServiceException {
        if (subject == null) {
            logger.error("subject is null");
            throw new ServiceException("subject must not be null");
        }
        logger.debug("calculating average exercise exam result for " + subject.getName());

        double[] result;
        try {
            if (subjectTopicDao.getTopicToSubject(subject).isEmpty()) {
                hints.get(subject.getSubjectId()).offerFirst(HINT_TOPIC);
                return new double[]{-1, 0};
            }
            result = calculateAvgGrade(subject);

        } catch (DaoException e) {
            logger.debug("Unable to calculate grade for exercise exams ", e);
            throw new ServiceException("Unable to calculate grade for exercise exams ", e);
        } catch (ServiceException e) {
            logger.debug("Unable to get all exercise exams for this subject", e);
            throw new ServiceException(e);
        }
        return result[1] == 0 ? new double[]{-1, 0} : new double[]{result[0], result[1]};
    }

    @Override
    public void initializeHints() throws ServiceException {
        for (Subject s : subjectService.getSubjects()) {
            int id = s.getSubjectId();
            hints.put(id, new LinkedList<>());
            if ((s.getEcts()) * 25 - (s.getTimeSpent() / 60) <= 0) {
                hints.get(id).offer(HINT_TIME);
            } else {
                hints.get(id).offer(HINT_FINE);
            }
        }
    }

    @Override
    public String getHint(Subject subject) {
        return hints.get(subject.getSubjectId()).peekFirst();
    }

    /**
     * This method calculates the the average exercise exam result for a given subject. The amount
     * of exercise exams is also counted.
     *
     * @param subject the subject
     * @return [0] average exam result [1] amount of exercise exams
     * @throws DaoException
     * @throws ServiceException
     */
    private double[] calculateAvgGrade(Subject subject) throws DaoException, ServiceException {
        int gradeSum = 0;
        int examCount = 0;
        for (Exam e : examDaoJdbc.getExams()) {
            if (e.getSubject() == subject.getSubjectId()) {
                for (Integer i : examService.getAllExerciseExamsOfExam(e)) {
                    examCount++;
                    ExerciseExam ex = exerciseExamDaoJdbc.getExam(i);
                    gradeSum += getIntValueOfGrade(exerciseExamService.gradeExam(ex)[2].charAt(0));
                }
            }
        }
        if (examCount <= 1) {
            hints.get(subject.getSubjectId()).offerFirst(HINT_NO_EXAMS);

        } else if (gradeSum / examCount >= 4) {
            hints.get(subject.getSubjectId()).offerFirst(HINT_EXAM_RESULT);
        }

        return new double[]{examCount != 0 ? gradeSum / examCount : 0, examCount};
    }

    /**
     * Fills the question Map with all questions in the topic
     * @throws DaoException
     */
    private void getTopicQuestions() throws DaoException {
        questions = new HashMap<>();
        for (Question q :questionTopicDaoJdbc.getQuestionToTopic(topic)) {
            questions.put(q.getQuestionId(), false);
        }
    }

    /**
     * Checks every question in the topic for correctness.
     * @throws DaoException
     * @throws ServiceException
     */
    private void checkForCorrectQuestions() throws DaoException, ServiceException {
        for (Exam e : examDaoJdbc.getExams()) {
            for (Integer i : examService.getAllExerciseExamsOfExam(e)) {

                ExerciseExam ex = exerciseExamDaoJdbc.getExam(i);
                for (Integer id : exerciseExamService.getAnsweredQuestionsOfExam(ex.getExamid())) {
                    if (questions.containsKey(id)) {
                        questions.put(id, true);
                    }
                }
            }
        }
    }

    /**
     * Returns the int value for a given grade
     *
     * @param c grade as character
     * @return 'A'=1,'B'=2,'C'=3,'D'=4,'F'=5
     */
    private int getIntValueOfGrade(char c) {
        switch (c) {
            case 'A':
                return 1;
            case 'B':
                return 2;
            case 'C':
                return 3;
            case 'D':
                return 4;
            default:
                return 5;
        }
    }
}