package Poker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class RegisterUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField emailField;
    private JTextField securityQuestionField;
    private JTextField securityAnswerField;
    private JButton registerButton;
    private JButton loginButton; 

    private AuthService authService;

    public RegisterUI(AuthService authService) {
        super("User Registration");
        this.authService = authService;  // Initialize the AuthService
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 300);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(8, 2, 10, 6));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        emailField = new JTextField();
        securityQuestionField = new JTextField();
        securityAnswerField = new JTextField();
        registerButton = new JButton("Register");
        loginButton = new JButton("Go to login page");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(new JLabel("Confirm Password:"));
        add(confirmPasswordField);
        add(new JLabel("Email:"));
        add(emailField);
        add(new JLabel("Security question:"));
        add(securityQuestionField);
        add(new JLabel("Answer to security question:"));
        add(securityAnswerField);
        add(new JLabel()); 
        add(registerButton);
        add(new JLabel());  
        add(loginButton);
        
        setVisible(true);
    }

    private void setUpListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToLoginScreen();  
            }
        });
    }

    private void goToLoginScreen() {
        dispose();  // Close the registration window
        new LoginUI(authService).setVisible(true);  // Open the login window
    }
    
    private void performRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String securityQuestion = securityQuestionField.getText();
        String securityAnswer = securityAnswerField.getText();
        
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Password cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Confirm password cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (email.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Email cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (!email.contains("@")) {
            JOptionPane.showMessageDialog(this, "Email is invalid, please enter the right Email", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (securityQuestion.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Security question cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Answer to security question cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Password and confirm password are not the same", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill the security question and its answer", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Initial Bonus Points for User Registration
        int initialScore = 1000;

        try {
            // Password encryption
            String encryptedPassword = authService.encryptPassword(password);
            // Call the register method and pass in the initial points
            boolean success = authService.register(username, encryptedPassword, email, securityQuestion, securityAnswer, initialScore);
            if (success) {
                JOptionPane.showMessageDialog(this, "Successful registration, congratulations on the registration bonus score：" + initialScore);
                dispose(); // Close Registration Window
                new LoginUI(authService).setVisible(true); // Open login window
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed, please try again", "Failure", JOptionPane.ERROR_MESSAGE);
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "Registration failed", JOptionPane.ERROR_MESSAGE);
        }
    }


    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl();  
        AuthService authService = new AuthService(userDao);  

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterUI(authService).setVisible(true);  
            }
        });
    }
}