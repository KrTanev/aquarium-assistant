package uni.fmi.masters.agent;

import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import uni.fmi.masters.AquariumOntology;
import uni.fmi.masters.behaviour.WaitForAquariumQuest;

public class AquariumExpertAgent extends Agent {

    private AquariumOntology aquariumOntology;

    @Override
    protected void setup() {
        System.out.println("AquariumExpertAgent " + getAID().getName() + " is starting.");

        aquariumOntology = new AquariumOntology();

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("aquarium-consultant");
        sd.setName("AquariumRecommendationService");
        dfd.addServices(sd);

        try {
            DFService.register(this, dfd);
            System.out.println("AquariumExpertAgent registered service: " + sd.getName() + " of type " + sd.getType());
        } catch (FIPAException e) {
            System.err.println("Error registering AquariumExpertAgent: " + e.getMessage());
            e.printStackTrace();
        }

        addBehaviour(new WaitForAquariumQuest(aquariumOntology, this));
    }

    @Override
    protected void takeDown() {
        try {
            DFService.deregister(this);
            System.out.println("AquariumExpertAgent " + getAID().getName() + " deregistered.");
        } catch (FIPAException e) {
            System.err.println("Error deregistering AquariumExpertAgent: " + e.getMessage());
            e.printStackTrace();
        }
        System.out.println("AquariumExpertAgent " + getAID().getName() + " is terminating.");
    }

}