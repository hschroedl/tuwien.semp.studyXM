package at.ac.tuwien.sepm.ss16.qse18.gui;

import at.ac.tuwien.sepm.util.AlertBuilder;
import at.ac.tuwien.sepm.util.SpringFXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Modality;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Abstract base class for all controllers that are not the mainframe controlelr.
 * Provides access to Alerts, Logger, and the MainFrameController.
 *
 * @author Hans-Joerg Schroedl
 */
@Component public abstract class BaseController implements GuiController {

    protected final Logger logger = LogManager.getLogger(getClass());

    protected MainFrameController mainFrameController;
    private AlertBuilder alertBuilder;
    @Autowired protected SpringFXMLLoader springFXMLLoader;

    @Autowired public void setMainFrameController(MainFrameController mainFrameController) {
        this.mainFrameController = mainFrameController;
    }

    @Autowired public void setAlertBuilder(AlertBuilder alertBuilder) {
        this.alertBuilder = alertBuilder;
    }

    private void showAlert(String title, String headerMsg, String contentMsg) {
        Alert alert =
            alertBuilder.alertType(Alert.AlertType.INFORMATION).title(title).headerText(headerMsg)
                .modality(Modality.APPLICATION_MODAL).contentText(contentMsg).setResizable(true)
                .build();
        alert.showAndWait();
    }

    protected void showSuccess(String contentMsg) {
        showAlert("Success", "Operation completed successfully.", contentMsg);
    }

    protected void showError(String contentMsg) {
        showAlert("Error", "An error occured.", contentMsg);
    }

    protected void showError(Exception e) {
        showAlert("Error", "An error occurred.", e.getMessage());
    }

    protected void showInformation(String contentMsg){
        Alert alert =
            alertBuilder.alertType(Alert.AlertType.INFORMATION).title("INFO").headerText("")
                .contentText(contentMsg).setResizable(true).build();
        alert.show();
    }

    protected boolean showConfirmation(String contentMsg) {
        Alert alert = alertBuilder.alertType(Alert.AlertType.CONFIRMATION).title("Confirmation")
            .setResizable(true).headerText("Are you sure?").contentText(contentMsg)
            .modality(Modality.APPLICATION_MODAL).build();
        alert.showAndWait();
        ButtonType result = alert.getResult();
        return result == ButtonType.OK;
    }

    protected <T extends GuiController> T setSubView(String fxmlPath, Class T, Pane subPane) throws IOException {
        logger.debug("Loading view from " + fxmlPath);
        SpringFXMLLoader.FXMLWrapper<Object, T> wrapper = springFXMLLoader.loadAndWrap(fxmlPath, T);
        subPane.getChildren().clear();
        Pane newPane = (Pane) wrapper.getLoadedObject();
        newPane.setPrefWidth(subPane.getWidth());
        newPane.setPrefHeight(subPane.getHeight());
        AnchorPane.setTopAnchor(newPane, 0.0);
        AnchorPane.setRightAnchor(newPane, 0.0);
        AnchorPane.setLeftAnchor(newPane, 0.0);
        AnchorPane.setBottomAnchor(newPane, 0.0);
        subPane.getChildren().add(newPane);
        return wrapper.getController();
    }


}