package zarvis.bakery.Gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import zarvis.bakery.Gui.model.Bakery;
import zarvis.bakery.models.Location;

public class BakeryEditDialogController {
	
	@FXML
    private TextField nameField;
    @FXML
    private TextField guidField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    

    private Stage dialogStage;
    private Bakery bakery;
    private boolean okClicked = false;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     * 
     * @param dialogStage
     */
    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        
        // Set the dialog icon.
        //this.dialogStage.getIcons().add(new Image("file:resources/images/edit.png"));
    }

    /**
     * Sets the Customer to be edited in the dialog.
     * 
     * @param Customer
     */
    public void setBakery(Bakery bakery) {
        this.bakery = bakery;

        // Fill the labels with info from the Customer object.
        nameField.setText(bakery.getName());
        guidField.setText(bakery.getGuid());
        xField.setText(Float.toString(bakery.getLocation().getX()));
        yField.setText(Float.toString(bakery.getLocation().getY()));
        
        
    }

    /**
     * Returns true if the user clicked OK, false otherwise.
     * 
     * @return
     */
    public boolean isOkClicked() {
        return okClicked;
    }

    /**
     * Called when the user clicks ok.
     */
    @FXML
    private void handleOk() {
        if (isInputValid()) {
            bakery.setName(nameField.getText());
            bakery.setGuid(guidField.getText());
            Location loc = new Location();
            loc.setX(Float.parseFloat(xField.getText()));
            loc.setY(Float.parseFloat(yField.getText()));
            bakery.setLocation(loc);

            okClicked = true;
            dialogStage.close();
        }
    }

    /**
     * Called when the user clicks cancel.
     */
    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    /**
     * Validates the user input in the text fields.
     * 
     * @return true if the input is valid
     */
    private boolean isInputValid() {
        String errorMessage = "";

        if (nameField.getText() == null || nameField.getText().length() == 0) {
            errorMessage += "No valid name!\n"; 
        }
        if (guidField.getText() == null || guidField.getText().length() == 0) {
            errorMessage += "No valid  guid!\n"; 
        }
        if (xField.getText() == null || xField.getText().length() == 0) {
            errorMessage += "No valid location.X !\n"; 
        } else {
            // try to parse the postal code into an int.
            try {
                Float.parseFloat(xField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid location.X (must be an float)!\n"; 
            }
        }
        if (yField.getText() == null || yField.getText().length() == 0) {
            errorMessage += "No valid location.Y !\n"; 
        } else {
            // try to parse the postal code into an int.
            try {
                Float.parseFloat(yField.getText());
            } catch (NumberFormatException e) {
                errorMessage += "No valid location.Y (must be an float)!\n"; 
            }
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Fields");
            alert.setHeaderText("Please correct invalid fields");
            alert.setContentText(errorMessage);
            
            alert.showAndWait();
            
            return false;
        }
    }

}
