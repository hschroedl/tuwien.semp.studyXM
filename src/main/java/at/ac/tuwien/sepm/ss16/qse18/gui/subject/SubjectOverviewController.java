package at.ac.tuwien.sepm.ss16.qse18.gui.subject;

import at.ac.tuwien.sepm.ss16.qse18.application.MainApplication;
import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import at.ac.tuwien.sepm.ss16.qse18.gui.BaseController;
import at.ac.tuwien.sepm.ss16.qse18.gui.navigation.SubjectNavigation;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableSubject;
import at.ac.tuwien.sepm.ss16.qse18.service.ImportService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.ss16.qse18.service.SubjectService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A controller for subjectView, to create, delete, and edit resourceListView.
 *
 * @author Hans-Joerg Schroedl
 */
@Component public class SubjectOverviewController extends BaseController {

    @FXML private ListView<ObservableSubject> subjectListView;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    private ObservableList<ObservableSubject> subjectList;
    private SubjectService subjectService;
    @Autowired private MainApplication mainApplication;
    @Autowired SubjectNavigation subjectNavigator;
    @Autowired private ImportService importService;

    @Autowired ApplicationContext applicationContext;

    @Autowired public SubjectOverviewController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @FXML public void initialize() {
        try {
            logger.debug("Initializing subject table");
            //Lambdas to create a new observable subject for each subject
            initializeButtons();
            initializeListView();
            subjectNavigator.refreshMainPane();
        } catch (ServiceException e) {
            logger.error(e);
            showError(e);
        }
    }

    /**
     * ActionHandler for creating a new subject
     *
     * @throws IOException
     */
    @FXML public void handleNew() throws IOException {
        logger.debug("Create new subject");
        subjectNavigator.handleCreateSubject(null);
    }

    @FXML public void handleImport() {
        logger.debug("Import button pressed");
        try {
            File selected = selectFile();
            if (selected != null) {
                importService.importSubject(selected);
                initializeListView();
                showSuccess("File was successfully imported");
            }
        } catch (ServiceException e) {
            logger.error(e);
            showError(e);
        }
    }

    /**
     * Action handler for deleting a subject
     */
    @FXML public void handleDelete() {
        try {
            logger.debug("Delete subject from table");
            if (!showConfirmation(
                "This will remove the subject and all associated questions, materials and exams.")) {
                return;
            }
            ObservableSubject subjectToDelete =
                subjectListView.getSelectionModel().getSelectedItem();
            if (subjectToDelete != null) {
                subjectService.deleteSubject(subjectToDelete.getSubject());
                subjectList.remove(subjectToDelete);
            }
        } catch (ServiceException e) {
            logger.error(e);
            showError(e);
        }
    }

    /**
     * Action handler for editing the selected subject
     *
     * @throws IOException
     */
    @FXML public void handleEdit() throws IOException {
        logger.debug("Editing selected subject");
        ObservableSubject subject = subjectListView.getSelectionModel().getSelectedItem();
        subjectNavigator.handleCreateSubject(subject);
    }

    /**
     * Creates a new subject and adds it to the subject list
     *
     * @param subject The subject to be added
     */
    void addSubject(ObservableSubject subject) {
        subjectList.add(subject);
    }

    /**
     * Updates a subject in the list and in the database
     *
     * @param observableSubject The current subject in the list
     * @param subject           The new subject, containing new values
     */
    void updateSubject(ObservableSubject observableSubject, Subject subject) {
        updateEntry(observableSubject, subject);
    }

    private void initializeListView() throws ServiceException {
        List<ObservableSubject> observableSubjects =
            subjectService.getSubjects().stream().map(ObservableSubject::new)
                .collect(Collectors.toList());
        subjectList = FXCollections.observableArrayList(observableSubjects);
        subjectListView.setItems(subjectList);
        subjectListView.setCellFactory(listView -> applicationContext.getBean(SubjectCell.class));
    }

    private void initializeButtons() {
        editButton.disableProperty()
            .bind(subjectListView.getSelectionModel().selectedItemProperty().isNull());
        deleteButton.disableProperty()
            .bind(subjectListView.getSelectionModel().selectedItemProperty().isNull());

    }

    private void updateEntry(ObservableSubject observableSubject, Subject subject) {
        observableSubject.setName(subject.getName());
        observableSubject.setEcts(subject.getEcts());
        observableSubject.setSemester(subject.getSemester());
        observableSubject.setAuthor(subject.getAuthor());
    }

    private File selectFile() throws ServiceException {
        FileChooser fileChooser = new FileChooser();

        fileChooser.getExtensionFilters()
            .addAll(new FileChooser.ExtensionFilter("XMS FILES (.xms)", "*.xms"));
        fileChooser.setTitle("Choose xms file");
        Stage mainStage = mainApplication.getPrimaryStage();
        File selected = fileChooser.showOpenDialog(mainStage);

        if (selected != null) {
            if (selected.getName()
                .substring(selected.getName().length() - 4, selected.getName().length())
                .equals(".xms")) {
                return selected;
            } else {
                logger.error("No valid file selected");
                throw new ServiceException("No valid file selected");
            }
        }
        return null;
    }

}
