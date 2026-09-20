package dao;

import enums.BookingStatus;
import model.Booking;
import model.Event;
import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Booking entity operations.
 */
public class BookingDAO {

    public boolean createBooking(Booking booking) throws SQLException {
        String sql = "INSERT INTO booking (user_id, event_id, booking_date, total_amount, status) " +
                     "VALUES (?, ?, ?, ?, ?::booking_status)";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, booking.getUserId());
            stmt.setInt(2, booking.getEventId());
            stmt.setTimestamp(3, Timestamp.valueOf(booking.getBookingDate()));
            stmt.setDouble(4, booking.getTotalAmount());
            stmt.setString(5, booking.getStatus().name());

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        booking.setBookingId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean cancelBooking(int bookingId) throws SQLException {
        String sql = "UPDATE booking SET status = 'CANCELLED'::booking_status WHERE booking_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, bookingId);
            return stmt.executeUpdate() > 0;
        }
    }

    public Booking getBookingById(int bookingId) throws SQLException {
        String sql = "SELECT b.booking_id, b.user_id, b.event_id, b.booking_date, b.total_amount, b.status, " +
                     "u.name as user_name, u.email as user_email, u.phone as user_phone, " +
                     "e.event_name, e.start_date, e.end_date, e.ticket_price " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN event e ON b.event_id = e.event_id " +
                     "WHERE b.booking_id = ?";

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

    public List<Booking> getBookingsByUserId(int userId) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.user_id, b.event_id, b.booking_date, b.total_amount, b.status, " +
                     "u.name as user_name, u.email as user_email, u.phone as user_phone, " +
                     "e.event_name, e.start_date, e.end_date, e.ticket_price " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN event e ON b.event_id = e.event_id " +
                     "WHERE b.user_id = ? " +
                     "ORDER BY b.booking_id DESC";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    bookings.add(mapRow(rs));
                }
            }
        }
        return bookings;
    }

    public List<Booking> getAllBookings() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = "SELECT b.booking_id, b.user_id, b.event_id, b.booking_date, b.total_amount, b.status, " +
                     "u.name as user_name, u.email as user_email, u.phone as user_phone, " +
                     "e.event_name, e.start_date, e.end_date, e.ticket_price " +
                     "FROM booking b " +
                     "JOIN users u ON b.user_id = u.user_id " +
                     "JOIN event e ON b.event_id = e.event_id " +
                     "ORDER BY b.booking_id DESC";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                bookings.add(mapRow(rs));
            }
        }
        return bookings;
    }

    public boolean hasActiveBooking(int userId, int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE user_id = ? AND event_id = ? AND status = 'CONFIRMED'::booking_status";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            stmt.setInt(2, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public int getActiveBookingCount(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE event_id = ? AND status = 'CONFIRMED'::booking_status";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    private Booking mapRow(ResultSet rs) throws SQLException {
        Booking booking = new Booking();
        booking.setBookingId(rs.getInt("booking_id"));
        booking.setUserId(rs.getInt("user_id"));
        booking.setEventId(rs.getInt("event_id"));

        Timestamp bDate = rs.getTimestamp("booking_date");
        if (bDate != null) booking.setBookingDate(bDate.toLocalDateTime());

        booking.setTotalAmount(rs.getDouble("total_amount"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            booking.setStatus(BookingStatus.valueOf(statusStr));
        }

        // Aggregate User
        User u = new User();
        u.setUserId(booking.getUserId());
        u.setName(rs.getString("user_name"));
        u.setEmail(rs.getString("user_email"));
        u.setPhone(rs.getString("user_phone"));
        booking.setUser(u);

        // Aggregate Event
        Event e = new Event();
        e.setEventId(booking.getEventId());
        e.setEventName(rs.getString("event_name"));
        Timestamp sDate = rs.getTimestamp("start_date");
        if (sDate != null) e.setStartDate(sDate.toLocalDateTime());
        Timestamp eDate = rs.getTimestamp("end_date");
        if (eDate != null) e.setEndDate(eDate.toLocalDateTime());
        e.setTicketPrice(rs.getDouble("ticket_price"));
        booking.setEvent(e);

        return booking;
    }
}
