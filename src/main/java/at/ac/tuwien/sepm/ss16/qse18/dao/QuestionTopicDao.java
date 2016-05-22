package at.ac.tuwien.sepm.ss16.qse18.dao;

import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;

import java.util.List;

/**
 * @author Philipp Ganiu
 */
public interface QuestionTopicDao {
    /**
     * Returns all questions to a specific topic in a List from the Resource
     *
     * @param topic the topic for which all quesitons are returned
     * @return a List of all questions to the {@param topic}
     * @throws DaoException if there is no connection to the resource
     * */
    List<Question> getQuestionToTopic(Topic topic) throws DaoException;

    /**
     * Returns all topics to a specific question in a List from the resource
     *
     * @param question the question for which all topics are returned
     * @return a list of all topics to the {@param question}
     * @throws DaoException if there is no connection to the resource
     * */
    List<Topic> getTopicsFromQuestion(Question question) throws DaoException;

    void removeQuestionFromTopic(Topic topic) throws DaoException;
}