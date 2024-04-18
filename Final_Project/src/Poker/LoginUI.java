package Poker;

import javax.swing.*;
import java.awt.*;
//import java.awt.event.ActionEvent;
//import java.awt.event.ActionListener;
import java.util.Optional;

public class LoginUI extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton forgotPasswordButton;
    private AuthService authService;

    public LoginUI(AuthService authService) {
        super("用户登录");
        this.authService = authService;
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 200);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(4, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("登录");
        forgotPasswordButton = new JButton("忘记密码？");

        add(new JLabel("用户名:"));
        add(usernameField);
        add(new JLabel("密码:"));
        add(passwordField);
        add(new JLabel());
        add(loginButton);
        add(new JLabel());
        add(forgotPasswordButton);

        setVisible(true);
    }

    private void setUpListeners() {
        loginButton.addActionListener(e -> performLogin(usernameField.getText(), new String(passwordField.getPassword())));
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());
    }

    private void performLogin(String username, String password) {
        String encryptedPassword = authService.encryptPassword(password); // 对密码进行加密
        Optional<User> user = authService.login(username, encryptedPassword); 
        if (user.isPresent()) {
            JOptionPane.showMessageDialog(this, "登录成功", "成功", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "登录失败：用户名或密码不正确", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }


    private void showForgotPasswordDialog() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请输入用户名后再尝试找回密码", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Optional<User> user = authService.getUserByUsername(username);
        if (user.isPresent()) {
            String securityQuestion = user.get().getSecurityQuestion();
            String answer = JOptionPane.showInputDialog(this, "安全问题：" + securityQuestion);
            if (answer != null && answer.equals(user.get().getSecurityAnswer())) {
                String newPassword = JOptionPane.showInputDialog(this, "回答正确，请输入新密码：");
                if (newPassword != null && !newPassword.isEmpty()) {
                    authService.updateUserPassword(username, authService.encryptPassword(newPassword));// 密码加密存储提高安全性
                    JOptionPane.showMessageDialog(this, "密码已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "安全问题答案错误", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "未找到该用户名的用户", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(); 
        AuthService authService = new AuthService(userDao);
        SwingUtilities.invokeLater(() -> new LoginUI(authService));
    }
}
