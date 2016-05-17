package at.ac.tuwien.sepm.ss16.qse18.dao;

import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;

import java.util.List;
import java.util.Map;

/**
 * @author Zhang Haixiang
 */
public interface ExamQuestionDao {
    void create(Exam exam, Question question) throws DaoException;
    void delete(Exam exam) throws DaoException;

    List<Boolean> getAllQuestionBooleans(List<Integer> questionList) throws DaoException;
    List<Integer> getAllQuestionID(int examID) throws DaoException;

}
