package model;

import enums.BookingStatus;
import java.time.LocalDateTime;

/**
 * Model class representing an Event Booking.
 * Demonstrates Aggregation: Booking aggregates User and Event.
 */
public class Booking {

    private int bookingId;
    private int userId;
    private int eventId;
    private LocalDateTime bookingDate;
    private double totalAmount;
    private BookingStatus status;

    // Aggregation references
    private User user;
    private Event event;

    public Booking() {
        this.status = BookingStatus.CONFIRMED;
        this.bookingDate = LocalDateTime.now();
    }

    public Booking(int bookingId, int userId, int eventId, LocalDateTime bookingDate, double totalAmount, BookingStatus status) {
        this.bookingId = bookingId;
        this.userId = userId;
        this.eventId = eventId;
        this.bookingDate = bookingDate;
        this.totalAmount = totalAmount;
        this.status = status;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }

    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    @Override
    public String toString() {
        String eventTitle = (event != null) ? event.getEventName() : "Event #" + eventId;
        String userName = (user != null) ? user.getName() : "User #" + userId;
        return "Booking #" + bookingId + " | " + userName + " -> " + eventTitle + 
               " | Date: " + bookingDate + " | ₹" + totalAmount + " | Status: " + status;
    }
}
