package uni.fmi.masters.gui;

import jade.core.AID;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import uni.fmi.masters.agent.AquariumAdviserAgent;

import com.fasterxml.jackson.databind.ObjectMapper;
import javax.swing.*;
import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

public class AdminPanel extends JPanel {

    private AquariumAdviserAgent mainAgent;
    private ObjectMapper mapper;

    // ! Components for Admin Screen
    private JTextField adminFishNameField;
    private JComboBox<String> adminFishAggressionComboBox;
    private JTextField adminFishCompatibleField;
    private JTextField adminFishSometimesCompatibleField;
    private JTextField adminFishIncompatibleField;
    private JTextField adminFishEatsField;
    private JButton addFishButton;

    private JTextField adminPlantNameField;
    private JSpinner adminPlantAmountSpinner;
    private JButton addPlantButton;

    // ! Dropdown fields for properties of new fish/plant
    private JComboBox<String> fishTempComboBox;
    private JComboBox<String> fishTankSizeComboBox;
    private JComboBox<String> fishWaterTypeComboBox;

    private JComboBox<String> plantTempComboBox;
    private JComboBox<String> plantTankSizeComboBox;
    private JComboBox<String> plantWaterTypeComboBox;

    public AdminPanel(AquariumAdviserAgent agent) {
        this.mainAgent = agent;
        this.mapper = new ObjectMapper();
        initAdminPanelUI();
        populateAdminDropdowns();
    }

    private void initAdminPanelUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createTitledBorder("Admin Panel: Add/Manage Data"));

        JTabbedPane adminTabbedPane = new JTabbedPane();

        // --- Add Fish Panel ---
        JPanel addFishPanel = new JPanel(new GridBagLayout());
        addFishPanel.setBorder(BorderFactory.createTitledBorder("Add New Fish"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        int row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Fish Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishNameField = new JTextField(20);
        addFishPanel.add(adminFishNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Aggression Level:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishAggressionComboBox = new JComboBox<>();
        addFishPanel.add(adminFishAggressionComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Temperature Range:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        fishTempComboBox = new JComboBox<>();
        addFishPanel.add(fishTempComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Minimum Tank Size:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        fishTankSizeComboBox = new JComboBox<>();
        addFishPanel.add(fishTankSizeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Water Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        fishWaterTypeComboBox = new JComboBox<>();
        addFishPanel.add(fishWaterTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Compatible With (comma-separated):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishCompatibleField = new JTextField(20);
        adminFishCompatibleField.setToolTipText("e.g., Guppy, Molly");
        addFishPanel.add(adminFishCompatibleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Sometimes Compatible With (comma-separated):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishSometimesCompatibleField = new JTextField(20);
        adminFishSometimesCompatibleField.setToolTipText("e.g., Angelfish, Dwarf Gourami");
        addFishPanel.add(adminFishSometimesCompatibleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Incompatible With (comma-separated):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishIncompatibleField = new JTextField(20);
        adminFishIncompatibleField.setToolTipText("e.g., Betta, Piranha");
        addFishPanel.add(adminFishIncompatibleField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addFishPanel.add(new JLabel("Eats (comma-separated):"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminFishEatsField = new JTextField(20);
        adminFishEatsField.setToolTipText("e.g., Flakes, Pellets");
        addFishPanel.add(adminFishEatsField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        addFishButton = new JButton("Add Fish to Ontology");
        addFishButton.addActionListener(e -> addFish());
        addFishPanel.add(addFishButton, gbc);

        adminTabbedPane.addTab("Add Fish", addFishPanel);

        // --- Add Plant Panel ---
        JPanel addPlantPanel = new JPanel(new GridBagLayout());
        addPlantPanel.setBorder(BorderFactory.createTitledBorder("Add New Plant"));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        row = 0;

        gbc.gridx = 0;
        gbc.gridy = row;
        addPlantPanel.add(new JLabel("Plant Name:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        adminPlantNameField = new JTextField(20);
        addPlantPanel.add(adminPlantNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addPlantPanel.add(new JLabel("Temperature Range:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        plantTempComboBox = new JComboBox<>();
        addPlantPanel.add(plantTempComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addPlantPanel.add(new JLabel("Minimum Tank Size:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        plantTankSizeComboBox = new JComboBox<>();
        addPlantPanel.add(plantTankSizeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        addPlantPanel.add(new JLabel("Water Type:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        plantWaterTypeComboBox = new JComboBox<>();
        addPlantPanel.add(plantWaterTypeComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        adminPlantAmountSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        addPlantPanel.add(new JLabel("Recommended Amount:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = row++;
        addPlantPanel.add(adminPlantAmountSpinner, gbc);

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;
        addPlantButton = new JButton("Add Plant to Ontology");
        addPlantButton.addActionListener(e -> addPlant());
        addPlantPanel.add(addPlantButton, gbc);

        adminTabbedPane.addTab("Add Plant", addPlantPanel);

        add(adminTabbedPane, BorderLayout.CENTER);
    }

    private void populateAdminDropdowns() {

        List<String> tankSizes = mainAgent.getOntology().getAllTankSizeRanges();
        List<String> waterTypes = mainAgent.getOntology().getAllWaterTypes();
        List<String> temperatures = mainAgent.getOntology().getAllWaterTemperatures();
        List<String> aggressionLevels = mainAgent.getOntology().getAllAggressionLevels();

        // !Populate fish
        fishTempComboBox.removeAllItems();
        temperatures.forEach(fishTempComboBox::addItem);

        fishTankSizeComboBox.removeAllItems();
        tankSizes.forEach(fishTankSizeComboBox::addItem);

        fishWaterTypeComboBox.removeAllItems();
        waterTypes.forEach(fishWaterTypeComboBox::addItem);

        adminFishAggressionComboBox.removeAllItems();
        aggressionLevels.forEach(adminFishAggressionComboBox::addItem);

        // ! Populate plant dropdowns
        plantTempComboBox.removeAllItems();
        temperatures.forEach(plantTempComboBox::addItem);

        plantTankSizeComboBox.removeAllItems();
        tankSizes.forEach(plantTankSizeComboBox::addItem);

        plantWaterTypeComboBox.removeAllItems();
        waterTypes.forEach(plantWaterTypeComboBox::addItem);

        if (!temperatures.isEmpty()) {
            fishTempComboBox.setSelectedIndex(0);
            plantTempComboBox.setSelectedIndex(0);
        }
        if (!tankSizes.isEmpty()) {
            fishTankSizeComboBox.setSelectedIndex(0);
            plantTankSizeComboBox.setSelectedIndex(0);
        }
        if (!waterTypes.isEmpty()) {
            fishWaterTypeComboBox.setSelectedIndex(0);
            plantWaterTypeComboBox.setSelectedIndex(0);
        }
        if (!aggressionLevels.isEmpty()) {
            adminFishAggressionComboBox.setSelectedIndex(0);
        }
    }

    private void addFish() {
        String name = adminFishNameField.getText().trim();
        String aggression = (String) adminFishAggressionComboBox.getSelectedItem(); // Get from ComboBox
        String temperature = (String) fishTempComboBox.getSelectedItem();
        String tankSize = (String) fishTankSizeComboBox.getSelectedItem();
        String waterType = (String) fishWaterTypeComboBox.getSelectedItem();

        List<String> compatible = Arrays.stream(adminFishCompatibleField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> sometimesCompatible = Arrays.stream(adminFishSometimesCompatibleField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> incompatible = Arrays.stream(adminFishIncompatibleField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        List<String> eats = Arrays.stream(adminFishEatsField.getText().split(","))
                .map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());

        if (name.isEmpty() || aggression == null || temperature == null || tankSize == null || waterType == null) {
            JOptionPane.showMessageDialog(this,
                    "All basic fish fields (Name, Aggression, Temperature, Tank Size, Water Type) cannot be empty.",
                    "Input Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> content = new HashMap<>();
        content.put("action", "addFish");
        content.put("name", name);
        content.put("temperature", temperature);
        content.put("aggression", aggression);
        content.put("tankSize", tankSize);
        content.put("waterType", waterType);
        content.put("compatibleWith", compatible);
        content.put("sometimesCompatibleWith", sometimesCompatible);
        content.put("incompatibleWith", incompatible);
        content.put("eats", eats);

        sendMessageToCuratorAgent(content, "addFish");
    }

    private void addPlant() {
        String name = adminPlantNameField.getText().trim();
        int amount = (Integer) adminPlantAmountSpinner.getValue();
        String temperature = (String) plantTempComboBox.getSelectedItem();
        String tankSize = (String) plantTankSizeComboBox.getSelectedItem();
        String waterType = (String) plantWaterTypeComboBox.getSelectedItem();

        if (name.isEmpty() || temperature == null || tankSize == null || waterType == null) {
            JOptionPane.showMessageDialog(this,
                    "All basic plant fields (Name, Temperature, Tank Size, Water Type) cannot be empty.", "Input Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        Map<String, Object> content = new HashMap<>();
        content.put("action", "addPlant");
        content.put("name", name);
        content.put("temperature", temperature);
        content.put("tankSize", tankSize);
        content.put("waterType", waterType);
        content.put("amount", amount);

        sendMessageToCuratorAgent(content, "addPlant");
    }

    private void sendMessageToCuratorAgent(Map<String, Object> messageContent, String actionType) {
        try {
            String jsonContent = mapper.writeValueAsString(messageContent);

            ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);

            msg.addReceiver(new AID("CuratorAgent", AID.ISLOCALNAME));
            msg.setConversationId("ontology-update");
            msg.setReplyWith("req_" + System.currentTimeMillis());
            msg.setContent(jsonContent);

            AtomicBoolean success = new AtomicBoolean(false);

            mainAgent.addBehaviour(new AchieveREInitiator(mainAgent, msg) {
                @Override
                protected void handleInform(ACLMessage inform) {
                    System.out.println("CuratorAgent replied: " + inform.getContent());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(AdminPanel.this,
                                actionType + " successful: " + inform.getContent(), "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        clearFields(actionType);
                        populateAdminDropdowns();
                    });
                    success.set(true);
                }

                @Override
                protected void handleFailure(ACLMessage failure) {
                    System.err.println("CuratorAgent failed: " + failure.getContent());
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(AdminPanel.this, actionType + " failed: " + failure.getContent(),
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                    success.set(false);
                }

                @Override
                protected void handleAllResultNotifications(Vector notifications) {
                    if (!success.get()) {
                        SwingUtilities.invokeLater(() -> {
                            JOptionPane.showMessageDialog(AdminPanel.this,
                                    "No response from CuratorAgent. Please check the agent logs.",
                                    "Communication Error", JOptionPane.ERROR_MESSAGE);
                        });
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error sending message to CuratorAgent: " + e.getMessage(),
                    "Communication Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void clearFields(String actionType) {
        if ("addFish".equals(actionType)) {
            adminFishNameField.setText("");
            adminFishAggressionComboBox.setSelectedIndex(0);
            fishTempComboBox.setSelectedIndex(0);
            fishTankSizeComboBox.setSelectedIndex(0);
            fishWaterTypeComboBox.setSelectedIndex(0);
            adminFishCompatibleField.setText("");
            adminFishSometimesCompatibleField.setText("");
            adminFishIncompatibleField.setText("");
            adminFishEatsField.setText("");
        } else if ("addPlant".equals(actionType)) {
            adminPlantNameField.setText("");
            adminPlantAmountSpinner.setValue(1);
            plantTempComboBox.setSelectedIndex(0);
            plantTankSizeComboBox.setSelectedIndex(0);
            plantWaterTypeComboBox.setSelectedIndex(0);
        }
    }
}