package zarvis.bakery.Gui.controller;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.Scene;
import javafx.scene.shape.Line;
import javafx.scene.control.Tooltip;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import zarvis.bakery.models.Node;
import zarvis.bakery.models.BakeryJsonWrapper;
import zarvis.bakery.models.Link;
import zarvis.bakery.models.Location;
import zarvis.bakery.models.StreetNetwork;
import zarvis.bakery.utils.Util;

public class StreetNetworkController {
	private  int width = 990;
	private  int height = 700;
	private  List<Rectangle> customers = new ArrayList<>();
	private  List<Circle> bakeries = new ArrayList<>();
	private  List<Line> connections = new ArrayList<>();

	private  StreetNetwork streetNetwork = new StreetNetwork();

	public StreetNetworkController() {
		Map<String, Location> nodeIdToLocationMap = new HashMap<>();
		BakeryJsonWrapper wrapper = Util.getWrapper();
		streetNetwork = wrapper.getStreet_network();
		for (Node node : streetNetwork.getNodes()) {
			float x =  ((float) width / 2) + (node.getLocation().getX() * 40);
			float y = (float) (height / 2.5) + (node.getLocation().getY() * 40);

			Location location = new Location((float) (x + 7.5), (float) (y + 7.5));

			nodeIdToLocationMap.put(node.getGuid(), location);

			if (node.getType().equals("customer")) {
				Rectangle rect = new Rectangle(x, y, 15, 15);
				rect.setFill(Color.BLUE);

				Tooltip tooltip = new Tooltip(node.getName());
				bindTooltip(rect, tooltip);

				customers.add(rect);
			} else if (node.getType().equals("bakery")) {
				Circle circ = new Circle(x, y, 10);
				circ.setFill(Color.GREEN);

				Tooltip tooltip = new Tooltip(node.getName());
				bindTooltip(circ, tooltip);

				bakeries.add(circ);
			}
		}

		for (Link link : streetNetwork.getLinks()) {
			Location origin = nodeIdToLocationMap.get(link.getSource());
			Location destination = nodeIdToLocationMap.get(link.getTarget());

			Line line = new Line(origin.getX(), origin.getY(), destination.getX(), destination.getY());
			connections.add(line);
		}
	}

	public void setStreetNetwork(StreetNetwork network) {
		streetNetwork = network;
	}

	public  void bindTooltip(final javafx.scene.Node node, final Tooltip tooltip) {
		node.setOnMouseMoved(event -> tooltip.show(node, event.getScreenX(), event.getScreenY() + 15));
		node.setOnMouseExited(event -> tooltip.hide());
	}

	public  void start(Stage primaryStage) {
		primaryStage.setTitle("Zarvis Project [Street  Network]");
		Pane pane = new Pane();
		pane.setPrefWidth(width);
		pane.setPrefHeight(height);
		pane.getChildren().addAll(connections);
		pane.getChildren().addAll(customers);
		pane.getChildren().addAll(bakeries);
		Stage dialogStage = new Stage();
        dialogStage.setTitle("orders");
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        Scene scene = new Scene(pane);
        dialogStage.setScene(scene);
		
        dialogStage.setScene(scene);
        dialogStage.showAndWait();
	}

}

