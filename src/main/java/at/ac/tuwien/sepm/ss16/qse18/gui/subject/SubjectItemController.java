package at.ac.tuwien.sepm.ss16.qse18.gui.subject;

import at.ac.tuwien.sepm.ss16.qse18.gui.observableEntity.ObservableSubject;
import at.ac.tuwien.sepm.ss16.qse18.gui.observableEntity.ObservableTopic;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;

/**
 * @author Hans-Joerg Schroedl
 */
public class SubjectItemController {

    @FXML public Node root;
    @FXML Label name;
    @FXML public ListView<ObservableTopic> topicListView;

    private ObservableSubject subject;

    public SubjectItemController(ObservableSubject subject) {
        this.subject = subject;
        loadGui();
        loadFields();
    }

    private void loadGui() {
        FXMLLoader fxmlLoader =
            new FXMLLoader(getClass().getResource("/fxml/subject/subjectItem.fxml"));
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadFields() {
        name.setText(subject.getName());
    }


    @FXML public void handleExport() {
        System.out.println("Wow, much export!");
    }

    @FXML public void handleNew() {

    }

    @FXML public void handleDelete(){

    }

    public Node getRoot() {
        return root;
    }

}
