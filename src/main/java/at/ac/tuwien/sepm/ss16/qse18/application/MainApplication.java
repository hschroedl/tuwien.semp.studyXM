package at.ac.tuwien.sepm.ss16.qse18.application;

import at.ac.tuwien.sepm.ss16.qse18.gui.MainFrameController;
import at.ac.tuwien.sepm.ss16.qse18.gui.subject.SubjectOverviewController;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * The starting point of the sample application.
 *
 * @author Dominik Moser
 */
@Configuration
@ComponentScan("at.ac.tuwien.sepm")
public class MainApplication extends Application {

    private Logger LOG = LoggerFactory.getLogger(MainApplication.class);

    private AnnotationConfigApplicationContext applicationContext = null;

    public static void main(String[] args) {
        Application.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        LOG.info("Starting Application");
        applicationContext = new AnnotationConfigApplicationContext(MainApplication.class);
        SpringFXMLLoader springFXMLLoader = applicationContext.getBean(SpringFXMLLoader.class);
        SpringFXMLLoader.FXMLWrapper<Object, SubjectOverviewController> mfWrapper =
                springFXMLLoader.loadAndWrap("/fxml/subject/subjectOverview.fxml", SubjectOverviewController.class);
        mfWrapper.getController().setPrimaryStage(primaryStage);
        primaryStage.setTitle("SEPM - SS16 - Spring/Maven/FXML Sample");
        primaryStage.setScene(new Scene((Parent) mfWrapper.getLoadedObject(), 800, 400));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        LOG.info("Stopping Application");
        if (this.applicationContext != null && applicationContext.isRunning()) {
            this.applicationContext.close();
        }
        super.stop();
    }

}
