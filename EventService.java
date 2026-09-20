package service;

import dao.EventDAO;
import enums.EventStatus;
import exception.EventException;
import model.Event;
import model.Venue;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Service handling event business rules, date filtering, and status calculations.
 */
public class EventService {

    private final EventDAO eventDAO;

    public EventService() {
        this.eventDAO = new EventDAO();
    }

    /**
     * User-facing event listing: returns only Current and Upcoming events.
     * Past events are strictly excluded.
     */
    public List<Event> getUserEvents() throws SQLException {
        List<Event> events = eventDAO.getUpcomingAndCurrentEvents();
        LocalDateTime now = LocalDateTime.now();
        List<Event> filtered = new ArrayList<>();

        for (Event event : events) {
            // Check dynamic status
            EventStatus currentStatus = event.calculateCurrentStatus(now);
            if (currentStatus != EventStatus.COMPLETED && currentStatus != EventStatus.CANCELLED) {
                event.setStatus(currentStatus);
                filtered.add(event);
            }
        }
        return filtered;
    }

    /**
     * Admin-facing event listing: returns all events (Upcoming, Ongoing, Completed, Cancelled).
     */
    public List<Event> getAllEventsForAdmin() throws SQLException {
        List<Event> events = eventDAO.getAllEvents();
        LocalDateTime now = LocalDateTime.now();

        for (Event event : events) {
            if (event.getStatus() != EventStatus.CANCELLED) {
                event.setStatus(event.calculateCurrentStatus(now));
            }
        }
        return events;
    }

    /**
     * Retrieves event details with validation.
     */
    public Event getEventById(int eventId) throws EventException, SQLException {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            throw new EventException("Event with ID " + eventId + " not found.");
        }
        if (event.getStatus() != EventStatus.CANCELLED) {
            event.setStatus(event.calculateCurrentStatus(LocalDateTime.now()));
        }
        return event;
    }

    public List<Venue> getAllVenues() throws SQLException {
        return eventDAO.getAllVenues();
    }

    public Venue getVenueById(int venueId) throws SQLException {
        return eventDAO.getVenueById(venueId);
    }
}
