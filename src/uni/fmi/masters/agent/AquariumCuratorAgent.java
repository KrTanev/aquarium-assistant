package uni.fmi.masters.agent;

import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.core.behaviours.CyclicBehaviour;
import uni.fmi.masters.AquariumOntology;

import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

public class AquariumCuratorAgent extends Agent {

    private AquariumOntology ontology;
    private ObjectMapper mapper;

    @Override
    protected void setup() {
        System.out.println("AquariumCuratorAgent " + getAID().getName() + " is starting.");

        ontology = new AquariumOntology();
        mapper = new ObjectMapper();

        // Handle ontology update messages
        addBehaviour(new HandleOntologyUpdateBehaviour());
    }

    private class HandleOntologyUpdateBehaviour extends CyclicBehaviour {
        @Override
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchConversationId("ontology-update");
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                try {
                    Map<String, Object> data = mapper.readValue(msg.getContent(), Map.class);
                    String action = (String) data.get("action");

                    switch (action) {
                        case "addFish":
                            handleAddFish(msg, data);
                            break;
                        case "addPlant":
                            handleAddPlant(msg, data);
                            break;

                        default:
                            sendReply(msg, ACLMessage.NOT_UNDERSTOOD, "Unknown action: " + action);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    sendReply(msg, ACLMessage.FAILURE, "Error processing update: " + e.getMessage());
                }
            } else {
                block();
            }
        }

        private void handleAddFish(ACLMessage msg, Map<String, Object> data) {
            try {
                String name = (String) data.get("name");
                String temp = (String) data.get("temperature");
                String aggression = (String) data.get("aggression");
                String tankSize = (String) data.get("tankSize");
                String waterType = (String) data.get("waterType");

                // Get compatibility lists and eats
                List<String> compatibleWith = (List<String>) data.getOrDefault("compatibleWith", List.of());
                List<String> sometimesCompatibleWith = (List<String>) data.getOrDefault("sometimesCompatibleWith",
                        List.of());
                List<String> incompatibleWith = (List<String>) data.getOrDefault("incompatibleWith", List.of());
                List<String> eats = (List<String>) data.getOrDefault("eats", List.of());

                boolean added = ontology.createNewFish(name, temp, aggression, tankSize, waterType);
                if (added) {
                    for (String compFish : compatibleWith) {
                        ontology.setFishCompatibility(name, compFish);
                    }
                    for (String scFish : sometimesCompatibleWith) {
                        ontology.setFishSometimesCompatibility(name, scFish);
                    }
                    for (String incFish : incompatibleWith) {
                        ontology.setFishIncompatibility(name, incFish);
                    }
                    ontology.setFishEats(name, eats);

                    ontology.saveOntology();

                    sendReply(msg, ACLMessage.CONFIRM, "Fish added and properties set: " + name);
                } else {
                    sendReply(msg, ACLMessage.FAILURE, "Fish already exists or invalid.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendReply(msg, ACLMessage.FAILURE, "Error adding fish: " + e.getMessage());
            }
        }

        private void handleAddPlant(ACLMessage msg, Map<String, Object> data) {
            try {
                String name = (String) data.get("name");
                String temp = (String) data.get("temperature");
                String tankSize = (String) data.get("tankSize");
                String waterType = (String) data.get("waterType");

                boolean added = ontology.createNewPlant(name, temp, tankSize, waterType);
                if (added) {
                    ontology.saveOntology();
                    sendReply(msg, ACLMessage.CONFIRM, "Plant added: " + name);
                } else {
                    sendReply(msg, ACLMessage.FAILURE, "Plant already exists or invalid.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                sendReply(msg, ACLMessage.FAILURE, "Error adding plant: " + e.getMessage());
            }
        }

        private void sendReply(ACLMessage request, int performative, String content) {
            ACLMessage reply = request.createReply();
            reply.setPerformative(performative);
            reply.setContent(content);
            myAgent.send(reply);
        }
    }

    @Override
    protected void takeDown() {
        System.out.println("AquariumCuratorAgent " + getAID().getName() + " is terminating.");

    }
}