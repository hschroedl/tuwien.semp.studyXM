package at.ac.tuwien.sepm.ss16.qse18.gui.exam;

import at.ac.tuwien.sepm.ss16.qse18.domain.Exam;
import at.ac.tuwien.sepm.ss16.qse18.gui.BaseController;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableSubject;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableTopic;
import at.ac.tuwien.sepm.ss16.qse18.service.*;
import at.ac.tuwien.sepm.util.AlertBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller of the create exam window in which in which new exams can be saved into the database
 *
 * @author Zhang Haixiang, Bicer Cem
 */
@Component public class InsertExamValuesController extends BaseController{
    private ObservableList<ObservableSubject> subjectList;
    private ObservableList<ObservableTopic> topicList;
    private ExamService examService;
    private SubjectService subjectService;
    private TopicService topicService;
    private QuestionService questionService;


    @FXML public Button buttonCreate;
    @FXML public Button buttonCancel;

    @FXML public Label topicText;

    @FXML public ListView<ObservableSubject> subjectListView;
    @FXML public ListView<ObservableTopic> topicListView;

    @FXML public TextField fieldAuthor;
    @FXML public TextField fieldTime;

    @Autowired
    public InsertExamValuesController(ExamService examService, SubjectService subjectService,
        TopicService topicService, QuestionService questionService) {
        this.examService = examService;
        this.subjectService = subjectService;
        this.topicService = topicService;
        this.questionService = questionService;
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
                            topicService.getTopicsFromSubject(newValue.getSubject()).stream()
                                .map(ObservableTopic::new).collect(Collectors.toList());

                        topicList = FXCollections.observableList(observableTopics);
                        topicListView.setItems(topicList);

                    } catch (ServiceException e) {
                        logger.error("Could not get observable topics", e);
                        topicListView.setItems(null);
                    }
                });
            topicListView.setCellFactory(lv -> new ListCell<ObservableTopic>() {
                @Override public void updateItem(ObservableTopic item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setText(null);
                    } else {
                        String text = item.getT().getTopicId() + ": " + item.getTopic();
                        setText(text);
                    }
                }
            });
        } catch (ServiceException e) {
            logger.error("Initialize not successful", e);
            showError(e);
        }
    }

    @FXML public void create() {
        logger.debug("Create button pressed. Entering create method.");

        if (fieldAuthor.getText().isEmpty()) {
            logger.error("TextField \'author\' is empty");
            showError("No author given. Textfield author must not be empty.");
        } else if (fieldTime.getText().isEmpty() || !fieldTime.getText().matches("\\d*")) {
            logger.error("No valid time has been given");
            showError("No valid time has been given. Make sure to fill the Time textfield with only whole numbers.");
        } else if (topicListView.getSelectionModel().getSelectedItem() == null) {
            logger.warn("No topic selected");
            showError("No topic selected. You have to select the topic you want to create an exam to.");
        } else {
            Exam exam = new Exam();
            exam.setAuthor(fieldAuthor.getText());
            exam.setCreated(new Timestamp(new Date().getTime()));
            exam.setPassed(false);

            int examTime = 0;

            exam.setSubjectID(
                subjectListView.getSelectionModel().getSelectedItem().getSubject().getSubjectId());

            try {
                examTime = Integer.parseInt(fieldTime.getText());

                exam.setExamQuestions(questionService.getQuestionsFromTopic(
                    topicListView.getSelectionModel().getSelectedItem().getT()));

                examService
                    .createExam(exam, topicListView.getSelectionModel().getSelectedItem().getT(),
                        examTime);
                showSuccess("Exam was created");
                mainFrameController.handleExams();
            } catch (ServiceException e) {
                logger.error("Could not create exam: ", e);
                showError("Check if the choosen topic has already questions to answer."
                        + "\nCheck if the length of the author do not exceed 80 characters."
                        + "\nCheck if there are enough questions in this topic to cover the exam time.");
            } catch (NumberFormatException e) {
                logger.error("Could not create exam: ", e);
                showError("Could not parse exam time. " +
                    "Make sure it only contains numbers and is lower than " + Integer.MAX_VALUE
                        + ".");
            }
        }
    }


    @FXML public void cancel() {
        mainFrameController.handleExams();
    }

}
