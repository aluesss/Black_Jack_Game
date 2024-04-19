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
        super("欢迎界面");
        this.user = user;
        this.authService = authService;
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JLabel welcomeLabel = new JLabel("Welcome to Texas Hold'em Poker Game!", JLabel.CENTER);
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        add(welcomeLabel, BorderLayout.NORTH);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        JButton startGameButton = new JButton("开始游戏");
        JButton changePasswordButton = new JButton("更改密码");
        JButton claimRewardButton = new JButton("领取签到奖励");
        JButton logoutButton = new JButton("退出登录");  
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
        System.out.println("Starting game...");
        this.dispose(); // 关闭欢迎界面

        // 创建游戏界面，向GameUI传递用户对象
        SwingUtilities.invokeLater(() -> new GameUI(user));
    }


    private void changePassword() {
        // 请求用户输入新密码
        String newPassword = JOptionPane.showInputDialog(this, "请输入新密码：");
        
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
            JOptionPane.showMessageDialog(this, "新密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 更新密码操作
        authService.updateUserPassword(user.getUsername(), authService.encryptPassword(newPassword));
        JOptionPane.showMessageDialog(this, "密码已成功更改", "更改成功", JOptionPane.INFORMATION_MESSAGE);
        //System.out.println("Password changed.");
    }


    private void claimDailyReward() {
        Date lastClaimed = authService.getLastRewardClaimed(user.getUsername());
        if (lastClaimed != null && isSameDay(lastClaimed, new Date())) {
            JOptionPane.showMessageDialog(this, "今天已经领取过奖励了，请明天再来哦 ╰(￣ω￣ｏ)", "领取失败", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int newScore = user.getScore() + 20;
        user.setScore(newScore);
        authService.updateUserScore(user.getUsername(), newScore);
        authService.updateLastRewardClaimed(user.getUsername(), new Date());
        JOptionPane.showMessageDialog(this, "签到成功，Score增加20！", "领取成功", JOptionPane.INFORMATION_MESSAGE);
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
