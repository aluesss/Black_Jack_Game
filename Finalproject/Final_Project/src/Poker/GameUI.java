package Poker;

import javax.swing.*;
import java.awt.*;

public class GameUI extends JFrame {
    private User user; // 存储当前用户信息

    public GameUI(User user) {
        super("Poker Game");
        this.user = user;
        initializeUI();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //setExtendedState(JFrame.MAXIMIZED_BOTH); 最大化窗口
        setSize(600, 400);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // 创建游戏区域面板
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        JLabel gameArea = new JLabel("Game area", JLabel.CENTER);
        mainPanel.add(gameArea, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // 创建并显示用户信息
        JLabel userInfoLabel = new JLabel("Username: " + user.getUsername() + " | Score: " + user.getScore(), JLabel.CENTER);
        userInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        add(userInfoLabel, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void performGameLogic() {
        // 添加游戏逻辑
        System.out.println("Performing game logic...");
    }

    private void updateGameUI() {
        // 添加更新界面的代码
        System.out.println("Updating game UI...");
    }

    private void handleUserInput() {
        // 添加处理用户输入的代码
        System.out.println("Handling user input...");
    }

    // 测试用main函数
    public static void main(String[] args) {
        User demoUser = new User("Diane", "password", "diane@example.com", 500, "What's your favorite color?", "QAQ");
        SwingUtilities.invokeLater(() -> new GameUI(demoUser));
    }
}
