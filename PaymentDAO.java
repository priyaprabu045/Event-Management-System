package dao;

import enums.PaymentStatus;
import model.Payment;
import util.DBConnection;

import java.sql.*;

/**
 * Data Access Object for Payment processing and history.
 */
public class PaymentDAO {

    public boolean savePayment(Payment payment) throws SQLException {
        String sql = "INSERT INTO payment (booking_id, amount, payment_date, payment_method, payment_status) " +
                     "VALUES (?, ?, ?, ?::payment_method, ?::payment_status)";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, payment.getBookingId());
            stmt.setDouble(2, payment.getAmount());
            stmt.setTimestamp(3, Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(4, normalizePaymentMethod(payment.getPaymentMethod()));
            stmt.setString(5, payment.getPaymentStatus().name());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        payment.setPaymentId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public Payment getPaymentByBookingId(int bookingId) throws SQLException {
        String sql = "SELECT payment_id, booking_id, amount, payment_date, payment_method, payment_status " +
                     "FROM payment WHERE booking_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean updatePaymentStatus(int bookingId, PaymentStatus status) throws SQLException {
        String sql = "UPDATE payment SET payment_status = ?::payment_status WHERE booking_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    private Payment mapRow(ResultSet rs) throws SQLException {
        Payment p = new Payment();
        p.setPaymentId(rs.getInt("payment_id"));
        p.setBookingId(rs.getInt("booking_id"));
        p.setAmount(rs.getDouble("amount"));
        Timestamp ts = rs.getTimestamp("payment_date");
        if (ts != null) {
            p.setPaymentDate(ts.toLocalDateTime());
        }
        p.setPaymentMethod(rs.getString("payment_method"));
        String statusStr = rs.getString("payment_status");
        if (statusStr != null) {
            p.setPaymentStatus(PaymentStatus.valueOf(statusStr));
        }
        return p;
    }

    private String normalizePaymentMethod(String method) {
        if (method == null || method.trim().isEmpty()) {
            return "CASH";
        }
        String upper = method.trim().toUpperCase();
        if (upper.contains("UPI")) return "UPI";
        if (upper.contains("CARD")) return "CARD";
        return "CASH";
    }
}
