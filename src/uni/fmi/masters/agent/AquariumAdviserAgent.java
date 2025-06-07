package uni.fmi.masters.agent;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SequentialBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;
import uni.fmi.masters.AquariumOntology;
import uni.fmi.masters.gui.AquariumClientGUI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AquariumAdviserAgent extends Agent {

    private AquariumClientGUI gui;
    private List<AID> expertAgents;
    private ObjectMapper mapper;
    private AquariumOntology ontology;

    public AquariumAdviserAgent() {
        this.ontology = new AquariumOntology();
    }

    public AquariumOntology getOntology() {
        return ontology;
    }

    @Override
    protected void setup() {
        System.out.println("AquariumAdviserAgent " + getAID().getName() + " is starting.");
        gui = new AquariumClientGUI(this);
        mapper = new ObjectMapper();

        addBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                Map<String, List<String>> ontologyData = new HashMap<>();
                ontologyData.put("tankSizes", ontology.getAllTankSizeRanges());
                ontologyData.put("waterTypes", ontology.getAllWaterTypes());
                ontologyData.put("temperatures", ontology.getAllWaterTemperatures());
                ontologyData.put("aggressionLevels", ontology.getAllAggressionLevels());
                ontologyData.put("fishNames", ontology.getAllFishNames());
                gui.populateDropdowns(ontologyData);
            }
        });
    }

    @Override
    protected void takeDown() {
        if (gui != null) {
            gui.dispose();
        }
        System.out.println("AquariumAdviserAgent " + getAID().getName() + " is terminating.");
        try {
            DFAgentDescription dfd = new DFAgentDescription();
            dfd.setName(getAID());
            DFService.deregister(this, dfd);
        } catch (FIPAException e) {
            System.err.println("Error deregistering agent: " + e.getMessage());
        }
    }

    /**
     * Public method to be called by the GUI when the "Get Recommendations" button
     * is clicked.
     * Initiates the sequence of behaviors to find and request recommendations.
     *
     * @param tankSize
     * @param waterType
     * @param preferredTemperature
     * @param aggressionLevel
     * @param numFishKinds         (Optional) How many kinds of fish the user wants.
     *                             Null if not specified.
     * @param alreadyHaveFish      (Optional) List of fish names the user already
     *                             has. Null if not specified.
     */
    public void requestRecommendations(String tankSize, String waterType, String preferredTemperature,
            String aggressionLevel,
            Integer numFishKinds, List<String> alreadyHaveFish) {
        expertAgents = new ArrayList<>();

        SequentialBehaviour overallRequest = new SequentialBehaviour(this) {
            @Override
            public int onEnd() {
                System.out.println("Sequential behaviour for recommendation request finished.");
                return 1;
            }
        };

        overallRequest.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                DFAgentDescription template = new DFAgentDescription();
                ServiceDescription sd = new ServiceDescription();
                sd.setType("aquarium-consultant");
                template.addServices(sd);

                try {
                    DFAgentDescription[] result = DFService.search(myAgent, template);
                    expertAgents.clear();
                    for (DFAgentDescription description : result) {
                        expertAgents.add(description.getName());
                    }

                    if (expertAgents.isEmpty()) {
                        System.out.println("No AquariumExpertAgent found. Please ensure it's running.");
                        gui.displayErrorMessage("No AquariumExpertAgent found. Please start it.");

                    } else {
                        System.out.println("Found " + expertAgents.size() + " AquariumExpertAgent(s).");
                    }
                } catch (FIPAException e) {
                    System.err.println("DFService search error: " + e.getMessage());
                    e.printStackTrace();
                    gui.displayErrorMessage("Failed to search for expert agents: " + e.getMessage());
                }
            }
        });

        overallRequest.addSubBehaviour(new OneShotBehaviour() {
            @Override
            public void action() {
                if (expertAgents.isEmpty()) {
                    System.out.println("Skipping message sending as no expert agents found in the previous step.");
                    return;
                }

                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (AID expert : expertAgents) {
                    cfp.addReceiver(expert);
                }

                Map<String, Object> requestParams = new HashMap<>();
                requestParams.put("tankSize", tankSize);
                requestParams.put("waterType", waterType);
                requestParams.put("preferredTemperature", preferredTemperature);
                requestParams.put("aggressionLevel", aggressionLevel);
                if (numFishKinds != null) {
                    requestParams.put("numFishKinds", numFishKinds);
                }
                if (alreadyHaveFish != null && !alreadyHaveFish.isEmpty()) {
                    requestParams.put("alreadyHaveFish", alreadyHaveFish);
                }

                try {
                    String jsonContent = mapper.writeValueAsString(requestParams);
                    cfp.setContent(jsonContent);
                    cfp.setLanguage("JSON");
                    cfp.setConversationId("aquarium-recommendation");
                    String replyWithId = "cfp" + System.currentTimeMillis();
                    cfp.setReplyWith(replyWithId);

                    myAgent.send(cfp);
                    System.out.println("Sent CFP to expert agent(s) with: " + jsonContent);

                    MessageTemplate mt = MessageTemplate.and(
                            MessageTemplate.MatchConversationId("aquarium-recommendation"),
                            MessageTemplate.MatchInReplyTo(replyWithId));

                    ACLMessage reply = myAgent.blockingReceive(mt, 10000);

                    if (reply != null) {
                        handleReply(reply);
                    } else {
                        System.out.println("Timeout: No reply from expert agent within 10 seconds.");
                        gui.displayErrorMessage(
                                "No response from expert agent within 10 seconds. Check expert agent logs.");
                    }
                } catch (JsonProcessingException e) {
                    System.err.println("Error creating JSON request: " + e.getMessage());
                    e.printStackTrace();
                    gui.displayErrorMessage("Failed to format request: " + e.getMessage());
                }
            }
        });

        addBehaviour(overallRequest);
    }

    private void handleReply(ACLMessage reply) {
        if (reply.getPerformative() == ACLMessage.PROPOSE) {
            try {
                TypeReference<Map<String, Object>> typeRef = new TypeReference<>() {
                };
                Map<String, Object> recommendationMap = mapper.readValue(reply.getContent(), typeRef);

                List<Fish> recommendedFish = new ArrayList<>();
                List<Plant> recommendedPlants = new ArrayList<>();

                Object fishData = recommendationMap.get("fish");
                if (fishData != null) {
                    recommendedFish = mapper.convertValue(
                            fishData,
                            new TypeReference<List<Fish>>() {
                            });
                }

                Object plantData = recommendationMap.get("plants");
                if (plantData != null) {
                    recommendedPlants = mapper.convertValue(
                            plantData,
                            new TypeReference<List<Plant>>() {
                            });
                }

                gui.displayRecommendations(recommendedFish, recommendedPlants);

            } catch (IOException e) {
                e.printStackTrace();
                gui.displayErrorMessage(
                        "Failed to parse recommendations: " + e.getMessage() + ". Check expert agent's reply format.");
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                gui.displayErrorMessage(
                        "Failed to convert recommendation data: " + e.getMessage() + ". Data format mismatch.");
            }
        } else if (reply.getPerformative() == ACLMessage.INFORM_REF) {
            gui.displayErrorMessage("No recommendations: " + reply.getContent());
        } else if (reply.getPerformative() == ACLMessage.REFUSE) {
            gui.displayErrorMessage("Expert Agent refused request: " + reply.getContent());
        } else {
            gui.displayErrorMessage("Unexpected response from expert agent (Performative: "
                    + ACLMessage.getPerformative(reply.getPerformative()) + "). Content: " + reply.getContent());
        }
    }
}