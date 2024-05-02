package Poker;

import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.security.SecureRandom;


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
        String salt = generateSalt();
        String encryptedPassword = encryptPassword(password + salt);  // Append salt to password before the encrypt process
        User user = new User(username, encryptedPassword, salt, email, initialScore, 1, 0, securityQuestion, securityAnswer);
        return userDao.addUser(user);
    }

    // Generate a five-digit salt
    private String generateSalt() {
        SecureRandom random = new SecureRandom();
        int num = 10000 + random.nextInt(90000);
        return String.valueOf(num);
    }

    
    public boolean updateUserScore(String username, int newScore) {
        return userDao.updateScore(username, newScore);
    }


    public Optional<User> login(String username, String password) {
        // Get the salt of user
        Optional<User> user = userDao.getUserByUsername(username);
        if (user.isPresent()) {
            // Salt the provided password and use encryptPassword to hash it
            String saltedAndHashedPassword = encryptPassword(password + user.get().getSalt());
            // Compare the hashed password with the stored one in the database
            if (saltedAndHashedPassword.equals(user.get().getPassword())) {
                return user;
            }
        }
        return Optional.empty();
    }


    // Get user information
    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    // Update user's password
    public boolean updateUserPassword(String username, String newPassword) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent()) {
            // Use the existing salt of user (Not changing the salt when changing the password)
            String existingSalt = user.get().getSalt();
            // Salt new password and use encryptPassword to hash it
            String encryptedPassword = encryptPassword(newPassword + existingSalt);
            // Set the new password
            user.get().setPassword(encryptedPassword);
            return userDao.updateUser(user.get());
        }
        return false;
    }

    // Password encryption
    protected String encryptPassword(String passwordWithSalt) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(passwordWithSalt.getBytes());
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
        // Get the salt of user
        Optional<User> userOpt = userDao.getUserByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            // Salt the password and use encryptPassword to hash it
            String saltedAndHashedPassword = encryptPassword(password + user.getSalt());
            // Check whether the hashed password and email match their counterparts stored in the database
            if (saltedAndHashedPassword.equals(user.getPassword()) && user.getEmail().equals(email)) {
                // If all matches, delete the user
                return userDao.deleteUser(username);
            }
        }
        return false;  // Returns false if the condition is not met
    }


    
    public Optional<User> validateUser(String username, String password) {
        return login(username, password);
    }

    public boolean canClaimReward(String username) {
        Date lastClaimed = userDao.getLastRewardClaimed(username);
        // If lastClaimed == null then this is the first time the user has received a check-in reward and the reward can be issued directly.
        return lastClaimed == null || !isSameDay(lastClaimed, new Date()); 
    }
    
    // Determine if it's the same day
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
        // Upgrade Logic: The amount of experience needed to upgrade to the next level is the value of the current level * 30
        while (newExperience >= newLevel * 30) {
            newExperience -= newLevel * 30; 
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
