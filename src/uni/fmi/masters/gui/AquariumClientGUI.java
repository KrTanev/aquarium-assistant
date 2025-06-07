package uni.fmi.masters.gui;

import uni.fmi.masters.agent.AquariumAdviserAgent;
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;

import javax.swing.*;
import java.util.Map;
import java.util.List;

public class AquariumClientGUI extends JFrame {

    private AquariumAdviserAgent agent;
    private AdviserPanel adviserPanel;
    private AdminPanel adminPanel;

    public AquariumClientGUI(AquariumAdviserAgent agent) {
        this.agent = agent;
        initUI();
    }

    private void initUI() {
        setTitle("Aquarium Adviser");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        adviserPanel = new AdviserPanel(agent);
        adminPanel = new AdminPanel(agent);

        tabbedPane.addTab("Adviser Screen", adviserPanel);
        tabbedPane.addTab("Admin Screen", adminPanel);

        add(tabbedPane);
        pack();
        setVisible(true);
    }

    public void populateDropdowns(Map<String, List<String>> ontologyData) {
        adviserPanel.populateDropdowns(ontologyData);
    }

    public void displayRecommendations(List<Fish> recommendedFish, List<Plant> recommendedPlants) {
        adviserPanel.displayRecommendations(recommendedFish, recommendedPlants);
    }

    public void displayErrorMessage(String message) {
        adviserPanel.displayErrorMessage(message);
    }
}