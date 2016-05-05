package at.ac.tuwien.sepm.ss16.qse18.gui.subject;

import at.ac.tuwien.sepm.ss16.qse18.domain.Subject;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON) public class SubjectEditController {

    @FXML TextField name;
    @FXML TextField semester;
    @FXML TextField ects;
    @FXML TextField author;
    private Stage dialogStage;
    private ObservableSubject subject;
    private boolean isNew;
    private SubjectOverviewController overviewController;

    @Autowired public SubjectEditController(SubjectOverviewController controller) {
        this.overviewController = controller;
    }

    public void setStage(Stage stage) {
        this.dialogStage = stage;
    }

    public void setSubject(ObservableSubject subject) {
        if (subject != null) {
            this.subject = subject;
            initalizeFields();
            isNew = false;
        } else {
            this.subject = new ObservableSubject(new Subject());
            isNew = true;
        }
    }

    @FXML public void handleOk() {
        fillDto();
        if (isNew) {
            overviewController.addSubject(subject);
        } else {
            overviewController.updatesubject(subject);
        }
        dialogStage.close();
    }

    private void fillDto() {
        subject.setName(name.getText());
        subject.setEcts(parseDouble());
        subject.setSemester(semester.getText());
        subject.setAuthor(author.getText());
    }

    private Double parseDouble() {
        try {
            return Double.parseDouble(ects.getText());

        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    @FXML public void handleCancel() {
        dialogStage.close();
    }

    public void initalizeFields() {
        name.setText(this.subject.getName());
        semester.setText(this.subject.getSemester());
        ects.setText(Double.toString(this.subject.getEcts()));
        name.setText(this.subject.getAuthor());
    }
}
