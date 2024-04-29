package Poker;

import java.util.Optional;
import java.util.Date;
//import java.text.SimpleDateFormat;

// Method declarations
public interface UserDao {
    boolean addUser(User user);
    Optional<User> getUserByUsername(String username);
    Optional<User> validateUser(String username, String password);
    boolean updateUser(User user);
    boolean deleteUser(String username);// Delete accounts
    boolean updateSecurityInfo(String username, String securityQuestion, String securityAnswer);
    boolean usernameExists(String username);  // Check if the username is taken
    boolean emailExists(String email);       // Check if the e-mail address is taken
    boolean updateScore(String username, int score); // Update score
    boolean updateLastRewardClaimed(String username, Date date);  // Last time receiving the sign-in bonus
    Date getLastRewardClaimed(String username);
    int getExperience(String username);
    int getLevel(String username);
    boolean updateExperienceAndLevel(String username, int newExperience, int newLevel);
}
