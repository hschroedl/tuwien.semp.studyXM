package at.ac.tuwien.sepm.ss16.qse18.gui.question;

import at.ac.tuwien.sepm.ss16.qse18.domain.Answer;
import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.ss16.qse18.domain.QuestionType;
import at.ac.tuwien.sepm.ss16.qse18.gui.GuiController;
import at.ac.tuwien.sepm.ss16.qse18.gui.MainFrameController;
import at.ac.tuwien.sepm.ss16.qse18.service.AnswerService;
import at.ac.tuwien.sepm.ss16.qse18.service.QuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.util.AlertBuilder;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.fxml.FXML;

import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * A controller for the creation of image-questions.
 *
 * @author Julian on 14.05.2016.
 */
@Component @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CreateImageQuestionController implements GuiController{

    @FXML public Button buttonCreateQuestion;
    @FXML public Button buttonAddImage;

    @FXML public ImageView imageViewQuestionImage;
    @FXML public ImageView imageViewIcon;

    @FXML public TextField textFieldAnswerOne;
    @FXML public TextField textFieldAnswerTwo;
    @FXML public TextField textFieldAnswerThree;
    @FXML public TextField textFieldAnswerFour;
    @FXML public TextField textFieldImagePath;

    @FXML public CheckBox checkBoxAnswerOne;
    @FXML public CheckBox checkBoxAnswerTwo;
    @FXML public CheckBox checkBoxAnswerThree;
    @FXML public CheckBox checkBoxAnswerFour;
    @Autowired MainFrameController mainFrameController;

    private Logger logger = LoggerFactory.getLogger(CreateImageQuestionController.class);
    private AlertBuilder alertBuilder;
    private SpringFXMLLoader springFXMLLoader;
    private Stage primaryStage;

    private QuestionService questionService;
    private AnswerService answerService;

    @Autowired
    public CreateImageQuestionController(SpringFXMLLoader springFXMLLoader,
                                         QuestionService questionService,
                                         AnswerService answerService, AlertBuilder alertBuilder) {
        this.springFXMLLoader = springFXMLLoader;
        this.questionService = questionService;
        this.answerService = answerService;
        this.alertBuilder = alertBuilder;
    }

    /**
     * Creates a new question from the users input and stores it in the database, if it doesn't already exist.
     * @throws IOException
     */
    @FXML
    public void handleCreateQuestion() throws IOException {
        logger.debug("Creating new Question");

        if(textFieldImagePath.getText().isEmpty()){
            logger.debug("No image was selected");
            new Alert(Alert.AlertType.WARNING,
                    "Please select an image located in resources/images/. ", ButtonType.OK).showAndWait();
            return;
        }
        if(textFieldAnswerOne.getText().isEmpty() &&
                textFieldAnswerTwo.getText().isEmpty() &&
                textFieldAnswerThree.getText().isEmpty() &&
                textFieldAnswerFour.getText().isEmpty()){
            logger.debug("No answer was given to this question");
            new Alert(Alert.AlertType.WARNING,
                    "You should at least give one answer to this question.", ButtonType.OK).showAndWait();
            return;
        }

        try {
            Question newQuestion = newQuestionFromFields();
            questionService.createQuestion(newQuestion);

            List<Answer> answerList = newAnswersFromFields();
            for(Answer a: answerList) {
                a.setQuestion(newQuestion);
                answerService.createAnswer(a);
            }
            questionService.setCorrespondingAnswers(newQuestion,answerList);
        } catch (ServiceException e) {
            showAlert(e);
            return;
        }
        showSuccess("Inserted new question into database.");
        mainFrameController.handleHome();
    }

    /**
     * Returns a question generated by the user input
     * @return generated question
     */
    private Question newQuestionFromFields()
    {
        // Question_Time is set to 1 for now because this parameter was added later and is not in the Mockup
        return new Question(textFieldImagePath.getText().toString(),QuestionType.NOTECARD,1);
    }

    /**
     * Returns a list of answers generated by the user input.
     * List size can be [0,4] depending on the modified text fields.
     * @return list of answers
     */
    private List<Answer> newAnswersFromFields(){
        List<Answer> answers = new LinkedList<>();

        if(!textFieldAnswerOne.getText().isEmpty())
        {
            answers.add(new Answer(
                    QuestionType.NOTECARD,textFieldAnswerOne.getText(),checkBoxAnswerOne.isSelected()));
        }
        if(!textFieldAnswerTwo.getText().isEmpty())
        {
            answers.add(new Answer(
                    QuestionType.NOTECARD,textFieldAnswerTwo.getText(),checkBoxAnswerTwo.isSelected()));
        }
        if(!textFieldAnswerThree.getText().isEmpty())
        {
            answers.add(new Answer(
                    QuestionType.NOTECARD,textFieldAnswerThree.getText(),checkBoxAnswerThree.isSelected()));
        }
        if(!textFieldAnswerFour.getText().isEmpty())
        {
            answers.add(new Answer(
                    QuestionType.NOTECARD,textFieldAnswerFour.getText(),checkBoxAnswerFour.isSelected()));
        }
        return answers;
    }

    /**
     * Handles the event of the "add image" button
     * @throws IOException
     */
    @FXML public void handleAddImage() throws IOException {
        logger.debug("Adding new image.");
        selectImage();
    }

    /**
     * Loads a new image into the imageViewQuestionImage and displays the path in the textFieldImagePath.
     * @throws IOException
     */
    public void selectImage() throws IOException{

        FileChooser fileChooser = new FileChooser();
        File defaultDirectory = new File("src/main/resources/images"); // Review: could this cause problems?
        fileChooser.setInitialDirectory(defaultDirectory);

        fileChooser.setTitle("Add image");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Image Files","*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            Image img = new Image(selectedFile.toURI().toString());
            imageViewQuestionImage.setImage(img);
            textFieldImagePath.setText(selectedFile.toURI().toString());
        }
    }

    public void setPrimaryStage(Stage primaryStage) {

        this.primaryStage = primaryStage;
    }

    private void showAlert(ServiceException e) {
        Alert alert = alertBuilder
                .alertType(Alert.AlertType.ERROR)
                .title("Error")
                .headerText("An error occured")
                .contentText(e.getMessage())
                .build();
        alert.showAndWait();
    }

    private void showSuccess(String msg) {
        Alert alert = alertBuilder
                .alertType(Alert.AlertType.INFORMATION)
                .title("Success")
                .headerText("The operation was successful!")
                .contentText(msg)
                .build();
        alert.showAndWait();
    }
}


