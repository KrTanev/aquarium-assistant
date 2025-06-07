package uni.fmi.masters.gui;

import uni.fmi.masters.model.SearchHistoryEntry;
import uni.fmi.masters.model.User;
import uni.fmi.masters.repository.SearchHistoryRepository;
import uni.fmi.masters.service.CurrentUser;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class UserProfilePanel extends JPanel {

    private JLabel usernameLabel;
    private JLabel emailLabel;
    private JTextArea historyTextArea;
    private SearchHistoryRepository searchHistoryRepository;

    public UserProfilePanel() {
        this.searchHistoryRepository = new SearchHistoryRepository();
        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20)); // Padding

        JPanel userInfoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        userInfoPanel.setBorder(BorderFactory.createTitledBorder("User Information"));

        usernameLabel = new JLabel("Username: N/A");
        emailLabel = new JLabel("Email: N/A");

        userInfoPanel.add(usernameLabel);
        userInfoPanel.add(emailLabel);

        add(userInfoPanel, BorderLayout.NORTH);

        JPanel historyPanel = new JPanel(new BorderLayout());
        historyPanel.setBorder(BorderFactory.createTitledBorder("Previous Searches"));

        historyTextArea = new JTextArea();
        historyTextArea.setEditable(false);
        historyTextArea.setLineWrap(true);
        historyTextArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(historyTextArea);
        historyPanel.add(scrollPane, BorderLayout.CENTER);

        add(historyPanel, BorderLayout.CENTER);

        this.addAncestorListener(new javax.swing.event.AncestorListener() {
            @Override
            public void ancestorAdded(javax.swing.event.AncestorEvent event) {
                // When the panel is added to a container and becomes visible
                refreshProfileData();
            }

            @Override
            public void ancestorRemoved(javax.swing.event.AncestorEvent event) {
            }

            @Override
            public void ancestorMoved(javax.swing.event.AncestorEvent event) {
            }
        });
    }

    public void refreshProfileData() {
        User currentUser = CurrentUser.getLoggedInUser();
        if (currentUser != null) {
            usernameLabel.setText("Username: " + currentUser.getUsername());
            emailLabel.setText("Email: " + currentUser.getEmail());
            loadSearchHistory(currentUser.getId());
        } else {
            usernameLabel.setText("Username: Not Logged In");
            emailLabel.setText("Email: Not Logged In");
            historyTextArea.setText("Please log in to view your profile and history.");
        }
    }

    private void loadSearchHistory(int userId) {
        List<SearchHistoryEntry> history = searchHistoryRepository.getSearchHistoryByUserId(userId);
        if (history.isEmpty()) {
            historyTextArea.setText("No previous search history found.");
        } else {
            StringBuilder sb = new StringBuilder();
            for (SearchHistoryEntry entry : history) {
                sb.append("------------------------------------\n");
                sb.append(entry.toString());
                sb.append("\n");
            }
            historyTextArea.setText(sb.toString());
        }
    }
}