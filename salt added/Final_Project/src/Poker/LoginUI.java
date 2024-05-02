package Poker;

import javax.swing.*;
import java.awt.*;
import java.util.Optional;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private JButton deleteAccountButton;
    private JButton registerButton; 
    private AuthService authService;

    public LoginUI(AuthService authService) {
        super("User login");
        this.authService = authService;
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(6, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("Login");
        forgotPasswordButton = new JButton("Forgot password?");
        deleteAccountButton = new JButton("Delete your account");
        registerButton = new JButton("Create account");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel());
        add(loginButton);
        add(new JLabel());
        add(forgotPasswordButton);
        add(new JLabel());
        add(deleteAccountButton);
        add(new JLabel());
        add(registerButton); 

        setVisible(true);
    }

    private void setUpListeners() {
        loginButton.addActionListener(e -> performLogin(usernameField.getText(), new String(passwordField.getPassword())));
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());
        deleteAccountButton.addActionListener(e -> showDeleteAccountDialog());
        registerButton.addActionListener(e -> {
            dispose(); // Close Login Window
            new RegisterUI(authService).setVisible(true);
        });
    }

    private void performLogin(String username, String password) {
        String encryptedPassword = authService.encryptPassword(password);
        Optional<User> user = authService.login(username, encryptedPassword);
        if (user.isPresent()) {
            
            setVisible(false);
            dispose();

            // Create and display the welcome screen, passing the user object and AuthService instance to the welcome screen
            SwingUtilities.invokeLater(() -> new WelcomeUI(user.get(), authService));
        } else {
            JOptionPane.showMessageDialog(this, "Login Failure: Incorrect user name or password.", "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void showForgotPasswordDialog() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username before trying to retrieve your password.", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // When you reset your password, you can also set it to the same password as the original one.
        Optional<User> user = authService.getUserByUsername(username);
        if (user.isPresent()) {
            String securityQuestion = user.get().getSecurityQuestion();
            String answer = JOptionPane.showInputDialog(this, "Security question：" + securityQuestion);
            if (answer != null && answer.equals(user.get().getSecurityAnswer())) {
                String newPassword = JOptionPane.showInputDialog(this, "The answer is correct, please set a new password：");
                if (newPassword == null) {
                    // The user clicks Cancel or closes the dialog box ,taking no action
                    return;
                }
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "The new password cannot be empty.", "Failure", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newPassword != null && !newPassword.isEmpty()) {
                    authService.updateUserPassword(username, authService.encryptPassword(newPassword));// Password encrypted storage for increased security
                    JOptionPane.showMessageDialog(this, "Password updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect answer or didn't answered to the security question.", "Failure", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No users found for this username.", "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Deletion of account
    private void showDeleteAccountDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        Object[] message = {
            "Username:", usernameField,
            "Password:", passwordField,
            "Email:", emailField
        };

        int response = JOptionPane.showConfirmDialog(this, message, "Deletion of account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String encryptedPassword = authService.encryptPassword(password);

            // Add confirmation of the logout process to ensure that no accounts are logged out by mistake
            if (authService.validateUser(username, encryptedPassword).isPresent() && authService.getUserByUsername(username).get().getEmail().equals(email)) {
                // User information is correct, confirm that you really want to cancel the account
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this account?", "Confirmation of deletion", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // The function to delete a user is only called when the user clicks "Yes", it is not called when the user clicks x to exit or clicks "No".
                    if (authService.deleteUser(username, encryptedPassword, email)) {
                        JOptionPane.showMessageDialog(this, "Account deleted", "Successful deletion", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to delete the account, please try again", "Failure", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
            	// To protect user information, no reminder of which information is incorrect (you can try the password if reminded)
                JOptionPane.showMessageDialog(this, "Account information is incorrect and the account deletion failed, please try again", "Failure", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(); 
        AuthService authService = new AuthService(userDao);
        SwingUtilities.invokeLater(() -> new LoginUI(authService));
    }
}
