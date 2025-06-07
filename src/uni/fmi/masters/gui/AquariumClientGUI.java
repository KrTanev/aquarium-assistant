package uni.fmi.masters.gui;

import uni.fmi.masters.agent.AquariumAdviserAgent;
import uni.fmi.masters.gui.AdviserPanel; // Import the new AdviserPanel
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;
import uni.fmi.masters.gui.AdminPanel; // Import the new AdminPanel

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.List;

public class AquariumClientGUI extends JFrame {

    private AquariumAdviserAgent agent; // Still need the agent reference to pass it to panels
    private AdviserPanel adviserPanel; // Reference to the adviser panel
    private AdminPanel adminPanel; // Reference to the admin panel

    public AquariumClientGUI(AquariumAdviserAgent agent) {
        this.agent = agent;
        initUI();
    }

    private void initUI() {
        setTitle("Aquarium Adviser");
        setSize(800, 750); // Set initial size, pack() will adjust later
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabbedPane = new JTabbedPane();

        // Create instances of our new panel classes
        adviserPanel = new AdviserPanel(agent);
        adminPanel = new AdminPanel(agent); // Pass the agent to AdminPanel too, if needed for data operations

        // Add the panels to the tabbed pane
        tabbedPane.addTab("Adviser Screen", adviserPanel);
        tabbedPane.addTab("Admin Screen", adminPanel);

        add(tabbedPane);
        pack(); // Pack the frame to adjust size based on content
        setVisible(true);
    }

    // Methods to be called by the agent to update specific panels
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