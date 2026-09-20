package dao;

import model.Admin;
import util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for Admin operations.
 */
public class AdminDAO {

    public Admin findByEmail(String email) throws SQLException {
        String sql = "SELECT admin_id, name, email, password, created_at FROM admin WHERE LOWER(email) = LOWER(?)";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, email.trim());
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public Admin findById(int adminId) throws SQLException {
        String sql = "SELECT admin_id, name, email, password, created_at FROM admin WHERE admin_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, adminId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    private Admin mapRow(ResultSet rs) throws SQLException {
        Admin admin = new Admin();
        admin.setAdminId(rs.getInt("admin_id"));
        admin.setName(rs.getString("name"));
        admin.setEmail(rs.getString("email"));
        admin.setPassword(rs.getString("password"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            admin.setCreatedAt(ts.toLocalDateTime());
        }
        return admin;
    }
}
