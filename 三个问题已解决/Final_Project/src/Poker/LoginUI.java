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
        deleteAccountButton = new JButton("Deregister your account");
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
            dispose(); // 关闭登录窗口
            new RegisterUI(authService).setVisible(true); // 打开注册窗口，确保authService已正确定义并传递
        });
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
            JOptionPane.showMessageDialog(this, "Login Failure: Incorrect user name or password.", "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void showForgotPasswordDialog() {
        String username = usernameField.getText();
        if (username.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter your username before trying to retrieve your password.", "Failure", JOptionPane.ERROR_MESSAGE);
            return;
        }
        // 我不喜欢重设密码必须与原密码不一样这一条规定，所以在这里并没有验证重设密码必须与原密码不一样，重设密码与原密码一样也可以重设成一样的，哼！
        Optional<User> user = authService.getUserByUsername(username);
        if (user.isPresent()) {
            String securityQuestion = user.get().getSecurityQuestion();
            String answer = JOptionPane.showInputDialog(this, "Security question：" + securityQuestion);
            if (answer != null && answer.equals(user.get().getSecurityAnswer())) {
                String newPassword = JOptionPane.showInputDialog(this, "The answer is correct, please set a new password：");
                if (newPassword == null) {
                    // 用户点击了取消或关闭了对话框，不进行任何操作
                    return;
                }
                if (newPassword.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "The new password cannot be empty.", "Failure", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                if (newPassword != null && !newPassword.isEmpty()) {
                    authService.updateUserPassword(username, authService.encryptPassword(newPassword));// 密码加密存储提高安全性
                    JOptionPane.showMessageDialog(this, "Password updated", "Success", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Incorrect answer or unanswered to the security question.", "Failure", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "No users found for this username.", "Failure", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    //注销账户
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

            // 添加确认注销的过程，确保不会出现手误注销账户的情况
            if (authService.validateUser(username, encryptedPassword).isPresent() && authService.getUserByUsername(username).get().getEmail().equals(email)) {
                // 用户信息正确，确认是否真的要注销账户
                int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to deregister this account?", "Confirmation of deregistration", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 只有当用户点击“是”时才调用删除用户的函数，点击×退出或者点击“否”都不会调用
                    if (authService.deleteUser(username, encryptedPassword, email)) {
                        JOptionPane.showMessageDialog(this, "Account deleted", "Successful deregistration", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to deregister, please try again", "Failure", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
            	// 为保护用户信息，不提醒哪一项信息不正确（提醒的话可以试密码）
                JOptionPane.showMessageDialog(this, "Account information is incorrect and cannot be canceled", "Failure", JOptionPane.ERROR_MESSAGE);
            }
        }
    }


    public static void main(String[] args) {
        UserDao userDao = new UserDaoImpl(); 
        AuthService authService = new AuthService(userDao);
        SwingUtilities.invokeLater(() -> new LoginUI(authService));
    }
}
