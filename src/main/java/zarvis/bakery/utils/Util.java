package zarvis.bakery.utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

import com.google.gson.Gson;

import jade.core.AID;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import zarvis.bakery.agents.BakeryAgent;
import zarvis.bakery.models.BakeryJsonWrapper;

public class Util {

	private static Logger logger = LoggerFactory.getLogger(Util.class);
	
	public static BakeryJsonWrapper getWrapper(){
		final String FILENAME = "src/main/config/sample-scenario.json";
		BakeryJsonWrapper jsonwrapper = null;
		try {
			// read json file and convert them to objects
			BufferedReader reader = new BufferedReader(new FileReader(FILENAME));
			jsonwrapper = new Gson().fromJson(reader, BakeryJsonWrapper.class);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return jsonwrapper;
	}

	public static DFAgentDescription[] searchInYellowPage(Agent agent,String keyword){
		DFAgentDescription agentDescription= new DFAgentDescription();
		ServiceDescription serviceDescription = new ServiceDescription();

		serviceDescription.setType(keyword);
		agentDescription.addServices(serviceDescription);

		try {
			return DFService.search(agent, agentDescription);
		} catch (FIPAException e) {
			e.printStackTrace();
			return null;
		}

	}

	public static boolean registerInYelloPage(Agent agent,String type,String name){
		// Create agent description and set AID
		DFAgentDescription agentDescription = new DFAgentDescription();
		agentDescription.setName(agent.getAID());

		// Create service description and set type and bakery name
		ServiceDescription serviceDescription = new ServiceDescription();
		serviceDescription.setType(type);
		serviceDescription.setName(name);

		// add the service description to this agent
		agentDescription.addServices(serviceDescription);
		try {
			DFService.register(agent, agentDescription);
			logger.info("{} is added to yellow pages",name);
			return true;
		} catch (FIPAException e) {
			e.printStackTrace();
			return false;
		}

	}

}
