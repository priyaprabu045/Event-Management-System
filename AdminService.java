package service;

import dao.BookingDAO;
import dao.EventDAO;
import dao.UserDAO;
import enums.EventStatus;
import exception.EventException;
import model.Booking;
import model.Event;
import model.User;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service encapsulating administrative operations: event management, user inspections,
 * booking oversight, and deletion safety checks.
 */
public class AdminService {

    private final EventDAO eventDAO;
    private final UserDAO userDAO;
    private final BookingDAO bookingDAO;

    public AdminService() {
        this.eventDAO = new EventDAO();
        this.userDAO = new UserDAO();
        this.bookingDAO = new BookingDAO();
    }

    /**
     * Creates a new event with validation.
     */
    public boolean addEvent(Event event) throws EventException, SQLException {
        if (event.getEventName() == null || event.getEventName().trim().isEmpty()) {
            throw new EventException("Event name cannot be empty.");
        }
        if (event.getStartDate() == null || event.getEndDate() == null) {
            throw new EventException("Start date and end date are required.");
        }
        if (event.getEndDate().isBefore(event.getStartDate())) {
            throw new EventException("Event end date cannot be earlier than start date.");
        }
        if (event.getTicketPrice() < 0) {
            throw new EventException("Ticket price cannot be negative.");
        }

        // Automatic status determination if not already set
        if (event.getStatus() == null) {
            event.setStatus(event.calculateCurrentStatus(LocalDateTime.now()));
        }

        return eventDAO.createEvent(event);
    }

    /**
     * Deletes an event safely, enforcing business rules to prevent data inconsistency.
     */
    public boolean deleteEvent(int eventId) throws EventException, SQLException {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            throw new EventException("Cannot delete: Event with ID " + eventId + " does not exist.");
        }

        if (eventDAO.hasBookings(eventId)) {
            throw new EventException("Deletion blocked: Event #" + eventId + 
                                   " has existing booking records. Deleting it would violate audit integrity. " +
                                   "Consider marking the event as CANCELLED instead.");
        }

        return eventDAO.deleteEvent(eventId);
    }

    /**
     * Updates an event's status.
     */
    public boolean updateEventStatus(int eventId, EventStatus status) throws EventException, SQLException {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            throw new EventException("Event #" + eventId + " not found.");
        }
        return eventDAO.updateEventStatus(eventId, status);
    }

    /**
     * Retrieves all registered users without exposing passwords.
     */
    public List<User> getAllUsers() throws SQLException {
        return userDAO.getAllUsers();
    }

    /**
     * Retrieves all bookings across all users and events.
     */
    public List<Booking> getAllBookings() throws SQLException {
        return bookingDAO.getAllBookings();
    }
}
