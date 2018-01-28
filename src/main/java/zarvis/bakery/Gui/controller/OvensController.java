package zarvis.bakery.Gui.controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import zarvis.bakery.Gui.model.Bakery;
import zarvis.bakery.models.Oven;

public class OvensController implements Initializable {
	@FXML
	  private Button BtnDelete;
	  
	  @FXML 
	  private HBox HBox4Btns; 

	  @FXML 
	  private ListView<String> listBoxMain;

	  @FXML 
	  private Label TitleLbl; 
	  @FXML 
	  private Label guidLabel;
	  @FXML 
	  private Label bakeryIdLabel; 
	  @FXML 
	  private Label productLabel; 
	  @FXML 
	  private VBox VBoxMain;
	  @FXML 
	  private Label ovenCoolingRate; 
	  @FXML 
	  private Label ovenHeatingRate; 

	  @FXML
	  private TextField txtAddItem; 
	  
	  private Bakery bakery;
	  
	  final ObservableList<String> listItems = FXCollections.observableArrayList();        
	  
	  @FXML
	  private void deleteAction(ActionEvent action){
	   int selectedItem = listBoxMain.getSelectionModel().getSelectedIndex();
	    listItems.remove(selectedItem);    
	  }

	  @FXML
	  private void details(ActionEvent action){
		  String selectedItem = listBoxMain.getSelectionModel().getSelectedItem();
		  Oven oven = bakery.getOvenById(selectedItem);
		  guidLabel.setText("Oven Id: "+oven.getGuid());
		  bakeryIdLabel.setText("Bakery Id: "+bakery.getGuid());
		  ovenCoolingRate.setText("Oven Cooling Rate:"+oven.getCooling_rate());
		  ovenHeatingRate.setText("Oven Heating Rtae:"+oven.getHeating_rate());
	  }
	  
	  @Override
	  public void initialize(URL url, ResourceBundle rb) {
	    
		// TODO
	    listBoxMain.setItems(listItems);
	    
	    // Disable buttons to start
	    BtnDelete.setDisable(true);
	  

	    // Add a ChangeListener to ListView to look for change in focus
	    listBoxMain.focusedProperty().addListener(new ChangeListener<Boolean>() {
	      public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
	        if(listBoxMain.isFocused()){
	          BtnDelete.setDisable(false);
	        }
	     }
	    });    
	    
	  }


	  	public void setOvens(Bakery bakery) {
	  		this.bakery = bakery;
	  		for(Oven oven : bakery.getOvens()){
				listItems.add(oven.getGuid());
				
			}
	  	}  

}
