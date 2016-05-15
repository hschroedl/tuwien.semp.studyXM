package at.ac.tuwien.sepm.ss16.qse18.domain;

import java.sql.Timestamp;
import java.util.ArrayList;

/**
 * @author Zhang Haixiang
 */
public class Exam {
    private int examid;
    private Timestamp created;
    private boolean passed;
    private String author;
    private int subjectID;
    private ArrayList<Question> examQuestions = new ArrayList<Question>();

    public int getExamid(){
        return this.examid;
    }

    public void setExamid(int examid){
        this.examid = examid;
    }

    public Timestamp getCreated(){
        return this.created;
    }

    public int getSubjectID() {
        return subjectID;
    }

    public void setSubjectID(int subjectID) {
        this.subjectID = subjectID;
    }

    public void setCreated(Timestamp created){
        this.created = created;
    }

    public boolean getPassed(){
        return this.passed;
    }

    public void setPassed(boolean passed){
        this.passed = passed;
    }

    public String getAuthor(){
        return this.author;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public ArrayList<Question> getExamQuestions() {
        return examQuestions;
    }

    public void setExamQuestions(ArrayList<Question> examQuestions) {
        this.examQuestions = examQuestions;
    }
}
