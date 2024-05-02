package Poker;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class WelcomeUI extends JFrame {
    private User user; // Storing user information
    private AuthService authService; // For processing password changes and updating credits
    private JLabel userInfoLabel; // For real-time display of Score updates

    public WelcomeUI(User user, AuthService authService) {
        super("Welcome");
        this.user = user;
        this.authService = authService;
        initializeUI();
    }
    
    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Blackjack game!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton startGameButton = new JButton("Start Game");
        JButton changePasswordButton = new JButton("Change Password");
        JButton claimRewardButton = new JButton("Claim your daily check-in rewards");
        JButton logoutButton = new JButton("Log out");  
        buttonPanel.add(startGameButton);
        buttonPanel.add(changePasswordButton);
        buttonPanel.add(claimRewardButton);
        buttonPanel.add(logoutButton);  
        add(buttonPanel, BorderLayout.CENTER);

        userInfoLabel = new JLabel("Username: " + user.getUsername() + " | Score: " + user.getScore() + " | Level: " + user.getLevel() + " | Experience: " + user.getExperience() + "/" + 30 * user.getLevel(), JLabel.CENTER);
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(userInfoLabel, BorderLayout.SOUTH);

        // Listeners
        startGameButton.addActionListener(e -> startGame());
        changePasswordButton.addActionListener(e -> changePassword());
        claimRewardButton.addActionListener(e -> claimDailyReward());
        logoutButton.addActionListener(e -> logout());  // The listener for logging out

        setVisible(true);
    }

    private void startGame() {
        this.dispose(); // Close the welcome page
        // [Front-end and back-end interfaces]: Send user information
        SwingUtilities.invokeLater(() -> new PokerClient(user, authService));
    }




    private void changePassword() {
        // Request the user to enter a new password
        String newPassword = JOptionPane.showInputDialog(this, "Please set a new password：");
        
        // Check if the user clicked Cancel or closed the dialog box
        if (newPassword == null) {
            // The user cancels the operation without any processing
            //System.out.println("Password change cancelled.");
            return;
        }

        // Remove spaces before and after the input
        newPassword = newPassword.trim();

        // Make sure the new password is not empty
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Update Password
        authService.updateUserPassword(user.getUsername(), authService.encryptPassword(newPassword));
        JOptionPane.showMessageDialog(this, "Password updated", "Success", JOptionPane.INFORMATION_MESSAGE);
        //System.out.println("Password changed.");
    }


    private void claimDailyReward() {
        Date lastClaimed = authService.getLastRewardClaimed(user.getUsername());
        if (lastClaimed != null && isSameDay(lastClaimed, new Date())) {
            JOptionPane.showMessageDialog(this, "You've already claimed your reward today, please come back tomorrow ╰(￣ω￣ｏ)", "Failed to claim", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newScore = user.getScore() + 200;
        user.setScore(newScore);
        authService.updateUserScore(user.getUsername(), newScore);
        authService.updateLastRewardClaimed(user.getUsername(), new Date());
        JOptionPane.showMessageDialog(this, "Successful check-in increases Score by 200 (^ ~ ^)", "Successful claim", JOptionPane.INFORMATION_MESSAGE);
        userInfoLabel.setText("Username: " + user.getUsername() + " | Score: " + user.getScore() + " | Level: " + user.getLevel() + " | Experience: " + user.getExperience() + "/" + 30 * user.getLevel());
    }
    
    private void logout() {
        dispose();  // Close current window
        
        SwingUtilities.invokeLater(() -> new LoginUI(authService)); // Display the login window
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
