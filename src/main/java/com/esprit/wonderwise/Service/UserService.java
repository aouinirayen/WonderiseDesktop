package com.esprit.wonderwise.Service;

import com.esprit.wonderwise.Model.User;
import com.esprit.wonderwise.Utils.DataSource;
import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;

public class UserService {

    private Connection cnx;

    public UserService() {
        cnx = DataSource.getInstance().getCnx();
    }

    public boolean updateUser(User user) {
        String sql = "UPDATE users SET username=?, email=?, password=?, phone=?, profile_picture=?, date_of_birth=?, address=?, gender=?, status=? WHERE id=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword()); // Hash in production!
            ps.setString(4, user.getPhone());
            ps.setString(5, user.getProfilePicture());
            ps.setDate(6, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(7, user.getAddress());
            ps.setString(8, user.getGender());
            ps.setString(9, user.getStatus());
            ps.setInt(10, user.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if email exists
    public boolean isEmailExist(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Generate and send reset token to email
    public boolean sendResetToken(String email) {
        String token = generateToken();
        return storeToken(email, token);
    }

    private String generateToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[24];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes).substring(0, 6).toUpperCase();
    }

    private boolean storeToken(String email, String token) {
        String sql = "UPDATE users SET reset_token=? WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Verify token
    public boolean verifyResetToken(String email, String token) {
        String sql = "SELECT reset_token FROM users WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String storedToken = rs.getString("reset_token");
                if (storedToken == null) return false;
                if (!storedToken.equals(token)) return false;
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Update password and clear token
    public boolean updatePassword(String email, String newPassword) {
        String sql = "UPDATE users SET password=?, reset_token=NULL WHERE email=?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, newPassword); // Hash in production!
            ps.setString(2, email);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Check if email is unique
    public boolean isEmailUnique(String email) {
        String sql = "SELECT COUNT(*) FROM users WHERE email = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Register a new user
    public boolean registerUser(User user) {
        String sql = "INSERT INTO users (username, password, role, email, phone, profile_picture, date_of_birth, address, gender, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, user.getUsername());
            ps.setString(2, user.getPassword()); // Hash in production!
            ps.setString(3, user.getRole());
            ps.setString(4, user.getEmail());
            ps.setString(5, user.getPhone());
            ps.setString(6, user.getProfilePicture());
            ps.setDate(7, user.getDateOfBirth() != null ? java.sql.Date.valueOf(user.getDateOfBirth()) : null);
            ps.setString(8, user.getAddress());
            ps.setString(9, user.getGender());
            ps.setString(10, user.getStatus() != null ? user.getStatus() : "Pending");
            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Login method: returns User if credentials are correct and account is not inactive, else null
    public User login(String email, String password) {
        String sql = "SELECT * FROM users WHERE email = ? AND password = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setString(1, email);
            ps.setString(2, password); // Hash in production!
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                String status = rs.getString("status");
                if (status != null && status.equalsIgnoreCase("Inactive")) {
                    return null; // Account inactive
                }
                // Build User object from result set
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setProfilePicture(rs.getString("profile_picture"));
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) user.setDateOfBirth(dob.toLocalDate());
                user.setAddress(rs.getString("address"));
                user.setCreatedAt(rs.getString("created_at"));
                user.setLastLogin(rs.getString("last_login"));
                user.setResetToken(rs.getString("reset_token"));
                user.setGender(rs.getString("gender"));
                user.setStatus(status);
                // Update last_login
                updateLastLogin(user.getId());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void updateLastLogin(int userId) {
        String sql = "UPDATE users SET last_login = NOW() WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Fetch all users for back office management
    public java.util.List<User> getAllUsers() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY created_at DESC";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setRole(rs.getString("role"));
                user.setEmail(rs.getString("email"));
                user.setPhone(rs.getString("phone"));
                user.setProfilePicture(rs.getString("profile_picture"));
                java.sql.Date dob = rs.getDate("date_of_birth");
                if (dob != null) user.setDateOfBirth(dob.toLocalDate());
                user.setAddress(rs.getString("address"));
                user.setCreatedAt(rs.getString("created_at"));
                user.setLastLogin(rs.getString("last_login"));
                user.setResetToken(rs.getString("reset_token"));
                user.setGender(rs.getString("gender"));
                user.setStatus(rs.getString("status"));
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    // Delete user by id
    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int getUserCount() {
        String sql = "SELECT COUNT(*) FROM users";
        try (PreparedStatement ps = cnx.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}

