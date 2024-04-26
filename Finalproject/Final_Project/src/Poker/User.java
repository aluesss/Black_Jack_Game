package Poker;

public class User {
    private String username;
    private String password; // 存储加密后的密码
    private String email;
    private int score; // 用户的游戏积分
    private String securityQuestion;
    private String securityAnswer;

    // 默认构造函数
    public User() {
    }

    // 含有所有参数的构造函数
    public User(String username, String password, String email, int score, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.score = score;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    // 获取用户名
    public String getUsername() {
        return username;
    }

    // 设置用户名
    public void setUsername(String username) {
        this.username = username;
    }

    // 获取密码
    public String getPassword() {
        return password;
    }

    // 设置密码
    public void setPassword(String password) {
        this.password = password;
    }

    // 获取电子邮件地址
    public String getEmail() {
        return email;
    }

    // 设置电子邮件地址
    public void setEmail(String email) {
        this.email = email;
    }

    // 获取用户游戏积分
    public int getScore() {
        return score;
    }

    // 设置用户游戏积分
    public void setScore(int score) {
        this.score = score;
    }

    // 获取安全问题
    public String getSecurityQuestion() {
        return securityQuestion;
    }

    // 设置安全问题
    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    // 获取安全问题答案
    public String getSecurityAnswer() {
        return securityAnswer;
    }

    // 设置安全问题答案
    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", email='" + email + '\'' +
                ", score=" + score +
                ", securityQuestion='" + securityQuestion + '\'' +
                ", securityAnswer='[PROTECTED]'" +
                '}';
    }
}
