package model;

import enums.EventStatus;
import enums.EventType;
import java.time.LocalDateTime;

/**
 * Model class representing an Event.
 * Demonstrates:
 * - Aggregation (Event has a Venue)
 * - Interface & Polymorphism (implements Comparable<Event>)
 */
public class Event implements Comparable<Event> {

    private int eventId;
    private String eventName;
    private EventType eventType;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String description;
    private double ticketPrice;
    private EventStatus status;
    private int venueId;
    private int adminId;
    private LocalDateTime createdAt;

    // Aggregation: Event holds a reference to a Venue object
    private Venue venue;

    public Event() {
        this.status = EventStatus.UPCOMING;
    }

    public Event(int eventId, String eventName, EventType eventType, 
                 LocalDateTime startDate, LocalDateTime endDate,
                 LocalDateTime startTime, LocalDateTime endTime, 
                 String description, double ticketPrice, 
                 EventStatus status, int venueId, int adminId) {
        this.eventId = eventId;
        this.eventName = eventName;
        this.eventType = eventType;
        this.startDate = startDate;
        this.endDate = endDate;
        this.startTime = startTime;
        this.endTime = endTime;
        this.description = description;
        this.ticketPrice = ticketPrice;
        this.status = status;
        this.venueId = venueId;
        this.adminId = adminId;
        this.createdAt = LocalDateTime.now();
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public void setTicketPrice(double ticketPrice) {
        this.ticketPrice = ticketPrice;
    }

    public EventStatus getStatus() {
        return status;
    }

    public void setStatus(EventStatus status) {
        this.status = status;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public int getAdminId() {
        return adminId;
    }

    public void setAdminId(int adminId) {
        this.adminId = adminId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Venue getVenue() {
        return venue;
    }

    public void setVenue(Venue venue) {
        this.venue = venue;
    }

    /**
     * Dynamically calculates status based on current time and dates.
     */
    public EventStatus calculateCurrentStatus(LocalDateTime now) {
        if (this.status == EventStatus.CANCELLED) {
            return EventStatus.CANCELLED;
        }
        if (now.isBefore(this.startDate)) {
            return EventStatus.UPCOMING;
        } else if (!now.isAfter(this.endDate)) {
            return EventStatus.ONGOING;
        } else {
            return EventStatus.COMPLETED;
        }
    }

    /**
     * Interface implementation for Comparable<Event>.
     * Sorts events chronologically by start date/time.
     */
    @Override
    public int compareTo(Event other) {
        if (other == null || this.startDate == null || other.startDate == null) {
            return 0;
        }
        return this.startDate.compareTo(other.startDate);
    }

    @Override
    public String toString() {
        return "Event #" + eventId + ": " + eventName + " (" + eventType + ") | " +
               startDate.toLocalDate() + " to " + endDate.toLocalDate() + 
               " | Status: " + status + " | Price: ₹" + ticketPrice;
    }
}
