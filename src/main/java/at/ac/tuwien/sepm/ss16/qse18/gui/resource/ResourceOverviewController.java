package at.ac.tuwien.sepm.ss16.qse18.gui.resource;

import at.ac.tuwien.sepm.ss16.qse18.gui.GuiController;
import at.ac.tuwien.sepm.ss16.qse18.gui.MainFrameController;
import at.ac.tuwien.sepm.ss16.qse18.gui.observable.ObservableResource;
import at.ac.tuwien.sepm.ss16.qse18.service.ResourceService;
import at.ac.tuwien.sepm.ss16.qse18.service.ServiceException;
import at.ac.tuwien.sepm.util.AlertBuilder;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Hans-Joerg Schroedl
 */
@Component public class ResourceOverviewController implements GuiController {

    @FXML public ListView<ObservableResource> resourceListView;
    @FXML public Button editButton;
    @FXML public Button deleteButton;
    @Autowired ApplicationContext applicationContext;
    @Autowired MainFrameController mainFrameController;
    private Logger logger = LogManager.getLogger();
    private ObservableList<ObservableResource> resourceList;

    private ResourceService resourceService;
    private AlertBuilder alertBuilder;

    @Autowired
    public ResourceOverviewController(ResourceService resourceService, AlertBuilder alertBuilder) {
        this.resourceService = resourceService;
        this.alertBuilder = alertBuilder;
    }

    @FXML public void initialize() {
        try {
            logger.debug("Initializing subject table");
            initializeListView();
        } catch (ServiceException e) {
            logger.error(e);
            showAlert(e);
        }
    }

    public void addResource(ObservableResource resource) throws ServiceException {
        resourceService.createResource(resource.getResource());
        resourceList.add(resource);
    }

    @FXML public void handleNew() {
        logger.debug("Creating new resource");
        mainFrameController.handleCreateResource();
    }

    private void initializeListView() throws ServiceException {
        List<ObservableResource> observableResources =
            resourceService.getResources().stream().map(ObservableResource::new)
                .collect(Collectors.toList());
        resourceList = FXCollections.observableArrayList(observableResources);
        resourceListView.setItems(resourceList);
        resourceListView.setCellFactory(listView -> applicationContext.getBean(ResourceCell.class));
    }

    private void showAlert(ServiceException e) {
        Alert alert = alertBuilder.alertType(Alert.AlertType.ERROR).title("Error")
            .headerText("An error occurred").contentText(e.getMessage()).setResizable(true).build();
        alert.showAndWait();
    }

}
