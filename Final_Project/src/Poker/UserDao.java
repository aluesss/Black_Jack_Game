package Poker;

import java.util.Optional;

public interface UserDao {
    boolean addUser(User user);
    Optional<User> getUserByUsername(String username);
    Optional<User> validateUser(String username, String password);
    boolean updateUser(User user);
    boolean deleteUser(String username);
    boolean updateSecurityInfo(String username, String securityQuestion, String securityAnswer);
    boolean usernameExists(String username);  // 检查用户名是否被占用
    boolean emailExists(String email);       // 检查邮箱地址是否被占用
}
