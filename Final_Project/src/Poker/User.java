package Poker;

public class User {
    private String username;
    private String password; // Stored as encrypted
    private String salt; // Used in password encryption process for the security of the password
    private String email;
    private int score; // User's game points
    private int level; // User's level
    private int experience; // User's experience
    private String securityQuestion;
    private String securityAnswer;

    // Default constructor
    public User() {
    }

    // Constructor
    public User(String username, String password, String salt, String email, int score, int level, int experience, String securityQuestion, String securityAnswer) {
        this.username = username;
        this.password = password;
        this.salt = salt;
        this.email = email;
        this.score = score;
        this.level = level;
        this.experience = experience;
        this.securityQuestion = securityQuestion;
        this.securityAnswer = securityAnswer;
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getExperience() {
        return experience;
    }

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public String getSecurityQuestion() {
        return securityQuestion;
    }

    public void setSecurityQuestion(String securityQuestion) {
        this.securityQuestion = securityQuestion;
    }

    public String getSecurityAnswer() {
        return securityAnswer;
    }

    public void setSecurityAnswer(String securityAnswer) {
        this.securityAnswer = securityAnswer;
    }

    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", salt='" + salt + '\'' +
                ", email='" + email + '\'' +
                ", score=" + score +
                ", level=" + level +
                ", experience=" + experience +
                ", securityQuestion='" + securityQuestion + '\'' +
                ", securityAnswer='[PROTECTED]'" +
                '}';
    }
}
