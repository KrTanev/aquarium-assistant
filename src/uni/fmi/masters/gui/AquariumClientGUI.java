package uni.fmi.masters.gui;

import uni.fmi.masters.agent.AquariumAdviserAgent;
import uni.fmi.masters.DBManager;
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;
import uni.fmi.masters.model.User;
import uni.fmi.masters.service.CurrentUser;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class AquariumClientGUI extends JFrame {

    private AquariumAdviserAgent agent;
    private AdviserPanel adviserPanel;
    private AdminPanel adminPanel;
    private UserProfilePanel userProfilePanel;
    private JTabbedPane tabbedPane;

    public AquariumClientGUI(AquariumAdviserAgent agent) {
        this.agent = agent;
        DBManager.initializeDatabase();

        LoginRegisterDialog loginDialog = new LoginRegisterDialog(this);
        loginDialog.setVisible(true);

        User loggedInUser = loginDialog.getAuthenticatedUser();
        if (loggedInUser != null) {
            CurrentUser.setLoggedInUser(loggedInUser);
            initUI();
        } else {
            System.exit(0);
        }
    }

    private void initUI() {
        setTitle("Aquarium Adviser - " + CurrentUser.getLoggedInUser().getUsername());
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();

        adviserPanel = new AdviserPanel(agent);
        adminPanel = new AdminPanel(agent);
        userProfilePanel = new UserProfilePanel();

        tabbedPane.addTab("Adviser Screen", adviserPanel);
        tabbedPane.addTab("Admin Screen", adminPanel);
        tabbedPane.addTab("User Profile", userProfilePanel);

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