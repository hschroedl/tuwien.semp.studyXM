package at.ac.tuwien.sepm.ss16.qse18.dao.impl;

import at.ac.tuwien.sepm.ss16.qse18.dao.ConnectionH2;
import at.ac.tuwien.sepm.ss16.qse18.dao.DaoException;
import at.ac.tuwien.sepm.ss16.qse18.dao.ExamQuestionDao;
import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.util.DTOValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Zhang Haixiang
 */
public class ExamQuestionDaoJdbc implements ExamQuestionDao {
    private ConnectionH2 database;
    private static final Logger logger = LogManager.getLogger();

    @Autowired public ExamQuestionDaoJdbc(ConnectionH2 database) {
        this.database = database;
    }

    @Override public void create(Exam exam, Question question) throws DaoException {
        logger.debug("entering method create with parameters {}", exam, question);

        if (!DTOValidator.validate(exam) || !DTOValidator.validate(question)) {
            logger.error("Dao Exception create {}", exam);
            throw new DaoException("Invalid values, please check your input");
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = this.database.getConnection().prepareStatement("Insert into rel_exam_question values(?, ?, ?, ?)");
            pstmt.setInt(1, exam.getExamid());
            pstmt.setInt(2, question.getQuestionid());
            pstmt.setBoolean(3, false);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("SQL Exception in create with parameters {}", exam, question, e);
            throw new DaoException(
                "Could not create ExamQuestion with values(" + exam.getExamid() + ", " + question.getQuestionid() + ")");

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                logger.error("SQL Exception in create with parameters {}", exam, question, e);
                throw new DaoException("Prepared Statement could not be closed");
            }
        }
    }

    @Override public void delete(Exam exam, Question question) throws DaoException {
        logger.debug("entering method delete with parameters {}", exam, question);

        if (!DTOValidator.validate(exam) || !DTOValidator.validate(question)) {
            logger.error("Dao Exception delete() {}", exam);
            throw new DaoException("Invalid values, please check your input");
        }

        PreparedStatement pstmt = null;

        try {
            pstmt = this.database.getConnection().prepareStatement("Delete from rel_exam_question "
                + "where examid = ? and questionid = ? and question_passed = ? and already_answered = ?");

            pstmt.setInt(1, exam.getExamid());
            pstmt.setInt(2, question.getQuestionid());
            pstmt.setBoolean(3, false);
            pstmt.setBoolean(4, false);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            logger.error("SQL Exception in delete with parameters {}", exam, question, e);
            throw new DaoException(
                "Could not delete ExamQuestion with values(" + exam.getExamid() + ", " + question.getQuestionid() + ")");

        } finally {
            try {
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                logger.error("SQL Exception in delete with parameters {}", exam, question, e);
                throw new DaoException("Prepared Statement could not be closed");
            }
        }
    }

    @Override public Map<Boolean, Boolean> getAllQuestionBooleans(int examID) throws DaoException{
        logger.debug("entering method getALlQuestionBooleans with parameters {}", examID);
        HashMap<Boolean, Boolean> questionMap = new HashMap<>(); // key: question_passed, value: already_answered

        if(examID <= 0){
            throw new DaoException("Ungueltige Exam ID, please check your input");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = this.database.getConnection().prepareStatement("Select * from rel_exam_question where examid = ?");

            pstmt.setInt(1, examID);
            rs = pstmt.executeQuery();

            while(rs.next()){
               questionMap.put(rs.getBoolean("question_passed"), rs.getBoolean("already_answered"));
            }


        }catch (SQLException e){
            logger.error("SQL Exception in delete with parameters {}", examID);
            throw new DaoException("Could not get List with all Question Booleans for Exam ID" + examID);
        }finally {
            try {
                if (rs!= null) {
                    rs.close();
                }
            } catch (SQLException e) {
                logger.error("SQL Exception in getAllQuestionBooleans with parameters {}", examID, e);
                throw new DaoException("Result Set could not be closed");
            }

            try{
                if(pstmt != null){
                    pstmt.close();
                }
            }catch (SQLException e){
                logger.error("SQL Excepiton in getAllQuestionBooleans with parameters {}", examID, e);
                throw new DaoException("Prepared Statement could not be closed");
            }
        }
        return questionMap;
    }

    @Override public List<Integer> getAllQuestionID(int examID) throws DaoException {
        logger.debug("entering method getALlQuestionID with parameters {}", examID);
        ArrayList<Integer> questionList = new ArrayList<>();

        if(examID <= 0){
            throw new DaoException("Invalid Exam ID, please check your input");
        }

        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            pstmt = this.database.getConnection().prepareStatement("Select * from rel_exam_question where examid = ?");

            pstmt.setInt(1, examID);
            rs = pstmt.executeQuery();

            while(rs.next()){
                questionList.add(rs.getInt("questionID"));
            }


        }catch (SQLException e){
            logger.error("SQL Exception in delete with parameters {}", examID);
            throw new DaoException("Could not get List with all Question ID's for Exam ID" + examID);
        }finally {
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                logger.error("SQL Exception in getAllQuestionID with parameters {}", examID);
                throw new DaoException("Result Set could not be closed");
            }

            try{
                if(pstmt != null){
                    pstmt.close();
                }
            }catch (SQLException e){
                logger.error("SQL Excepiton in getAllQuestionID with parameters {}", examID, e);
                throw new DaoException("Prepared Statement could not be closed");
            }
        }
        return questionList;
    }


}
