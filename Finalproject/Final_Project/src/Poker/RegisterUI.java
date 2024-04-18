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

    private AuthService authService;

    public RegisterUI() {
        super("用户注册");
        this.authService = new AuthService(new UserDaoImpl());  // 初始化 AuthService
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(350, 250);
        setLocationRelativeTo(null);  
        setLayout(new GridLayout(7, 2, 10, 5));  

        // Create UI components
        usernameField = new JTextField();
        passwordField = new JPasswordField();
        confirmPasswordField = new JPasswordField();
        emailField = new JTextField();
        securityQuestionField = new JTextField();
        securityAnswerField = new JTextField();
        registerButton = new JButton("注册");

        // Adding components to JFrame
        add(new JLabel("用户名:"));
        add(usernameField);
        add(new JLabel("密码:"));
        add(passwordField);
        add(new JLabel("确认密码:"));
        add(confirmPasswordField);
        add(new JLabel("电子邮件:"));
        add(emailField);
        add(new JLabel("安全问题:"));
        add(securityQuestionField);
        add(new JLabel("问题答案:"));
        add(securityAnswerField);
        add(new JLabel()); // For the button
        add(registerButton);

        setVisible(true);
    }

    private void setUpListeners() {
        registerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                performRegistration();
            }
        });
    }

    private void performRegistration() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String email = emailField.getText();
        String securityQuestion = securityQuestionField.getText();
        String securityAnswer = securityAnswerField.getText();

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "密码和确认密码不匹配", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (securityQuestion.isEmpty() || securityAnswer.isEmpty()) {
            JOptionPane.showMessageDialog(this, "请填写安全问题及其答案", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            boolean success = authService.register(username, password, email, securityQuestion, securityAnswer);
            if (success) {
                JOptionPane.showMessageDialog(this, "注册成功");
                dispose(); // 可选择关闭窗口
            }
        } catch (IllegalStateException e) {
            JOptionPane.showMessageDialog(this, e.getMessage(), "注册错误", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new RegisterUI(); // Run the constructor
            }
        });
    }
}
