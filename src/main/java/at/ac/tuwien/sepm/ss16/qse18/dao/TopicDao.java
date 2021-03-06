package at.ac.tuwien.sepm.ss16.qse18.dao;

import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;

import java.util.List;

/**
 * Interface TopicDao
 * Data Access Object interface for topic.
 * Retrieves, saves, updates and deletes topics from the persistency
 *
 * @author Philipp Ganiu
 */
public interface TopicDao {

    /**
     * Searches  for the topic with topicid {@param topicid} and returns it.
     *
     * @param topicid Id of the topic that is searched and returned
     * @return  If a topic with topicid {@param topicid} exists then this topic is returned, else
     * null is returned
     * @throws DaoException if there is no connection to the resource
     */
    Topic getTopic(int topicid) throws DaoException;

    /**
     * Returns every subject from the resource.
     *
     * @return A list containing every subject that was found in the resource
     * @throws DaoException if there is no connection to the resource
     */
    List<Topic> getTopics() throws DaoException;

    /**
     * Creates a topic with values from parameter {@param topic}.This method calls another method
     * that creates the relation of the topic to a subject it was created for.
     *
     * @param topic DTO that contains information for the topic that is created
     * @param subject subject to which the topic is created
     * @return the topic that is created with the resource generated topicid
     * @throws DaoException if there is no connection to the resource, or if a topic with the
     * topicid of {@param topic} already exists
     */
    Topic createTopic(Topic topic, Subject subject) throws DaoException;

    /**
     * Deletes an existing topic. The topic to be deleted is specified in {@param topic}.
     *
     * @param topic DTO that contains information for the topic to be delete
     * @return True if topic is delted, false if operation failed
     * @throws DaoException if there is no connection to the resource, or if a topic with the
     * topicid of {@param topic} does not exist
     */
    boolean deleteTopic(Topic topic) throws DaoException;

    /**
     * Updates an existing topic. The topic to be updated is specified in {@param topic}.
     *
     * @param topic DTO that contains information for the topic to be updated
     * @throws DaoException if there is no connection to the resource, or if a topic with the
     * topicid of {@param topic} does not exist
     */
    Topic updateTopic(Topic topic) throws DaoException;
}

