package Poker;

import java.util.Optional;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class AuthService {

    private UserDao userDao;

    public AuthService(UserDao userDao) {
        this.userDao = userDao;
    }
    
    public boolean register(String username, String password, String email, String securityQuestion, String securityAnswer) {
        if (userDao.usernameExists(username)) {
            throw new IllegalStateException("该用户名已被占用");
        }
        if (userDao.emailExists(email)) {
            throw new IllegalStateException("该邮箱已被占用");
        }
        String encryptedPassword = encryptPassword(password);
        User user = new User(username, encryptedPassword, email, 0, securityQuestion, securityAnswer);
        return userDao.addUser(user);
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
}
