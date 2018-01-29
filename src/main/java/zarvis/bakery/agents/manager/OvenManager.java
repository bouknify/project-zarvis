package zarvis.bakery.agents.manager;

import java.util.Arrays;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.ParallelBehaviour;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import zarvis.bakery.messages.CustomMessage;
import zarvis.bakery.models.Bakery;
import zarvis.bakery.utils.Util;

public class OvenManager extends Agent{
	
	private static final long serialVersionUID = 1L;
	private Bakery bakery;
	private boolean isAvailable = true;
	private boolean isOrderReady = false;
	private DFAgentDescription[] ovens;
	private boolean[] isOvenAvailable;
	
	private boolean hasOrder;
	private boolean isRemainEmpty;
	private String currentOrderGuid;
	private int[] currentOrderOrigin = new int[Util.PRODUCTNAMES.size()];
	private int[] currentOrderRemains = new int[Util.PRODUCTNAMES.size()];
	private int[] currentOrderExisting = new int[Util.PRODUCTNAMES.size()];
	private String currentOrderString;
	
	private boolean talkWithOven;
	
	public OvenManager(Bakery bakery) {
		this.bakery = bakery;
	}
	
	protected void setup() {
		Util.registerInYellowPage(this, "OvenManager", "ovenManager-" + bakery.getGuid());
		ovens = Util.searchInYellowPage(this, "OvenAgent", bakery.getGuid());
		isOvenAvailable = new boolean[ovens.length];
		Arrays.fill(isOvenAvailable, true);
		this.setTalkWithOven(false);
		this.hasOrder = false;
		this.isRemainEmpty = false;
		Arrays.fill(currentOrderRemains, 0);
		
		ParallelBehaviour pal = new ParallelBehaviour();
		
		pal.addSubBehaviour(new AnswerAvailability());
		pal.addSubBehaviour(new ReceiveOrder());
		pal.addSubBehaviour(new WorkDistribution());
		pal.addSubBehaviour(new FinishListener());
		
		addBehaviour(pal);
	}
	
	private class AnswerAvailability extends CyclicBehaviour{
		
		private static final long serialVersionUID = 1L;
		private MessageTemplate avaiTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(CustomMessage.INQUIRE_AVAILABILITY),
				MessageTemplate.MatchConversationId("oven-availability"));
		
		public void action() {
			ACLMessage avaiMsg = myAgent.receive(avaiTemplate);
			if (avaiMsg!=null) {
				ACLMessage avaiReply = avaiMsg.createReply();
				avaiReply.setContent(isAvailable ? "A" : "U");
				avaiReply.setPerformative(CustomMessage.RESPOND_AVAILABILITY);
				myAgent.send(avaiReply);
			} else {
				block();
			}
		}
	}
	
	private class ReceiveOrder extends CyclicBehaviour{
		
		private static final long serialVersionUID = 1L;
		private MessageTemplate orderTemplate = MessageTemplate.and(
				MessageTemplate.MatchPerformative(CustomMessage.INFORM_ORDER),
				MessageTemplate.MatchConversationId("oven-order"));
		public void action() {
			ACLMessage orderMsg = myAgent.receive(orderTemplate);
			if (orderMsg!=null && isAvailable) {
				String orderString = orderMsg.getContent();
				setCurrentOrderString(orderString);
				
				System.out.println("[OVEN] Order received: " + orderString);
				ACLMessage orderReply = orderMsg.createReply();
				orderReply.setPerformative(ACLMessage.CONFIRM);
				isAvailable = false;
				myAgent.send(orderReply);
				
				InitOrder(orderString);
			} else if (orderMsg!=null && isAvailable == false) {
				ACLMessage orderReply = orderMsg.createReply();
				orderReply.setPerformative(ACLMessage.REFUSE);
				myAgent.send(orderReply);
			} else {
				block(15*Util.MILLIS_PER_MIN);
			}
		}
	}
	
	private class WorkDistribution extends CyclicBehaviour{
		
		
		private static final long serialVersionUID = 1L;
		private MessageTemplate avaiTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("single-oven-availability"),
				MessageTemplate.MatchPerformative(CustomMessage.RESPOND_AVAILABILITY));
		private MessageTemplate productConfirmTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("oven-product"),
				MessageTemplate.MatchPerformative(ACLMessage.CONFIRM));
		
		private int step = 0;
		private int consideringOven = 0;
		private int consideringProduct = 0;
		
		public void action() {
			switch(step) {
			case 0:
				if (hasOrder && isRemainEmpty == false) {
					for (int i = 0; i < isOvenAvailable.length; i++ ) {
						if (isOvenAvailable[i] == true) {
							System.out.println("[OVEN] Ask oven");
							Util.sendMessage(myAgent, ovens[i].getName(), CustomMessage.INQUIRE_AVAILABILITY, "", "single-oven-availability");
							consideringOven = i;
							step = 1;
							break;
						}
					}
				}
				
				block(15*Util.MILLIS_PER_MIN);
				break;
			case 1:
				ACLMessage avaiReply = myAgent.receive(avaiTemplate);
				if (avaiReply!=null && avaiReply.getContent().equals("U")) {
					//Not really free
					isOvenAvailable[consideringOven] = false;
					step = 0;
				}
				else if (avaiReply!=null && avaiReply.getContent().equals("A")) {
					isRemainEmpty = true;
					for (int i = 0; i < currentOrderRemains.length; i++) {
						if (currentOrderRemains[i]> 0) {
							consideringProduct = i;
							isRemainEmpty = false;
							Util.sendMessage(myAgent, ovens[consideringOven].getName(), CustomMessage.INFORM_PRODUCT, Integer.toString(i), "oven-product");
							step = 2;
							break;
						}
					}
					
					if (isRemainEmpty) {
						step = 0;
					}
				} else {
					block();
				}
				
				break;
				
			case 2:
				ACLMessage productReply = myAgent.receive(productConfirmTemplate);
				
				if (productReply!=null && productReply.getPerformative()==ACLMessage.CONFIRM) {
					System.out.println("[OVEN] Get Oven confirm");
					currentOrderRemains[consideringProduct]--;
					isOvenAvailable[consideringOven] = false;
					step = 0;
				} else if (productReply!=null && productReply.getPerformative()==ACLMessage.REFUSE) {
					step = 0;
				} else {
					block();
				}
				break;
			}
		}
	}
	
	private class FinishListener extends CyclicBehaviour{
		
		private static final long serialVersionUID = 1L;
		private MessageTemplate finishProductTemplate = MessageTemplate.and(MessageTemplate.MatchConversationId("oven-product-finish"),
				MessageTemplate.MatchPerformative(CustomMessage.FINISH_PRODUCT));
		
		public void action() {
			ACLMessage productFinishMsg = myAgent.receive(finishProductTemplate);
			
			if (productFinishMsg!=null) {
				System.out.println("[OVEN] " + productFinishMsg.getContent());
				String[] content = productFinishMsg.getContent().split(",");
				int productIdx = Integer.parseInt(content[1]);
				currentOrderExisting[productIdx]++;
				
				for (int i = 0; i < isOvenAvailable.length; i++) {
					if (ovens[i].getName().getLocalName().equals(content[0])) {
						isOvenAvailable[i] = true;
					}
				}
				
				if(Arrays.equals(currentOrderExisting, currentOrderOrigin)) {
//					talkWithOven = true;
					isAvailable = true;
					Util.sendMessage(myAgent, bakery.getAid(), CustomMessage.FINISH_ORDER, currentOrderGuid, "FINISH");
				}	
			} else {
				block();
			}
		}
		
	}
	
	private void InitOrder(String orderString) {
		String[] content = orderString.split(",");
		hasOrder = true;
		isRemainEmpty = false;
		currentOrderGuid = content[0];
		String[] currentOrderinString = content[1].split("\\.");
		for (int i = 0; i < currentOrderinString.length; i++) {
			currentOrderOrigin[i] = Integer.parseInt(currentOrderinString[i]);
		}
		Arrays.fill(currentOrderExisting, 0);
		currentOrderRemains = currentOrderOrigin.clone();
	}

	public boolean isOrderReady() {
		return isOrderReady;
	}

	public void setOrderReady(boolean isOrderReady) {
		this.isOrderReady = isOrderReady;
	}

	public String getCurrentOrderString() {
		return currentOrderString;
	}

	public void setCurrentOrderString(String currentOrderString) {
		this.currentOrderString = currentOrderString;
	}

	public boolean isTalkWithOven() {
		return talkWithOven;
	}

	public void setTalkWithOven(boolean talkWithOven) {
		this.talkWithOven = talkWithOven;
	}
}
