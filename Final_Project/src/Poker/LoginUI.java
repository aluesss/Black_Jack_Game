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
    private AuthService authService;

    public LoginUI(AuthService authService) {
        super("用户登录");
        this.authService = authService;
        initializeUI();
        setUpListeners();
    }

    private void initializeUI() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(5, 2, 10, 10));

        usernameField = new JTextField();
        passwordField = new JPasswordField();
        loginButton = new JButton("登录");
        forgotPasswordButton = new JButton("忘记密码？");
        deleteAccountButton = new JButton("注销用户");

        add(new JLabel("用户名:"));
        add(usernameField);
        add(new JLabel("密码:"));
        add(passwordField);
        add(new JLabel());
        add(loginButton);
        add(new JLabel());
        add(forgotPasswordButton);
        add(new JLabel());
        add(deleteAccountButton);

        setVisible(true);
    }

    private void setUpListeners() {
        loginButton.addActionListener(e -> performLogin(usernameField.getText(), new String(passwordField.getPassword())));
        forgotPasswordButton.addActionListener(e -> showForgotPasswordDialog());
        deleteAccountButton.addActionListener(e -> showDeleteAccountDialog());
    }

    private void performLogin(String username, String password) {
        String encryptedPassword = authService.encryptPassword(password);
        Optional<User> user = authService.login(username, encryptedPassword);
        if (user.isPresent()) {
            // 登录成功后隐藏登录窗口
            setVisible(false);
            dispose();

            // 创建并显示欢迎界面，向欢迎界面传递用户对象和AuthService实例
            SwingUtilities.invokeLater(() -> new WelcomeUI(user.get(), authService));
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
        // 我不喜欢重设密码必须与原密码不一样这一条规定，所以在这里并没有验证重设密码必须与原密码不一样，重设密码与原密码一样也可以重设成一样的，哼！
        Optional<User> user = authService.getUserByUsername(username);
        if (user.isPresent()) {
            String securityQuestion = user.get().getSecurityQuestion();
            String answer = JOptionPane.showInputDialog(this, "安全问题：" + securityQuestion);
            if (answer != null && answer.equals(user.get().getSecurityAnswer())) {
                String newPassword = JOptionPane.showInputDialog(this, "回答正确，请输入新密码：");
                if (newPassword == null) {
                    // 用户点击了取消或关闭了对话框，不进行任何操作
                    return;
                }
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "新密码不能为空", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newPassword != null && !newPassword.isEmpty()) {
                    authService.updateUserPassword(username, authService.encryptPassword(newPassword));// 密码加密存储提高安全性
                    JOptionPane.showMessageDialog(this, "密码已更新", "成功", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "安全问题答案错误或未作答", "错误", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "未找到该用户名的用户", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //注销账户
    private void showDeleteAccountDialog() {
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JTextField emailField = new JTextField();
        Object[] message = {
            "用户名:", usernameField,
            "密码:", passwordField,
            "邮箱:", emailField
        };

        int response = JOptionPane.showConfirmDialog(this, message, "注销账户", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (response == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());
            String email = emailField.getText();
            String encryptedPassword = authService.encryptPassword(password);

            // 添加确认注销的过程，确保不会出现手误注销账户的情况
            if (authService.validateUser(username, encryptedPassword).isPresent() && authService.getUserByUsername(username).get().getEmail().equals(email)) {
                // 用户信息正确，确认是否真的要注销账户
                int confirm = JOptionPane.showConfirmDialog(this, "你确定要注销这个账户吗?", "确认注销", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 只有当用户点击“是”时才调用删除用户的函数，点击×退出或者点击“否”都不会调用
                    if (authService.deleteUser(username, encryptedPassword, email)) {
                        JOptionPane.showMessageDialog(this, "账户已注销", "注销成功", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "注销失败，请重试", "错误", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
            	// 为保护用户信息，不提醒哪一项信息不正确（提醒的话可以试密码）
                JOptionPane.showMessageDialog(this, "账户信息不正确，无法注销", "错误", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(); 
        AuthService authService = new AuthService(userDao);
        SwingUtilities.invokeLater(() -> new LoginUI(authService));
    }
}
