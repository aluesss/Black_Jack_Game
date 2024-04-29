package Poker;

import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.SimpleDateFormat;

public class AuthService {

    private UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public boolean register(String username, String password, String email, String securityQuestion, String securityAnswer, int initialScore) {
        if (userDao.usernameExists(username)) {
            throw new IllegalStateException("This username is already taken");
        }
        if (userDao.emailExists(email)) {
            throw new IllegalStateException("This mailbox is already taken");
        }
        String encryptedPassword = encryptPassword(password);  // Encrypted password
        // Create User
        User user = new User(username, encryptedPassword, email, initialScore, 1, 0, securityQuestion, securityAnswer);
        return userDao.addUser(user);
    }


    public boolean updateUserScore(String username, int newScore) {
        return userDao.updateScore(username, newScore);
    }


    public Optional<User> login(String username, String password) {
        // Encrypt the entered password
        String encryptedPassword = encryptPassword(password);
        // Verify user
        return userDao.validateUser(username, encryptedPassword);
    }


    // Get user information based on user name
    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    // Update user passwords
    public boolean updateUserPassword(String username, String newPassword) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent()) {
            // Encrypt the new password
            String encryptedPassword = encryptPassword(newPassword);
            user.get().setPassword(encryptedPassword);
            // Update user information
            return userDao.updateUser(user.get());
        }
        return false;
    }

    // Password encryption method
    protected String encryptPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Could not hash password", e);
        }
    }
    
    public boolean deleteUser(String username, String password, String email) {
        // Encrypting the entered password
        String encryptedPassword = encryptPassword(password);
        // Use the validateUser method to authenticate a user
        Optional<User> user = userDao.validateUser(username, encryptedPassword);
        if (user.isPresent() && user.get().getEmail().equals(email)) {
            // If the user exists and the e-mail address matches, delete the user
            return userDao.deleteUser(username);
        }
        return false; // Returns false if validation fails
    }
    
    public Optional<User> validateUser(String username, String password) {
        String encryptedPassword = encryptPassword(password);  // Use of encryption
        return userDao.validateUser(username, encryptedPassword);  // Call the validateUser method
    }

    public boolean canClaimReward(String username) {
        Date lastClaimed = userDao.getLastRewardClaimed(username);
        // If lastClaimed == null then this is the first time the user has received a check-in reward and the reward can be issued directly.
        return lastClaimed == null || !isSameDay(lastClaimed, new Date()); 
    }
    
    public boolean isSameDay(Date date1, Date date2) {
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(date1).equals(fmt.format(date2));
    }

    public void recordRewardClaim(String username) {
        userDao.updateLastRewardClaimed(username, new Date());
    }
    
    public Date getLastRewardClaimed(String username) {
        return userDao.getLastRewardClaimed(username);
    }
    
    public void updateLastRewardClaimed(String username, Date date) {
        userDao.updateLastRewardClaimed(username, date);
    }
    
    // Update user experience and levels
    public boolean updateExperienceAndLevel(String username, int gainedExperience) {
        int currentExperience = userDao.getExperience(username); // Get current experience
        int currentLevel = userDao.getLevel(username); // Get current level
        // The new experience value is the current experience plus the newly gained experience
        int newExperience = currentExperience + gainedExperience; 
        int newLevel = currentLevel; 
        //Upgrade Logic
        while (newExperience >= 300) {
            newExperience -= 300; 
            newLevel++; 
        }

        // Update experience and levels in the database
        return userDao.updateExperienceAndLevel(username, newExperience, newLevel);
    }


    // Getting the user's experience
    public int getExperience(String username) {
        return userDao.getExperience(username);
    }

    // Getting the user's level
    public int getLevel(String username) {
        return userDao.getLevel(username);
    }
}
