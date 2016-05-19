package at.ac.tuwien.sepm.ss16.qse18.gui.exam;

import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import at.ac.tuwien.sepm.ss16.qse18.domain.Topic;
import at.ac.tuwien.sepm.ss16.qse18.gui.GuiController;
import at.ac.tuwien.sepm.ss16.qse18.gui.MainFrameController;
import at.ac.tuwien.sepm.ss16.qse18.gui.observableEntity.ObservableSubject;
import at.ac.tuwien.sepm.ss16.qse18.gui.observableEntity.ObservableTopic;
import at.ac.tuwien.sepm.ss16.qse18.service.ExamService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.ss16.qse18.service.SubjectService;
import at.ac.tuwien.sepm.ss16.qse18.service.TopicService;
import at.ac.tuwien.sepm.util.AlertBuilder;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Zhang Haixiang, Bicer Cem
 */
@Component public class InsertExamValuesController implements GuiController {
    private Logger logger = LoggerFactory.getLogger(InsertExamValuesController.class);
    private Stage primaryStage;
    private AlertBuilder alertBuilder;
    private ObservableList<ObservableSubject> subjectList;
    private ObservableList<ObservableTopic> topicList;
    private ExamService examService;
    private SubjectService subjectService;
    private TopicService topicService;

    @Autowired MainFrameController mainFrameController;

    @FXML public Button buttonCreate;
    @FXML public Button buttonCancel;

    @FXML public Text topicText;

    @FXML public ListView<ObservableSubject> subjectListView;
    @FXML public ListView<ObservableTopic> topicListView;

    @FXML public TextField fieldAuthor;

    @Autowired
    public InsertExamValuesController(ExamService examService, SubjectService subjectService,
        TopicService topicService, AlertBuilder alertBuilder) {
        this.examService = examService;
        this.subjectService = subjectService;
        this.topicService = topicService;
        this.alertBuilder = alertBuilder;
    }

    @FXML public void initialize() {
        logger.debug("Filling subject list");

        try {
            List<ObservableSubject> observableSubjects =
                subjectService.getSubjects().stream().map(ObservableSubject::new)
                    .collect(Collectors.toList());
            subjectList = FXCollections.observableList(observableSubjects);
            subjectListView.setItems(subjectList);
            subjectListView.setCellFactory(lv -> new ListCell<ObservableSubject>() {
                @Override public void updateItem(ObservableSubject item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        String text =
                            item.getSubject().getSubjectId() + ": " + item.getName() + " (" + item
                                .getSemester() + ")";
                        setText(text);
                    }
                }
            });
            subjectListView.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    topicText.setOpacity(1);
                    topicListView.setDisable(false);
                    try {
                        List<ObservableTopic> observableTopics =
                            topicService.getTopicsFromSubject(newValue.getSubject().getSubjectId())
                                .stream().map(ObservableTopic::new).collect(Collectors.toList());

                        topicList = FXCollections.observableList(observableTopics);
                        topicListView.setItems(topicList);

                    } catch (ServiceException e) {
                        topicListView.setItems(null);
                    }
                });
            topicListView.setCellFactory(lv -> new ListCell<ObservableTopic>() {
                @Override public void updateItem(ObservableTopic item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        String text = item.getTopic().getTopicId() + ": " + item.getName();
                        setText(text);
                    }
                }
            });
        } catch (Exception e) {
            logger.error(e.toString());
        }
    }

    @Override public void setPrimaryStage(Stage stage) {
        this.primaryStage = stage;
    }

    @FXML public void create() {
        logger.debug("Create button pressed. Entering create method.");

        if (fieldAuthor.getText().isEmpty()) {
            logger.error("TextField \'author\' is empty");
            showalert(Alert.AlertType.WARNING, "Textfield author must not be empty");
        } else {
            Exam exam = new Exam();
            exam.setAuthor(fieldAuthor.getText());
            exam.setCreated(new Timestamp(new Date().getTime()));
            exam.setPassed(false);
            exam.setSubjectID(1);

            if (topicListView.getSelectionModel().getSelectedItem() == null) {
                logger.warn("No topic selected");
                showalert(Alert.AlertType.WARNING, "No topic selected.");
                return;
            }


            // TODO: Set questions for exam like
            /* "exam.setExamQuestions(TopicQuestionService
                                              .getQuestionsFromTopic(topicListView
                                                                              .getSelectionModel()
                                                                              .getSelectedItem()
                                                                              .getTopic()
                                                                              .getTopicId()))"
            */

            try {
                examService.createExam(exam,
                    topicListView.getSelectionModel().getSelectedItem().getTopic(), 1);
            } catch (ServiceException e) {
                e.printStackTrace();
                logger.error("Could not create exam: " + e.getMessage());
                showalert(Alert.AlertType.ERROR,
                    "Unexpected error. Could not create exam. For more information view logs.");
            }
        }
    }

    @FXML public void cancel() {
        mainFrameController.handleExams();
    }

    private void showalert(Alert.AlertType type, String contentMsg) {
        String header = "";

        if (type == Alert.AlertType.ERROR) {
            header = "Error";
        } else if (type == Alert.AlertType.WARNING) {
            header = "Warning";
        }

        Alert alert =
            alertBuilder.alertType(type).headerText(header).contentText(contentMsg).build();
        alert.showAndWait();
    }
}
