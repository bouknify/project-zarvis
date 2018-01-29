package zarvis.bakery.agents;

import java.util.List;
import java.util.Map;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.core.behaviours.WakerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import zarvis.bakery.models.Bakery;
import zarvis.bakery.models.Location;
import zarvis.bakery.models.Node;
import zarvis.bakery.utils.NeiGraph;
import zarvis.bakery.utils.Util;
import zarvis.bakery.messages.CustomMessage;

public class TruckAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Bakery bakery;
	private String bakeryGuid;
	private boolean isAvailable = true;
	private NeiGraph neig;
	private Location bakeryLoc;
	
	private Node bakeryNode = new Node();
	
	public TruckAgent(Bakery bakery) {
		this.bakery = bakery;
		setNeig(Util.InitializeGraph());
	}
	
	@Override
	protected void setup() {
		Util.registerInYellowPage(this, "TruckAgent", this.bakery.getGuid());
		setBakeryGuid(bakery.getGuid());
		setBakeryLoc(bakery.getLocation());
		for (Map.Entry<Node, List<Node>> n : neig.GetMap().entrySet()) {
			if (n.getKey().getLocation().getX() == bakeryLoc.getX() && 
					n.getKey().getLocation().getY() == bakeryLoc.getY()) {
				bakeryNode = n.getKey();
				break;
			}
		}
		ParallelBehaviour pal = new ParallelBehaviour();
		pal.addSubBehaviour(new ReceiveRequest());
		addBehaviour(pal);
	}
	
	private class ReceiveRequest extends CyclicBehaviour{
		
		private static final long serialVersionUID = 1L;
		private MessageTemplate truckTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(CustomMessage.REQUEST_DELIVERY),
				MessageTemplate.MatchConversationId("delivery-request"));
		public void action() {
			ACLMessage truckMsg= myAgent.receive(truckTemplate);
			if (truckMsg != null) {
				if (isAvailable) {
					
					System.out.println(bakery.getGuid() + " [TRUCK] accept request");
					ACLMessage truckReply = truckMsg.createReply();
					truckReply.setPerformative(ACLMessage.CONFIRM);
					myAgent.send(truckReply);
					
					isAvailable = false;
					
					long waitTime = calculateWaitTime(truckMsg.getContent());
					System.out.println(bakery.getGuid() + " [TRUCK] accept request, ETA: " + waitTime);
					
					myAgent.addBehaviour(new Deliver(myAgent, waitTime, truckMsg.getContent()));
					myAgent.addBehaviour(new Return(myAgent, waitTime*2));
					
				} else {
					ACLMessage truckReply = truckMsg.createReply();
					truckReply.setPerformative(ACLMessage.REFUSE);
					myAgent.send(truckReply);
				}
			}
		}
	}
	
	private class Deliver extends WakerBehaviour {
		
		
		private static final long serialVersionUID = 1L;
		private String customer;
		private String msg;

		public Deliver(Agent a, long timeout, String msg) {
			super(a, timeout);
//			System.out.println(msg);
			this.customer = msg.split(",")[1];
			this.msg = msg;
		}
		
		public void onWake() {
//			System.out.println(msg);
			Util.sendMessage(myAgent, new AID(customer, AID.ISLOCALNAME), CustomMessage.FINISH_ORDER, msg, "to-customer-finish-order");
			myAgent.removeBehaviour(this);
		}
		
	}
	
	private class Return extends WakerBehaviour {

		private static final long serialVersionUID = 1L;

		public Return(Agent a, long timeout) {
			super(a, timeout);
			// TODO Auto-generated constructor stub
		}
		
		public void onWake() {
			Util.sendMessage(myAgent, new AID(bakery.getGuid(), AID.ISLOCALNAME), CustomMessage.HAS_RETURNED, myAgent.getLocalName(), "truck-return");
			isAvailable = true;
			myAgent.removeBehaviour(this);
		}
	}
	
	private long calculateWaitTime(String content) {
		
		double waitTime = 0;
		System.out.println(content + "!!!!!!!!!!!!!!!!!!!!!!!!");
		String customer = content.split(",")[1];
		Node targetNode = new Node();
		Node currentNode = bakeryNode;
		for (Map.Entry<Node, List<Node>> n : neig.GetMap().entrySet()) {
			if (n.getKey().getCompany().equals(customer)) {
				targetNode = n.getKey();
				break;
			}
		}
		
		System.out.println(currentNode);
//		System.out.println(targetNode);
		while (currentNode.equals(targetNode)==false) {
			double  minDist = 9999999.00;
			Node minNode = currentNode;
			
			for (Node n1 : neig.GetMap().get(currentNode)) {
				double dist = Math.sqrt(Math.pow(n1.getLocation().getX() - targetNode.getLocation().getX(),2) + 
						Math.pow(n1.getLocation().getY() - targetNode.getLocation().getY(),2));
				if (dist < minDist) {
					minDist = dist;
					minNode = n1;
				}
			}
			
			double travelCost = Math.sqrt(Math.pow(currentNode.getLocation().getX() - minNode.getLocation().getX(),2)
					+ Math.pow(currentNode.getLocation().getY() - minNode.getLocation().getY(),2));
//			System.out.println("[TRUCK] travelCost " + travelCost);
			
			waitTime += travelCost;
			currentNode = minNode;
		}
		return (long) waitTime;
	}

	public String getBakeryGuid() {
		return bakeryGuid;
	}

	public void setBakeryGuid(String bakeryGuid) {
		this.bakeryGuid = bakeryGuid;
	}

	public NeiGraph getNeig() {
		return neig;
	}

	public void setNeig(NeiGraph neig) {
		this.neig = neig;
	}

	public Location getBakeryLoc() {
		return bakeryLoc;
	}

	public void setBakeryLoc(Location bakeryLoc) {
		this.bakeryLoc = bakeryLoc;
	}
}