package at.ac.tuwien.sepm.ss16.qse18.gui.exam.exercise;

import at.ac.tuwien.sepm.ss16.qse18.gui.BaseController;

import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableAnswer;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableQuestion;
import at.ac.tuwien.sepm.ss16.qse18.service.QuestionService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhang Haixiang on 15.06.2016.
 */
@Component public class PostExerciseExamItemController extends BaseController{
    @FXML private Node root;
    @FXML protected ListView<ObservableAnswer> answerListView;
    @FXML protected Label questionLabel;
    @FXML protected ImageView questionImageView;
    @FXML protected Button showResourceButton;

    @Autowired private QuestionService questionService;

    private ObservableQuestion question;
    private ObservableList<ObservableAnswer> answerList;

    private ScrollPane scrollPane = new ScrollPane();
    final DoubleProperty zoomProperty = new SimpleDoubleProperty(200);

    @FXML public void initialize(ObservableQuestion question){
        try {
            this.question = question;
            List<ObservableAnswer> observableAnswers = questionService.
                getCorrespondingAnswers(question.getQuestionInstance()).
                stream().map(ObservableAnswer::new).collect(Collectors.toList());

            answerList = FXCollections.observableList(observableAnswers);
            answerListView.setItems(answerList);


        }catch (ServiceException e){
            logger.error("Service Exception in PostExerciseExamItemController initialize with parameters", question);
            showError(e);
        }
    }

    public void loadFields() {
        questionLabel.setVisible(true);
        questionLabel.setText(question.getQuestion());

        if(question.getType() == 4) {
            questionLabel.setVisible(false);

            zoomProperty.addListener(new InvalidationListener() {
                @Override
                public void invalidated(Observable arg0) {
                    questionImageView.setFitWidth(zoomProperty.get() * 4);
                    questionImageView.setFitHeight(zoomProperty.get() * 3);
                }
            });

            FileChooser fileChooser = new FileChooser();
            fileChooser.setInitialDirectory(new File(question.getQuestion()));
            questionImageView.setImage(new Image(fileChooser.getInitialDirectory().toURI().toString()));
        }

    }

    public Node getRoot() {
        return root;
    }

}
