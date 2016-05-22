package at.ac.tuwien.sepm.ss16.qse18.gui.exam;

import at.ac.tuwien.sepm.ss16.qse18.domain.Question;
import at.ac.tuwien.sepm.ss16.qse18.gui.GuiController;
import at.ac.tuwien.sepm.util.AlertBuilder;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Controller of the showQuestion window in which the questions of the chosen exam are displayed
 * @author Zhang Haixiang
 */
@Component
public class ShowQuestionsController implements GuiController {
    private Logger logger = LoggerFactory.getLogger(ShowQuestionsController.class);
    private Stage primaryStage;
    private SpringFXMLLoader springFXMLLoader;
    private AlertBuilder alertBuilder;
    ObservableList<Question> questionObservableList;
    @Autowired CreateExamController createExamController;

    @FXML TableView<Question> tableQuestion;
    @FXML TableColumn<Question, Integer> columnQuestionID;
    @FXML TableColumn<Question, String> columnQuestion;
    @FXML TableColumn<Question, Integer> columnType;
    @FXML TableColumn<Question, Long> columnQuestionTime;


    @Override public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @Autowired public ShowQuestionsController(SpringFXMLLoader springFXMLLoader, AlertBuilder alertBuilder){
        this.springFXMLLoader = springFXMLLoader;
        this.alertBuilder = alertBuilder;
    }

    @FXML public void initialize(){
        questionObservableList = FXCollections.observableArrayList(this.createExamController.getQuestionList());
        columnQuestionID.setCellValueFactory(new PropertyValueFactory<>("questionId"));
        columnQuestion.setCellValueFactory(new PropertyValueFactory<>("question"));
        columnType.setCellValueFactory(new PropertyValueFactory<>("type"));
        columnQuestionTime.setCellValueFactory(new PropertyValueFactory<>("questionTime"));
        tableQuestion.setItems(questionObservableList);
    }
}