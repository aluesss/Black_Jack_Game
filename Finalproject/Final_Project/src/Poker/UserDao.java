package Poker;

import java.util.Optional;
import java.util.Date;
//import java.text.SimpleDateFormat;

public interface UserDao {
    boolean addUser(User user);
    Optional<User> getUserByUsername(String username);
    Optional<User> validateUser(String username, String password);
    boolean updateUser(User user);
    boolean deleteUser(String username);// 注销账户
    boolean updateSecurityInfo(String username, String securityQuestion, String securityAnswer);
    boolean usernameExists(String username);  // 检查用户名是否被占用
    boolean emailExists(String email);       // 检查邮箱地址是否被占用
    boolean updateScore(String username, int score); // 更新score
    boolean updateLastRewardClaimed(String username, Date date);  // 上次领取签到奖励的时间
    Date getLastRewardClaimed(String username);
}
