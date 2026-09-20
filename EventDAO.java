package dao;

import enums.EventStatus;
import enums.EventType;
import model.Event;
import model.Venue;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Data Access Object for Event and Venue database interactions.
 */
public class EventDAO {

    public List<Event> getAllEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        String sql = "SELECT e.event_id, e.event_name, e.event_type, e.start_date, e.end_date, " +
                     "e.start_time, e.end_time, e.description, e.ticket_price, e.status, " +
                     "e.venue_id, e.admin_id, e.created_at, " +
                     "v.venue_name, v.location, v.capacity " +
                     "FROM event e " +
                     "LEFT JOIN venue v ON e.venue_id = v.venue_id " +
                     "ORDER BY e.start_date ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                events.add(mapRow(rs));
            }
        }
        Collections.sort(events); // Uses Comparable<Event>
        return events;
    }

    public List<Event> getUpcomingAndCurrentEvents() throws SQLException {
        List<Event> events = new ArrayList<>();
        // Current or upcoming: event has not ended yet (end_date >= NOW()) and is not cancelled
        String sql = "SELECT e.event_id, e.event_name, e.event_type, e.start_date, e.end_date, " +
                     "e.start_time, e.end_time, e.description, e.ticket_price, e.status, " +
                     "e.venue_id, e.admin_id, e.created_at, " +
                     "v.venue_name, v.location, v.capacity " +
                     "FROM event e " +
                     "LEFT JOIN venue v ON e.venue_id = v.venue_id " +
                     "WHERE e.end_date >= NOW() AND e.status != 'CANCELLED'::event_status " +
                     "ORDER BY e.start_date ASC";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                events.add(mapRow(rs));
            }
        }
        Collections.sort(events);
        return events;
    }

    public Event getEventById(int eventId) throws SQLException {
        String sql = "SELECT e.event_id, e.event_name, e.event_type, e.start_date, e.end_date, " +
                     "e.start_time, e.end_time, e.description, e.ticket_price, e.status, " +
                     "e.venue_id, e.admin_id, e.created_at, " +
                     "v.venue_name, v.location, v.capacity " +
                     "FROM event e " +
                     "LEFT JOIN venue v ON e.venue_id = v.venue_id " +
                     "WHERE e.event_id = ?";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    public boolean createEvent(Event event) throws SQLException {
        String sql = "INSERT INTO event (event_name, event_type, start_date, end_date, " +
                     "start_time, end_time, description, ticket_price, status, venue_id, admin_id, created_at) " +
                     "VALUES (?, ?::event_type, ?, ?, ?, ?, ?, ?, ?::event_status, ?, ?, ?)";

        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, event.getEventName());
            stmt.setString(2, mapEventTypeToDb(event.getEventType()));
            stmt.setTimestamp(3, Timestamp.valueOf(event.getStartDate()));
            stmt.setTimestamp(4, Timestamp.valueOf(event.getEndDate()));
            stmt.setTimestamp(5, Timestamp.valueOf(event.getStartTime()));
            stmt.setTimestamp(6, Timestamp.valueOf(event.getEndTime()));
            stmt.setString(7, event.getDescription());
            stmt.setDouble(8, event.getTicketPrice());
            stmt.setString(9, event.getStatus().name());
            stmt.setInt(10, event.getVenueId());
            stmt.setInt(11, event.getAdminId());
            stmt.setTimestamp(12, Timestamp.valueOf(event.getCreatedAt()));

            int affected = stmt.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        event.setEventId(rs.getInt(1));
                    }
                }
                return true;
            }
        }
        return false;
    }

    public boolean updateEventStatus(int eventId, EventStatus status) throws SQLException {
        String sql = "UPDATE event SET status = ?::event_status WHERE event_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, eventId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteEvent(int eventId) throws SQLException {
        String sql = "DELETE FROM event WHERE event_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean hasBookings(int eventId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM booking WHERE event_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, eventId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    public List<Venue> getAllVenues() throws SQLException {
        List<Venue> venues = new ArrayList<>();
        String sql = "SELECT venue_id, venue_name, location, capacity, created_at FROM venue ORDER BY venue_id ASC";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                Venue v = new Venue();
                v.setVenueId(rs.getInt("venue_id"));
                v.setVenueName(rs.getString("venue_name"));
                v.setLocation(rs.getString("location"));
                v.setCapacity(rs.getInt("capacity"));
                Timestamp ts = rs.getTimestamp("created_at");
                if (ts != null) v.setCreatedAt(ts.toLocalDateTime());
                venues.add(v);
            }
        }
        return venues;
    }

    public Venue getVenueById(int venueId) throws SQLException {
        String sql = "SELECT venue_id, venue_name, location, capacity, created_at FROM venue WHERE venue_id = ?";
        Connection conn = DBConnection.getInstance().getConnection();
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, venueId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Venue v = new Venue();
                    v.setVenueId(rs.getInt("venue_id"));
                    v.setVenueName(rs.getString("venue_name"));
                    v.setLocation(rs.getString("location"));
                    v.setCapacity(rs.getInt("capacity"));
                    Timestamp ts = rs.getTimestamp("created_at");
                    if (ts != null) v.setCreatedAt(ts.toLocalDateTime());
                    return v;
                }
            }
        }
        return null;
    }

    private Event mapRow(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setEventId(rs.getInt("event_id"));
        event.setEventName(rs.getString("event_name"));

        String typeStr = rs.getString("event_type");
        event.setEventType(mapDbToEventType(typeStr));

        Timestamp sDate = rs.getTimestamp("start_date");
        if (sDate != null) event.setStartDate(sDate.toLocalDateTime());

        Timestamp eDate = rs.getTimestamp("end_date");
        if (eDate != null) event.setEndDate(eDate.toLocalDateTime());

        Timestamp sTime = rs.getTimestamp("start_time");
        if (sTime != null) event.setStartTime(sTime.toLocalDateTime());

        Timestamp eTime = rs.getTimestamp("end_time");
        if (eTime != null) event.setEndTime(eTime.toLocalDateTime());

        event.setDescription(rs.getString("description"));
        event.setTicketPrice(rs.getDouble("ticket_price"));

        String statusStr = rs.getString("status");
        if (statusStr != null) {
            event.setStatus(EventStatus.valueOf(statusStr));
        }

        event.setVenueId(rs.getInt("venue_id"));
        event.setAdminId(rs.getInt("admin_id"));

        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) event.setCreatedAt(createdAt.toLocalDateTime());

        // Venue details if joined
        String vName = rs.getString("venue_name");
        if (vName != null) {
            Venue venue = new Venue();
            venue.setVenueId(event.getVenueId());
            venue.setVenueName(vName);
            venue.setLocation(rs.getString("location"));
            venue.setCapacity(rs.getInt("capacity"));
            event.setVenue(venue);
        }

        return event;
    }

    private String mapEventTypeToDb(EventType type) {
        if (type == EventType.BIRTHDAY_PARTY) {
            return "BIRTHDAY_PARTY";
        }
        return type.name();
    }

    private EventType mapDbToEventType(String typeStr) {
        if (typeStr == null) return EventType.OTHER;
        if ("BIRTHDAY".equalsIgnoreCase(typeStr)) return EventType.BIRTHDAY_PARTY;
        try {
            return EventType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EventType.OTHER;
        }
    }
}
