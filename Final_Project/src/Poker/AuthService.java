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
            throw new IllegalStateException("该用户名已被占用");
        }
        if (userDao.emailExists(email)) {
            throw new IllegalStateException("该邮箱已被占用");
        }
        String encryptedPassword = encryptPassword(password);  // 加密密码
        // 使用参数中的 initialScore 创建用户
        User user = new User(username, encryptedPassword, email, initialScore, securityQuestion, securityAnswer);
        return userDao.addUser(user);
    }


    public boolean updateUserScore(String username, int newScore) {
        return userDao.updateScore(username, newScore);
    }


    public Optional<User> login(String username, String password) {
        // 加密输入的密码
        String encryptedPassword = encryptPassword(password);
        // 验证用户
        return userDao.validateUser(username, encryptedPassword);
    }


    // 根据用户名获取用户信息
    public Optional<User> getUserByUsername(String username) {
        return userDao.getUserByUsername(username);
    }

    // 更新用户密码
    public boolean updateUserPassword(String username, String newPassword) {
        Optional<User> user = getUserByUsername(username);
        if (user.isPresent()) {
            // 加密新密码
            String encryptedPassword = encryptPassword(newPassword);
            user.get().setPassword(encryptedPassword);
            // 更新用户信息
            return userDao.updateUser(user.get());
        }
        return false;
    }

    // 密码加密方式
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
        // 加密输入的密码
        String encryptedPassword = encryptPassword(password);
        // 使用validateUser 方法来验证用户身份
        Optional<User> user = userDao.validateUser(username, encryptedPassword);
        if (user.isPresent() && user.get().getEmail().equals(email)) {
            // 如果用户存在并且电子邮箱匹配，则删除用户
            return userDao.deleteUser(username);
        }
        return false; // 如果验证失败，返回 false
    }
    
    public Optional<User> validateUser(String username, String password) {
        String encryptedPassword = encryptPassword(password);  // 使用加密方法
        return userDao.validateUser(username, encryptedPassword);  // 调用 validateUser 方法
    }

    public boolean canClaimReward(String username) {
        Date lastClaimed = userDao.getLastRewardClaimed(username);
        return lastClaimed == null || !isSameDay(lastClaimed, new Date()); //如果lastClaimed == null则说明该用户是第一次领取签到奖励，可以直接发放签到奖励
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

}
