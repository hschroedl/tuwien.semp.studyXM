package at.ac.tuwien.sepm.ss16.qse18.gui.exam.exercise;

import at.ac.tuwien.sepm.ss16.qse18.domain.Answer;
import at.ac.tuwien.sepm.ss16.qse18.domain.ExerciseExam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import org.springframework.stereotype.Component;

/**
 * @author Philipp Ganiu
 */
@Component
public class AnswerOpenQuestionController extends AnswerQuestionController {
    @FXML private TextArea textArea;

    @Override public void initialize(ExerciseExam exam, Question question, Answer answer1, Answer answer2,
        Answer answer3, Answer answer4) {
        super.initialize(exam, question, answer1, answer2, answer3, answer4);
        String questionText = question.getQuestion().replaceAll("(.{120})", "$1\n");
        questionLabel.setText(questionText);
    }

    @Override public boolean isCorrect() {
        String text = textArea.getText().trim();
        return answerService.checkIfOpenAnswersAreCorrect(text,new Answer[]{answer1,answer2,answer3,answer4});
    }

    @Override public boolean noButtonSelected(){
        return textArea.getText().isEmpty();
    }
}
