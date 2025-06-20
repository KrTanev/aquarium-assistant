package uni.fmi.masters;

import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;

import uni.fmi.masters.agent.AquariumExpertAgent;
import uni.fmi.masters.agent.AquariumAdviserAgent;
import uni.fmi.masters.agent.AquariumCuratorAgent;

public class MainClass {

    public static void main(String[] args) {

        Runtime runtime = Runtime.instance();

        Profile profile = new ProfileImpl();

        profile.setParameter(profile.MAIN_HOST, "localhost");
        profile.setParameter(profile.MAIN_PORT, "9898");
        profile.setParameter(profile.GUI, "true");

        AgentContainer mainContainer = runtime.createMainContainer(profile);

        try {
            AgentController expertAgent = mainContainer.createNewAgent("AquariumExpert",
                    AquariumExpertAgent.class.getName(),
                    null);
            expertAgent.start();

            AgentController adviserAgent = mainContainer.createNewAgent("UserAdviser",
                    AquariumAdviserAgent.class.getName(),
                    null);
            adviserAgent.start();

            AgentController curatorAgent = mainContainer.createNewAgent("CuratorAgent",
                    AquariumCuratorAgent.class.getName(),
                    null);
            curatorAgent.start();

            System.out.println(
                    "JADE Main Container, AquariumExpertAgent, AquariumAdviserAgent, and CuratorAgent launched successfully.");

        } catch (StaleProxyException e) {
            System.err.println("Error creating or starting agents: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}