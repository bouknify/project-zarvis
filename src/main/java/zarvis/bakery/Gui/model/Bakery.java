package zarvis.bakery.Gui.model;

import java.util.ArrayList;
import java.util.List;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import zarvis.bakery.models.KneedingMachine;
import zarvis.bakery.models.Location;
import zarvis.bakery.models.Oven;
import zarvis.bakery.models.Truck;

public class Bakery {
	
	private final StringProperty guid;
	private final StringProperty name;
	private final ObjectProperty<Location> location;
	private ArrayList<Oven> ovens = new ArrayList<Oven>();
	private List<Truck> trucks = new ArrayList<Truck>();
	private List<KneedingMachine> kneeding_machines = new ArrayList<KneedingMachine>();
	public Bakery(){
		this(null,null);
	}
	
	public Bakery(String guid, String name) {
		this.guid = new SimpleStringProperty(guid);
		this.name = new SimpleStringProperty(name);
		this.location = new SimpleObjectProperty<Location>();
		this.location.set(new Location());
	}

	public String getGuid() {
		return guid.get();
	}

	public void setGuid(String guid) {
		this.guid.set(guid);
	}
	
	public StringProperty guidProperty(){
		return this.guid;
	}
	
	public String getName() {
		return name.get();
	}

	public void setName(String name) {
		this.name.set(name);
	}
	
	public StringProperty nameProperty(){
		return this.name;
	}

	public Location getLocation() {
		return location.get();
	}

	public void setLocation(Location location) {
		this.location.set(location);
	}
	public List<Oven> getOvens() {
		return ovens;
	}
	
	public void setOvens(List<Oven> ovens){
		this.ovens = (ArrayList<Oven>) ovens;
	}

	public Oven getOvenById(String selectedItem) {
		for(Oven oven : this.getOvens())
			if(oven.getGuid().equals(selectedItem))
				return oven;
		return null;
	}
	

	public List<Truck> getTrucks() {
		return trucks;
	}

	public void setTrucks(List<Truck> trucks){
		this.trucks = (ArrayList<Truck>) trucks;
	}
	
	public Truck getTruckById(String selectedItem) {
		for(Truck truck : this.getTrucks())
			if(truck.getGuid().equals(selectedItem))
				return truck;
		return null;
	}
	public List<KneedingMachine> getKneedingMachines() {
		return kneeding_machines;
	}

	public void setKneedingMachines(List<KneedingMachine> kneeding_machines){
		this.kneeding_machines = (ArrayList<KneedingMachine>) kneeding_machines;
	}
	
	public KneedingMachine getKneedingMachineById(String selectedItem) {
		for(KneedingMachine kneeding_machine : this.getKneedingMachines())
			if(kneeding_machine.getGuid().equals(selectedItem))
				return kneeding_machine;
		return null;
	}



}
