package zarvis.bakery.Gui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import zarvis.bakery.Gui.MainApp;
import zarvis.bakery.Gui.model.Bakery;

public class BakeryOverviewController {
	
	@FXML
    private TableView<Bakery> BakeryTable;
    @FXML
    private TableColumn<Bakery, String> nameColumn;
    @FXML
    private TableColumn<Bakery, String> guidColumn;
    @FXML
    private Label nameLabel;
    @FXML
    private Label guidLabel;
    /**
    * @FXML
    * private Label typeLabel;
    * @FXML
    * private Label totalType1Label;
    * @FXML
    * private Label totalType2Label;
    * @FXML
    * private Label totalType3Label;
    */
    @FXML
    private Label locationLabel;
    
    
    // Reference to the main application.
    private MainApp mainApp;

    /**
     * The constructor.
     * The constructor is called before the initialize() method.
     */
    public BakeryOverviewController() {
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Initialize the Bakery table with the two columns.
        nameColumn.setCellValueFactory(cellData -> cellData.getValue().nameProperty());
        guidColumn.setCellValueFactory(cellData -> cellData.getValue().guidProperty());
        
        // Clear Bakery details.
        showBakeryDetails(null);

        // Listen for selection changes and show the Bakery details when changed.
        BakeryTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBakeryDetails(newValue));
    }

    /**
     * Is called by the main application to give a reference back to itself.
     * 
     * @param mainApp
     */
    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        // Add observable list data to the table
        BakeryTable.setItems(mainApp.getBakeryData());
    }
    
    /**
     * Fills all text fields to show details about the Customer.
     * If the specified Customer is null, all text fields are cleared.
     * 
     * @param Customer the Customer or null
     */
    private void showBakeryDetails(Bakery Bakery) {
        if (Bakery != null) {
            // Fill the labels with info from the Customer object.
            nameLabel.setText(Bakery.getName());
            guidLabel.setText(Bakery.getGuid());
            locationLabel.setText("x = "+Bakery.getLocation().getX()+ ", y = "+Bakery.getLocation().getY());

        } else {
            // Customer is null, remove all the text.
            nameLabel.setText("");
            guidLabel.setText("");
            locationLabel.setText("");

        }
    }
    
    /**
     * Called when the user clicks on the delete button.
     */
    @FXML
    private void handleDeleteBakery() {
        int selectedIndex = BakeryTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            BakeryTable.getItems().remove(selectedIndex);
        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bakery Selected");
            alert.setContentText("Please select a Bakery in the table.");
            
            alert.showAndWait();
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new bakery.
     */
    @FXML
    private void handleNewBakery() {
        Bakery tempBakery = new Bakery();
        boolean okClicked = mainApp.showBakeryEditDialog(tempBakery);
        if (okClicked) {
            mainApp.getBakeryData().add(tempBakery);
        }
    }
    
    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected Bakery.
     */
    @FXML
    private void handleEditBakery() {
        Bakery selectedBakery = BakeryTable.getSelectionModel().getSelectedItem();
        if (selectedBakery != null) {
            boolean okClicked = mainApp.showBakeryEditDialog(selectedBakery);
            if (okClicked) {
                showBakeryDetails(selectedBakery);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bakery Selected");
            alert.setContentText("Please select a Bakery in the table.");
            
            alert.showAndWait();
        }
    }
    
    /**
     * Called when the user clicks the new button. Opens a dialog to edit
     * details for a new Customer.
     */
    @FXML
    private void handleOvens() {
    	Bakery selectedBakery = BakeryTable.getSelectionModel().getSelectedItem();
    	 if (selectedBakery != null) {
            mainApp.showOvens(selectedBakery);

         } else {
             // Nothing selected.
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Bakery Selected");
             alert.setContentText("Please select a Bakery in the list.");
             
             alert.showAndWait();
         }
    }
    @FXML
    private void handleTrucks() {
    	Bakery selectedBakery = BakeryTable.getSelectionModel().getSelectedItem();
    	 if (selectedBakery != null) {
            mainApp.showTrucks(selectedBakery);

         } else {
             // Nothing selected.
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Bakery Selected");
             alert.setContentText("Please select a Bakery in the list.");
             
             alert.showAndWait();
         }
    }

    @FXML
    private void handleKneedingMachines() {
    	Bakery selectedBakery = BakeryTable.getSelectionModel().getSelectedItem();
    	 if (selectedBakery != null) {
            mainApp.showKneedingMachines(selectedBakery);

         } else {
             // Nothing selected.
             Alert alert = new Alert(AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Bakery Selected");
             alert.setContentText("Please select a Bakery in the list.");
             
             alert.showAndWait();
         }
    }

}
