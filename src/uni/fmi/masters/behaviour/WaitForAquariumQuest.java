package uni.fmi.masters.behaviour;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;
import uni.fmi.masters.AquariumOntology;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class WaitForAquariumQuest extends CyclicBehaviour {

    private AquariumOntology ontology;
    private ObjectMapper mapper;

    public WaitForAquariumQuest(AquariumOntology ontology, Agent agent) {
        super(agent);
        this.ontology = ontology;
        this.mapper = new ObjectMapper();
    }

    @Override
    public void action() {
        MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
        ACLMessage msg = myAgent.receive(mt);

        if (msg != null) {
            System.out.println("AquariumExpertAgent received request from " + msg.getSender().getName());
            String requestContent = msg.getContent();
            ACLMessage reply = msg.createReply();

            try {
                ontology.reloadOntology();

                Map<String, Object> requestParams = mapper.readValue(requestContent,
                        new com.fasterxml.jackson.core.type.TypeReference<Map<String, Object>>() {
                        });

                String tankSize = (String) requestParams.get("tankSize");
                String waterType = (String) requestParams.get("waterType");
                String preferredTemperature = (String) requestParams.get("preferredTemperature");
                String aggressionLevel = (String) requestParams.get("aggressionLevel");

                Integer numFishKinds = (Integer) requestParams.get("numFishKinds");
                List<String> alreadyHaveFish = (List<String>) requestParams.get("alreadyHaveFish");

                System.out.println(
                        "Searching for: Tank=" + tankSize + ", Water=" + waterType + ", Temp=" + preferredTemperature +
                                ", Aggression=" + aggressionLevel +
                                (numFishKinds != null ? ", Kinds=" + numFishKinds : "") +
                                (alreadyHaveFish != null && !alreadyHaveFish.isEmpty()
                                        ? ", Existing=" + String.join(", ", alreadyHaveFish)
                                        : ""));

                List<Fish> suitableFish = ontology.getSuitableFish(tankSize, waterType, preferredTemperature,
                        aggressionLevel);

                if (alreadyHaveFish != null && !alreadyHaveFish.isEmpty()) {
                    List<Fish> compatibleNewFish = new ArrayList<>();

                    for (Fish potentialNewFish : suitableFish) {
                        boolean allCompatible = true;
                        for (String existingFishName : alreadyHaveFish) {
                            String compatibility = ontology.checkFishCompatibility(potentialNewFish.getName(),
                                    existingFishName);
                            if (compatibility.equals("Incompatible")) {
                                allCompatible = false;
                                break;
                            }
                        }
                        if (allCompatible) {
                            compatibleNewFish.add(potentialNewFish);
                        }
                    }
                    suitableFish = compatibleNewFish;
                }

                List<Plant> suitablePlants = ontology.getSuitablePlantsForTankSize(tankSize);

                Map<String, Object> recommendations = new HashMap<>();
                recommendations.put("fish", suitableFish);
                recommendations.put("plants", suitablePlants);
                recommendations.put("alreadyHaveFish", alreadyHaveFish);

                if (!suitableFish.isEmpty() || !suitablePlants.isEmpty()) {
                    System.out.println("AquariumExpertAgent found recommendations.");
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(mapper.writeValueAsString(recommendations));
                    reply.setLanguage("JSON");
                } else {
                    System.out.println("AquariumExpertAgent found no recommendations for the given criteria.");
                    reply.setPerformative(ACLMessage.INFORM_REF);
                    reply.setContent("No suitable fish or plants found for your criteria given your preferences.");
                    reply.setLanguage("JSON");
                }

            } catch (JsonProcessingException e) {
                System.err.println("Error processing JSON request: " + e.getMessage());
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("Invalid request format received by expert agent.");
                reply.setLanguage("JSON");
            } catch (Exception e) {
                System.err.println("An unexpected error occurred in AquariumExpertAgent: " + e.getMessage());
                e.printStackTrace();
                reply.setPerformative(ACLMessage.FAILURE);
                reply.setContent("An internal error occurred while processing your request.");
                reply.setLanguage("JSON");
            } finally {
                myAgent.send(reply);
            }
        } else {
            block();
        }
    }
}