package service;

import dao.BookingDAO;
import dao.EventDAO;
import dao.PaymentDAO;
import enums.BookingStatus;
import enums.EventStatus;
import enums.PaymentStatus;
import exception.BookingException;
import model.Booking;
import model.Event;
import model.Payment;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service orchestrating booking creation, capacity verification, and cancellation rules.
 */
public class BookingService {

    private final BookingDAO bookingDAO;
    private final EventDAO eventDAO;
    private final PaymentDAO paymentDAO;

    public BookingService() {
        this.bookingDAO = new BookingDAO();
        this.eventDAO = new EventDAO();
        this.paymentDAO = new PaymentDAO();
    }

    /**
     * Books an available event for a user after performing capacity and past-event validation.
     */
    public Booking bookEvent(int userId, int eventId, String paymentMethod) throws BookingException, SQLException {
        Event event = eventDAO.getEventById(eventId);
        if (event == null) {
            throw new BookingException("Event with ID " + eventId + " does not exist.");
        }

        LocalDateTime now = LocalDateTime.now();
        EventStatus currentStatus = event.calculateCurrentStatus(now);

        // 1. Prevent booking for past events
        if (currentStatus == EventStatus.COMPLETED || now.isAfter(event.getEndDate())) {
            throw new BookingException("Cannot book this event. It has already ended.");
        }

        // 2. Prevent booking cancelled events
        if (event.getStatus() == EventStatus.CANCELLED) {
            throw new BookingException("Cannot book this event. It has been cancelled by the organizer.");
        }

        // 3. Prevent duplicate active booking by the same user
        if (bookingDAO.hasActiveBooking(userId, eventId)) {
            throw new BookingException("Duplicate booking rejected. You already have an active booking for this event.");
        }

        // 4. Capacity check
        if (event.getVenue() != null) {
            int currentBookings = bookingDAO.getActiveBookingCount(eventId);
            if (currentBookings >= event.getVenue().getCapacity()) {
                throw new BookingException("Booking failed: Event venue capacity (" + 
                                           event.getVenue().getCapacity() + ") is completely full.");
            }
        }

        // 5. Create Booking
        Booking booking = new Booking();
        booking.setUserId(userId);
        booking.setEventId(eventId);
        booking.setBookingDate(now);
        booking.setTotalAmount(event.getTicketPrice());
        booking.setStatus(BookingStatus.CONFIRMED);

        boolean created = bookingDAO.createBooking(booking);
        if (!created) {
            throw new BookingException("Failed to persist booking in database.");
        }

        // 6. Process and record Payment
        Payment payment = new Payment();
        payment.setBookingId(booking.getBookingId());
        payment.setAmount(event.getTicketPrice());
        payment.setPaymentDate(now);
        payment.setPaymentMethod(paymentMethod != null ? paymentMethod : "CASH");
        payment.setPaymentStatus(PaymentStatus.SUCCESS);

        paymentDAO.savePayment(payment);

        booking.setEvent(event);
        return booking;
    }

    /**
     * Cancels a booking if the event has not yet ended.
     */
    public boolean cancelBooking(int bookingId, int userId) throws BookingException, SQLException {
        Booking booking = bookingDAO.getBookingById(bookingId);
        if (booking == null) {
            throw new BookingException("Booking #" + bookingId + " not found.");
        }

        if (booking.getUserId() != userId) {
            throw new BookingException("Unauthorized: You do not own this booking.");
        }

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingException("This booking is already cancelled.");
        }

        // Cancellation rule: Cancellation is allowed only before the event ends.
        Event event = booking.getEvent();
        if (event != null && event.getEndDate() != null) {
            if (LocalDateTime.now().isAfter(event.getEndDate())) {
                throw new BookingException("Cancellation blocked: The event has already completed. " +
                                           "Cancellations are only permitted before the event ends.");
            }
        }

        boolean cancelled = bookingDAO.cancelBooking(bookingId);
        if (cancelled) {
            paymentDAO.updatePaymentStatus(bookingId, PaymentStatus.REFUNDED);
            return true;
        }
        return false;
    }

    /**
     * Retrieves all bookings for a user.
     */
    public List<Booking> getUserBookings(int userId) throws SQLException {
        return bookingDAO.getBookingsByUserId(userId);
    }
}
