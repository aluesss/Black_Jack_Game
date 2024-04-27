package Poker;

import javax.swing.*;
import java.awt.*;
import java.util.Date;
import java.text.SimpleDateFormat;

public class WelcomeUI extends JFrame {
    private User user; // 存储用户信息
    private AuthService authService; // 用于处理密码更改和更新积分
    private JLabel userInfoLabel; // 用于实时显示Score更新

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

        userInfoLabel = new JLabel("Username: " + user.getUsername() + " | Score: " + user.getScore(), JLabel.CENTER);
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(userInfoLabel, BorderLayout.SOUTH);

        // Event Listeners
        startGameButton.addActionListener(e -> startGame());
        changePasswordButton.addActionListener(e -> changePassword());
        claimRewardButton.addActionListener(e -> claimDailyReward());
        logoutButton.addActionListener(e -> logout());  // 设置退出登录的监听器

        setVisible(true);
    }

    private void startGame() {
        this.dispose(); // 关闭欢迎界面
        // [前后端接口]: 发送用户信息
        SwingUtilities.invokeLater(() -> new PokerClient(user, authService));
    }




    private void changePassword() {
        // 请求用户输入新密码
        String newPassword = JOptionPane.showInputDialog(this, "Please set a new password：");
        
        // 检查是否点击了取消或关闭了对话框
        if (newPassword == null) {
            // 用户取消操作，不进行任何处理
            //System.out.println("Password change cancelled.");
            return;
        }

        // 去除输入的前后空格
        newPassword = newPassword.trim();

        // 确保新密码不为空
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "New password cannot be empty", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 更新密码操作
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

        int newScore = user.getScore() + 20;
        user.setScore(newScore);
        authService.updateUserScore(user.getUsername(), newScore);
        authService.updateLastRewardClaimed(user.getUsername(), new Date());
        JOptionPane.showMessageDialog(this, "Successful check-in increases Score by 20 (^ ~ ^)", "Successful claim", JOptionPane.INFORMATION_MESSAGE);
        userInfoLabel.setText("Username: " + user.getUsername() + " | Score: " + user.getScore());
    }

    private void logout() {
        dispose();  // 关闭当前窗口
        
        SwingUtilities.invokeLater(() -> new LoginUI(authService)); // 重新显示登录界面
    }

    private boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }
}
