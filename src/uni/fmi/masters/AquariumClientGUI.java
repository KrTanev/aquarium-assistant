package uni.fmi.masters;

import uni.fmi.masters.agent.AquariumAdviserAgent;
import uni.fmi.masters.model.Fish;
import uni.fmi.masters.model.Plant;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AquariumClientGUI extends JFrame {

    private AquariumAdviserAgent agent;

    private JComboBox<String> tankSizeComboBox;
    private JComboBox<String> waterTypeComboBox;
    private JComboBox<String> preferredTemperatureComboBox;
    private JComboBox<String> aggressionLevelComboBox;

    private JCheckBox showAdvancedOptionsCheckBox;
    private JPanel advancedOptionsPanel;
    private JSpinner numFishKindsSpinner;
    private JTextField alreadyHaveFishTextField;

    private JTextArea recommendationsTextArea;
    private JScrollPane scrollPane;

    public AquariumClientGUI(AquariumAdviserAgent agent) {
        this.agent = agent;
        initUI();
    }

    private void initUI() {
        setTitle("Aquarium Adviser");
        setSize(800, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));

        // ! --- Input Panel (Basic) ---
        JPanel basicInputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        basicInputPanel.setBorder(BorderFactory.createTitledBorder("Basic Aquarium Preferences"));

        basicInputPanel.add(new JLabel("Tank Size:"));
        tankSizeComboBox = new JComboBox<>();
        basicInputPanel.add(tankSizeComboBox);

        basicInputPanel.add(new JLabel("Water Type:"));
        waterTypeComboBox = new JComboBox<>();
        basicInputPanel.add(waterTypeComboBox);

        basicInputPanel.add(new JLabel("Preferred Temperature:"));
        preferredTemperatureComboBox = new JComboBox<>();
        basicInputPanel.add(preferredTemperatureComboBox);

        basicInputPanel.add(new JLabel("Fish Aggression Level:"));
        aggressionLevelComboBox = new JComboBox<>();
        basicInputPanel.add(aggressionLevelComboBox);

        populateDropdowns(Map.of(
                "tankSizes", List.of("Loading..."),
                "waterTypes", List.of("Loading..."),
                "temperatures", List.of("Loading..."),
                "aggressionLevels", List.of("Loading...")));

        mainPanel.add(basicInputPanel, BorderLayout.NORTH);

        // ! --- Advanced Options Panel ---
        advancedOptionsPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        advancedOptionsPanel.setBorder(BorderFactory.createTitledBorder("Advanced Options"));

        advancedOptionsPanel.add(new JLabel("Desired Fish Kinds:"));
        numFishKindsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        advancedOptionsPanel.add(numFishKindsSpinner);

        advancedOptionsPanel.add(new JLabel("Already Have Fish (comma-separated):"));
        alreadyHaveFishTextField = new JTextField();
        alreadyHaveFishTextField.setToolTipText("e.g., Guppy, Molly");
        advancedOptionsPanel.add(alreadyHaveFishTextField);

        advancedOptionsPanel.setVisible(false);

        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));

        showAdvancedOptionsCheckBox = new JCheckBox("Show Advanced Options");
        showAdvancedOptionsCheckBox.addActionListener(e -> {
            advancedOptionsPanel.setVisible(showAdvancedOptionsCheckBox.isSelected());
            pack();
        });
        controlsPanel.add(showAdvancedOptionsCheckBox);

        JButton searchButton = new JButton("Get Recommendations");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                requestRecommendations();
            }
        });
        controlsPanel.add(searchButton);

        JButton clearButton = new JButton("Clear All Filters");
        clearButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                clearAllFilters();
            }
        });
        controlsPanel.add(clearButton);

        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.add(controlsPanel, BorderLayout.NORTH);
        centerPanel.add(advancedOptionsPanel, BorderLayout.CENTER);

        mainPanel.add(centerPanel, BorderLayout.CENTER);

        // ! --- Output Panel ---
        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(BorderFactory.createTitledBorder("Recommendations"));

        recommendationsTextArea = new JTextArea(15, 50);
        recommendationsTextArea.setEditable(false);
        recommendationsTextArea.setLineWrap(true);
        recommendationsTextArea.setWrapStyleWord(true);
        scrollPane = new JScrollPane(recommendationsTextArea);
        outputPanel.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(outputPanel, BorderLayout.SOUTH);

        add(mainPanel);
        pack();
        setVisible(true);
    }

    private void requestRecommendations() {
        // Basic parameters
        String selectedTankSize = (String) tankSizeComboBox.getSelectedItem();
        String selectedWaterType = (String) waterTypeComboBox.getSelectedItem();
        String selectedTemperature = (String) preferredTemperatureComboBox.getSelectedItem();
        String selectedAggression = (String) aggressionLevelComboBox.getSelectedItem();

        // Advanced parameters (handle null/empty if not shown)
        Integer numFishKinds = null;
        List<String> alreadyHaveFish = null;

        if (showAdvancedOptionsCheckBox.isSelected()) {
            numFishKinds = (Integer) numFishKindsSpinner.getValue();
            String alreadyHaveFishText = alreadyHaveFishTextField.getText().trim();
            if (!alreadyHaveFishText.isEmpty()) {
                alreadyHaveFish = Arrays.stream(alreadyHaveFishText.split(","))
                        .map(String::trim)
                        .filter(s -> !s.isEmpty())
                        .collect(Collectors.toList());
            }
        }

        if (selectedTankSize == null || selectedTankSize.isEmpty() || selectedTankSize.equals("Loading...")) {
            JOptionPane.showMessageDialog(AquariumClientGUI.this, "Please select a tank size.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedWaterType == null || selectedWaterType.isEmpty() || selectedWaterType.equals("Loading...")) {
            JOptionPane.showMessageDialog(AquariumClientGUI.this, "Please select a water type.", "Missing Input",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedTemperature == null || selectedTemperature.isEmpty() || selectedTemperature.equals("Loading...")) {
            JOptionPane.showMessageDialog(AquariumClientGUI.this, "Please select a preferred temperature.",
                    "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (selectedAggression == null || selectedAggression.isEmpty() || selectedAggression.equals("Loading...")) {
            JOptionPane.showMessageDialog(AquariumClientGUI.this, "Please select a fish aggression level.",
                    "Missing Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        agent.requestRecommendations(
                selectedTankSize,
                selectedWaterType,
                selectedTemperature,
                selectedAggression,
                numFishKinds,
                alreadyHaveFish);
        recommendationsTextArea.setText("Searching for recommendations...\n");
    }

    private void clearAllFilters() {
        if (tankSizeComboBox.getItemCount() > 0)
            tankSizeComboBox.setSelectedIndex(0);
        if (waterTypeComboBox.getItemCount() > 0)
            waterTypeComboBox.setSelectedIndex(0);
        if (preferredTemperatureComboBox.getItemCount() > 0)
            preferredTemperatureComboBox.setSelectedIndex(0);
        if (aggressionLevelComboBox.getItemCount() > 0)
            aggressionLevelComboBox.setSelectedIndex(0);

        // Reset advanced options
        showAdvancedOptionsCheckBox.setSelected(false);
        advancedOptionsPanel.setVisible(false);
        numFishKindsSpinner.setValue(1);
        alreadyHaveFishTextField.setText("");

        recommendationsTextArea.setText("");
        pack();
    }

    /**
     * !Called by the agent to populate the dropdowns with data from the ontology.
     */
    public void populateDropdowns(Map<String, List<String>> ontologyData) {
        SwingUtilities.invokeLater(() -> {
            String currentTankSize = (String) tankSizeComboBox.getSelectedItem();
            String currentWaterType = (String) waterTypeComboBox.getSelectedItem();
            String currentTemp = (String) preferredTemperatureComboBox.getSelectedItem();
            String currentAggression = (String) aggressionLevelComboBox.getSelectedItem();

            tankSizeComboBox.removeAllItems();
            ontologyData.getOrDefault("tankSizes", List.of()).forEach(tankSizeComboBox::addItem);
            if (currentTankSize != null
                    && ontologyData.getOrDefault("tankSizes", List.of()).contains(currentTankSize)) {
                tankSizeComboBox.setSelectedItem(currentTankSize);
            }

            waterTypeComboBox.removeAllItems();
            ontologyData.getOrDefault("waterTypes", List.of()).forEach(waterTypeComboBox::addItem);
            if (currentWaterType != null
                    && ontologyData.getOrDefault("waterTypes", List.of()).contains(currentWaterType)) {
                waterTypeComboBox.setSelectedItem(currentWaterType);
            }

            preferredTemperatureComboBox.removeAllItems();
            ontologyData.getOrDefault("temperatures", List.of()).forEach(preferredTemperatureComboBox::addItem);
            if (currentTemp != null && ontologyData.getOrDefault("temperatures", List.of()).contains(currentTemp)) {
                preferredTemperatureComboBox.setSelectedItem(currentTemp);
            }

            aggressionLevelComboBox.removeAllItems();
            ontologyData.getOrDefault("aggressionLevels", List.of()).forEach(aggressionLevelComboBox::addItem);
            if (currentAggression != null
                    && ontologyData.getOrDefault("aggressionLevels", List.of()).contains(currentAggression)) {
                aggressionLevelComboBox.setSelectedItem(currentAggression);
            }
        });
    }

    /**
     * !Called by the agent to display the recommendations.
     * This method will need a bit of adjustment to present the data better,
     * especially when 'alreadyHaveFish' and 'numFishKinds' are involved.
     */
    public void displayRecommendations(List<Fish> recommendedFish, List<Plant> recommendedPlants) {
        SwingUtilities.invokeLater(() -> {
            recommendationsTextArea.setText("");
            recommendationsTextArea.append("--- Recommended Fish ---\n");
            if (recommendedFish.isEmpty()) {
                recommendationsTextArea.append("No fish found matching your criteria.\n");
            } else {
                recommendedFish.forEach(fish -> {
                    recommendationsTextArea
                            .append(fish.getName() + " (Aggression: " + fish.getAggressionLevel() + ")\n");
                    recommendationsTextArea
                            .append("  Compatible with: " + String.join(", ", fish.getCompatibleWith()) + "\n");
                    if (!fish.getSometimesCompatibleWith().isEmpty()) {
                        recommendationsTextArea.append("  Sometimes compatible with: "
                                + String.join(", ", fish.getSometimesCompatibleWith()) + " (Exercise caution)\n");
                    }
                    if (!fish.getIncompatibleWith().isEmpty()) {
                        recommendationsTextArea.append("  Incompatible with: "
                                + String.join(", ", fish.getIncompatibleWith()) + " (Avoid!)\n");
                    }
                    recommendationsTextArea.append("  Eats: " + String.join(", ", fish.getEats()) + "\n");
                    recommendationsTextArea.append("\n");
                });
            }

            recommendationsTextArea.append("\n--- Recommended Plants ---\n");
            if (recommendedPlants.isEmpty()) {
                recommendationsTextArea.append("No plants found matching your tank size.\n");
            } else {
                recommendedPlants.forEach(plant -> {
                    recommendationsTextArea
                            .append(plant.getName() + " (Recommended Amount: " + plant.getAmount() + ")\n");
                });
            }
        });
    }

    /**
     * !Called by the agent to display an error message.
     */
    public void displayErrorMessage(String message) {
        SwingUtilities.invokeLater(() -> {
            recommendationsTextArea.setText("Error: " + message + "\n");
        });
    }
}