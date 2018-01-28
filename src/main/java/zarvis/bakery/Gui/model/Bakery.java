package zarvis.bakery.Gui.model;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import zarvis.bakery.models.Location;

public class Bakery {
	
	private final StringProperty guid;
	private final StringProperty name;
	private final ObjectProperty<Location> location;
	//private ArrayList<Order> orders = new ArrayList<Order>();
	
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

}
