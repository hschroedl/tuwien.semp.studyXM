package at.ac.tuwien.sepm.ss16.qse18.service.merge;

import at.ac.tuwien.sepm.ss16.qse18.dao.DaoException;
import at.ac.tuwien.sepm.ss16.qse18.dao.SubjectTopicDao;
import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Hans-Joerg Schroedl
 */
@Service public class TopicConflictDetection {

    private static final Logger logger = LogManager.getLogger();
    private Subject subject;
    private List<Topic> importedTopics;

    private SubjectTopicDao subjectTopicDao;

    public void initialize(Subject subject, List<Topic> importedTopics) {
        this.subject = subject;
        this.importedTopics = importedTopics;
    }

    @Autowired public void setSubjectTopicDao(SubjectTopicDao subjectTopicDao) {
        this.subjectTopicDao = subjectTopicDao;
    }

    public List<Duplicate<Topic>> getConflictingTopics() throws ServiceException {
        List<Topic> existingTopics = getExistingTopics();
        List<Duplicate<Topic>> duplicates = new ArrayList<>();
        for (Topic existingTopic : existingTopics)
            for (Topic importedTopic : importedTopics) {
                String existingTopicName = existingTopic.getTopic();
                String importedTopicName = importedTopic.getTopic();
                if (existingTopicName.equals(importedTopicName)) {
                    Duplicate<Topic> duplicate = new Duplicate<>(existingTopic, importedTopic);
                    duplicates.add(duplicate);
                    break;
                }
            }
        return duplicates;
    }

    private List<Topic> getExistingTopics() throws ServiceException {
        try {
            return subjectTopicDao.getTopicToSubject(subject);
        } catch (DaoException e) {
            logger.error(e);
            throw new ServiceException("Could not read topics for subject.");
        }
    }

}
