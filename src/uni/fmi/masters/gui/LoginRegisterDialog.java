package uni.fmi.masters.gui;

import uni.fmi.masters.model.User;
import uni.fmi.masters.service.AuthService;
import uni.fmi.masters.service.CurrentUser; // Ensure this is imported

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

public class LoginRegisterDialog extends JDialog {

    private final AuthService authService;
    private User authenticatedUser;

    private JTextField loginUsernameField;
    private JPasswordField loginPasswordField;

    private JTextField registerUsernameField;
    private JTextField registerEmailField;
    private JPasswordField registerPasswordField;
    private JPasswordField registerConfirmPasswordField;

    private JButton loginButton;
    private JButton registerButton;
    private JButton showRegisterPanelButton;
    private JButton showLoginPanelButton;

    private JPanel cardPanel;
    private CardLayout cardLayout;

    public LoginRegisterDialog(Frame owner) {
        super(owner, "Login / Register", true);
        this.authService = new AuthService();
        this.authenticatedUser = null;

        initUI();
        pack();
        setLocationRelativeTo(owner);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (authenticatedUser == null) {
                    System.exit(0);
                } else {
                    dispose();
                }
            }
        });
    }

    private void initUI() {
        setLayout(new BorderLayout());

        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);

        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        loginPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        loginUsernameField = new JTextField(20);
        loginPanel.add(loginUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        loginPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        loginPasswordField = new JPasswordField(20);
        loginPanel.add(loginPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 2;
        loginButton = new JButton("Login");
        loginPanel.add(loginButton, gbc);
        loginButton.addActionListener(e -> performLogin());

        gbc.gridx = 1;
        gbc.gridy = 3;
        showRegisterPanelButton = new JButton("Don't have an account? Register");
        loginPanel.add(showRegisterPanelButton, gbc);
        showRegisterPanelButton.addActionListener(e -> cardLayout.show(cardPanel, "Register"));

        cardPanel.add(loginPanel, "Login");

        JPanel registerPanel = new JPanel(new GridBagLayout());
        registerPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        registerPanel.add(new JLabel("Username:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 0;
        registerUsernameField = new JTextField(20);
        registerPanel.add(registerUsernameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        registerPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 1;
        registerEmailField = new JTextField(20);
        registerPanel.add(registerEmailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        registerPanel.add(new JLabel("Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 2;
        registerPasswordField = new JPasswordField(20);
        registerPanel.add(registerPasswordField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        registerPanel.add(new JLabel("Confirm Password:"), gbc);
        gbc.gridx = 1;
        gbc.gridy = 3;
        registerConfirmPasswordField = new JPasswordField(20);
        registerPanel.add(registerConfirmPasswordField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        registerButton = new JButton("Register");
        registerPanel.add(registerButton, gbc);
        registerButton.addActionListener(e -> performRegistration());

        gbc.gridx = 1;
        gbc.gridy = 5;
        showLoginPanelButton = new JButton("Already have an account? Login");
        registerPanel.add(showLoginPanelButton, gbc);
        showLoginPanelButton.addActionListener(e -> cardLayout.show(cardPanel, "Login"));

        cardPanel.add(registerPanel, "Register");

        add(cardPanel, BorderLayout.CENTER);
        cardLayout.show(cardPanel, "Login");
    }

    private void performLogin() {
        String username = loginUsernameField.getText().trim();
        String password = new String(loginPasswordField.getPassword());

        Optional<User> userOptional = authService.loginUser(username, password);
        if (userOptional.isPresent()) {
            authenticatedUser = userOptional.get();
            CurrentUser.setLoggedInUser(authenticatedUser);
            JOptionPane.showMessageDialog(this, "Login successful!", "Success", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Invalid username or password.", "Login Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void performRegistration() {
        String username = registerUsernameField.getText().trim();
        String email = registerEmailField.getText().trim();
        String password = new String(registerPasswordField.getPassword());
        String confirmPassword = new String(registerConfirmPasswordField.getPassword());

        if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields are required.", "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match.", "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (authService.registerUser(username, email, password)) {
            JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.", "Success",
                    JOptionPane.INFORMATION_MESSAGE);
            cardLayout.show(cardPanel, "Login");

            loginUsernameField.setText(username);
            loginPasswordField.setText("");
            registerUsernameField.setText("");
            registerEmailField.setText("");
            registerPasswordField.setText("");
            registerConfirmPasswordField.setText("");
        } else {
            JOptionPane.showMessageDialog(this,
                    "Registration failed. Username or email might be taken, or invalid input.", "Registration Failed",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public User getAuthenticatedUser() {
        return authenticatedUser;
    }
}