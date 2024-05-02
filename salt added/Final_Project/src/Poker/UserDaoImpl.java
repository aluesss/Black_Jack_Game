package Poker;

import java.sql.*;
import java.util.Optional;
import java.util.Date;

public class UserDaoImpl implements UserDao {
    
    private static final String DB_URL = "jdbc:mysql://localhost:3306/pokerdb"; // Connecting to MySQL Database
    private static final String USER = "root"; // MySQL database user name
    private static final String PASS = "1768"; // The password for this username in the MySQL database, in this case is 1768.

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, USER, PASS);
    }

    @Override
    public boolean addUser(User user) {
        String sql = "INSERT INTO users (username, password, salt, email, score, security_question, security_answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPassword());
            pstmt.setString(3, user.getSalt());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getScore());
            pstmt.setString(6, user.getSecurityQuestion());
            pstmt.setString(7, user.getSecurityAnswer());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get user information by username
    @Override
    public Optional<User> getUserByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new User(
                    rs.getString("username"),
                    rs.getString("password"),
                    rs.getString("salt"),
                    rs.getString("email"),
                    rs.getInt("score"),
                    rs.getInt("level"),
                    rs.getInt("experience"),
                    rs.getString("security_question"),
                    rs.getString("security_answer")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

    @Override
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET password = ?, salt = ?, email = ?, score = ?, security_question = ?, security_answer = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, user.getPassword());
            pstmt.setString(2, user.getSalt());
            pstmt.setString(3, user.getEmail());
            pstmt.setInt(4, user.getScore());
            pstmt.setString(5, user.getSecurityQuestion());
            pstmt.setString(6, user.getSecurityAnswer());
            pstmt.setString(7, user.getUsername());
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean deleteUser(String username) {
        String sql = "DELETE FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateSecurityInfo(String username, String securityQuestion, String securityAnswer) {
        String sql = "UPDATE users SET security_question = ?, security_answer = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, securityQuestion);
            pstmt.setString(2, securityAnswer);
            pstmt.setString(3, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Determine if the username is already taken
    @Override
    public boolean usernameExists(String username) {
        String sql = "SELECT 1 FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean updateScore(String username, int score) {
        String sql = "UPDATE users SET score = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, score);
            stmt.setString(2, username);
            int affectedRows = stmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Determine if the email is already taken
    @Override
    public boolean emailExists(String email) {
        String sql = "SELECT 1 FROM users WHERE email = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update the date of latest check-in after a check-in.
    @Override
    public boolean updateLastRewardClaimed(String username, Date date) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE users SET last_reward_claimed = ? WHERE username = ?")) {
            stmt.setDate(1, new java.sql.Date(date.getTime()));
            stmt.setString(2, username);
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public Date getLastRewardClaimed(String username) {
        String sql = "SELECT last_reward_claimed FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDate("last_reward_claimed");
            } else {
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int getExperience(String username) {
        String sql = "SELECT experience FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("experience");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public int getLevel(String username) {
        String sql = "SELECT level FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("level");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 1;
    }

    @Override
    public boolean updateExperienceAndLevel(String username, int newExperience, int newLevel) {
        String sql = "UPDATE users SET experience = ?, level = ? WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, newExperience);
            pstmt.setInt(2, newLevel);
            pstmt.setString(3, username);
            int affectedRows = pstmt.executeUpdate();
            return affectedRows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    @Override
    public Optional<User> validateUser(String username, String password) {
        String sql = "SELECT username, password, salt, email, score, level, experience, security_question, security_answer FROM users WHERE username = ?";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String storedPassword = rs.getString("password");
                String salt = rs.getString("salt");
                String encryptedInputPassword = PasswordUtil.encryptPassword(password + salt);
                if (storedPassword.equals(encryptedInputPassword)) {
                    return Optional.of(new User(
                        rs.getString("username"),
                        rs.getString("password"),
                        salt,
                        rs.getString("email"),
                        rs.getInt("score"),
                        rs.getInt("level"),
                        rs.getInt("experience"),
                        rs.getString("security_question"),
                        rs.getString("security_answer")
                    ));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return Optional.empty();
    }

}
