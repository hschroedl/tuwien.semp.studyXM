package at.ac.tuwien.sepm.ss16.qse18.gui.question;

import at.ac.tuwien.sepm.ss16.qse18.domain.Answer;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.ss16.qse18.domain.QuestionType;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableResource;
import at.ac.tuwien.sepm.ss16.qse18.service.QuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ResourceQuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.util.AlertBuilder;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;

/**
 * Controller for managing creation of single choice questions
 * <p>
 * Created by Felix on 19.05.2016.
 */
@Component @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CreateSingleChoiceQuestionController extends QuestionController {
    @FXML private TextArea textAreaQuestion;
    @FXML private RadioButton radioButtonAnswerOne;
    @FXML private RadioButton radioButtonAnswerTwo;
    @FXML private RadioButton radioButtonAnswerThree;
    @FXML private RadioButton radioButtonAnswerFour;

    /**
     * Creates a controller for the single choice question creation.
     *
     * @param questionService The question service which saves a given question and answers
     *                        persistently.
     * @param alertBuilder    An alert builder which wraps pop ups for user interaction.
     */
    @Autowired public CreateSingleChoiceQuestionController(QuestionService questionService,
        ResourceQuestionService resourceQuestionService, AlertBuilder alertBuilder,
        SpringFXMLLoader fxmlLoader) {
        super(questionService, resourceQuestionService, alertBuilder, fxmlLoader);

    }

    @Override protected void fillFieldsAndCheckboxes() {
        this.textAreaQuestion.setText(inputs == null ? "" : (String) inputs.get(0));

        fillAnswerFields(1);

        this.radioButtonAnswerOne.setSelected(inputs != null && (boolean) inputs.get(5));
        this.radioButtonAnswerTwo.setSelected(inputs != null && (boolean) inputs.get(6));
        this.radioButtonAnswerThree.setSelected(inputs != null && (boolean) inputs.get(7));
        this.radioButtonAnswerFour.setSelected(inputs != null && (boolean) inputs.get(8));

        this.checkBoxContinue.setSelected(inputs == null || (boolean) inputs.get(9));

        this.resource = (inputs == null ? null : (ObservableResource) inputs.get(10));
        this.resourceLabel.setText(resource == null ? "none" : resource.getName());
    }

    @Override protected void saveQuestionInput(List inputs) {
        if (textAreaQuestion != null) {
            inputs.add(textAreaQuestion.getText());
        } else {
            inputs.add(null);
        }
    }

    @Override protected void saveCheckboxesAndRadiobuttons(List inputs) {
        inputs.add(radioButtonAnswerOne.isSelected());
        inputs.add(radioButtonAnswerTwo.isSelected());
        inputs.add(radioButtonAnswerThree.isSelected());
        inputs.add(radioButtonAnswerFour.isSelected());

        inputs.add(checkBoxContinue.isSelected());
    }

    @Override protected QuestionType getQuestionType() {
        return QuestionType.SINGLECHOICE;
    }

    @FXML public void handleCreateQuestion() {
        if (createQuestion()) {
            return;
        }
        if (checkBoxContinue.isSelected()) {
            mainFrameController.handleSingleChoiceQuestion(this.topic);
        } else {
            mainFrameController.handleSubjects();
        }
        showSuccess("Inserted new question into database.");
    }

    private boolean createQuestion() {
        logger.info("Now creating new question");
        Question newQuestion;
        try {
            List<Answer> answers = newAnswersFromField();
            newQuestion = questionService.createQuestion(newQuestionFromField(), topic.getT());
            questionService.setCorrespondingAnswers(newQuestion, answers);

            if (resource != null) {
                resourceQuestionService.createReference(resource.getResource(), newQuestion);
            }
        } catch (ServiceException e) {
            logger.error("Could not create new question", e);
            showAlert(e);
            return true;
        } catch (IllegalArgumentException e) {
            showAlert(e);
            return true;
        }
        return false;
    }

    private Question newQuestionFromField() throws ServiceException {
        logger.info("Collecting question from field.");
        if (textAreaQuestion.getText().isEmpty()) {
            throw new IllegalArgumentException("The question must not be empty.");
        }
        return new Question(textAreaQuestion.getText(), QuestionType.SINGLECHOICE, 1L);
    }

    private List<Answer> newAnswersFromField() throws ServiceException {
        logger.debug("Collecting all answers");
        List<Answer> newAnswers = new LinkedList<>();

        if (!textFieldAnswerOne.getText().isEmpty()) {
            newAnswers.add(new Answer(QuestionType.SINGLECHOICE, textFieldAnswerOne.getText(),
                radioButtonAnswerOne.isSelected()));
        }
        if (!textFieldAnswerTwo.getText().isEmpty()) {
            newAnswers.add(new Answer(QuestionType.SINGLECHOICE, textFieldAnswerTwo.getText(),
                radioButtonAnswerTwo.isSelected()));
        }
        if (!textFieldAnswerThree.getText().isEmpty()) {
            newAnswers.add(new Answer(QuestionType.SINGLECHOICE, textFieldAnswerThree.getText(),
                radioButtonAnswerThree.isSelected()));
        }
        if (!textFieldAnswerFour.getText().isEmpty()) {
            newAnswers.add(new Answer(QuestionType.SINGLECHOICE, textFieldAnswerFour.getText(),
                radioButtonAnswerFour.isSelected()));
        }

        if (newAnswers.isEmpty()) {
            throw new IllegalArgumentException("At least one answer must be given.");
        }

        for (Answer a : newAnswers) {
            if (a.isCorrect()) {
                return newAnswers;
            }
        }

        throw new IllegalArgumentException("At least one given answer must be true.");
    }
}
