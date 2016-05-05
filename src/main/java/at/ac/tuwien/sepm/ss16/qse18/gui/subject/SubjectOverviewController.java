package at.ac.tuwien.sepm.ss16.qse18.gui.subject;

import at.ac.tuwien.sepm.ss16.qse18.service.SubjectService;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * A controller for subjectView, to create, delete, and edit subjects.
 *
 * @author Hans-Joerg Schroedl
 */
@Component @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) public class SubjectOverviewController {


    @FXML public TableView<ObservableSubject> subjects;
    @FXML public TableColumn<ObservableSubject, String> nameColumn;
    private ObservableList<ObservableSubject> subjectList;
    private SpringFXMLLoader springFXMLLoader;
    private Stage primaryStage;
    private Logger logger = LoggerFactory.getLogger(SubjectOverviewController.class);
    private SubjectService subjectService;


    @Autowired public SubjectOverviewController(SpringFXMLLoader springFXMLLoader,
        SubjectService subjectService) {
        this.springFXMLLoader = springFXMLLoader;
        this.subjectService = subjectService;
    }

    @FXML public void initialize() {
        subjectList = FXCollections.observableArrayList(
            subjectService.getSubjects().stream().map(ObservableSubject::new)
                .collect(Collectors.toList()));
        subjects.setItems(subjectList);
        nameColumn
            .setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getName()));
    }

    @FXML public void handleNew() throws IOException {
        Stage stage = new Stage();
        SpringFXMLLoader.FXMLWrapper<Object, SubjectEditController> editSubjectWrapper =
            springFXMLLoader
                .loadAndWrap("/fxml/subject/subjectEdit.fxml", SubjectEditController.class);
        SubjectEditController childController = editSubjectWrapper.getController();
        childController.setSubject(null);
        childController.setStage(stage);
        configureStage(stage, "New Subject", editSubjectWrapper);
    }

    @FXML public void handleDelete() {
        ObservableSubject subjectToDelete = subjects.getSelectionModel().getSelectedItem();
        subjectService.deleteSubject(subjectToDelete.getSubject());
        subjectList.remove(subjectToDelete);
    }

    @FXML public void handleEdit() throws IOException {
        ObservableSubject subject = subjects.getSelectionModel().getSelectedItem();
        Stage stage = new Stage();
        SpringFXMLLoader.FXMLWrapper<Object, SubjectEditController> editSubjectWrapper =
            springFXMLLoader
                .loadAndWrap("/fxml/subject/subjectEdit.fxml", SubjectEditController.class);
        SubjectEditController childController = editSubjectWrapper.getController();
        childController.setSubject(subject);
        childController.setStage(stage);
        configureStage(stage, "Edit Subject", editSubjectWrapper);
    }

    private void configureStage(Stage stage, String title,
        SpringFXMLLoader.FXMLWrapper<Object, SubjectEditController> editSubjectWrapper)
        throws IOException {
        stage.setTitle(title);
        stage.setScene(new Scene((Parent) editSubjectWrapper.getLoadedObject(), 400, 400));
        stage.initModality(Modality.WINDOW_MODAL);
        stage.initOwner(this.primaryStage);
        stage.showAndWait();
    }



    public void addSubject(ObservableSubject subject) {
        subjectList.add(subject);
        subjectService.createSubject(subject.getSubject());
    }

    public void updatesubject(ObservableSubject subject) {
        subjectService.updateSubject(subject.getSubject());
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }
}
