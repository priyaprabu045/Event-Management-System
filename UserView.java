package view;

import exception.BookingException;
import model.Booking;
import model.Event;
import model.User;
import service.BookingService;
import service.EventService;
import util.AppScanner;

import java.sql.SQLException;
import java.util.List;

/**
 * Terminal user interface for customer users.
 */
public class UserView {

    private final User currentUser;
    private final EventService eventService;
    private final BookingService bookingService;

    public UserView(User user) {
        this.currentUser = user;
        this.eventService = new EventService();
        this.bookingService = new BookingService();
    }

    public void displayMenu() {
        boolean active = true;
        while (active) {
            System.out.println("\n===== USER MENU =====");
            System.out.println("1. View Events");
            System.out.println("2. Book Event");
            System.out.println("3. Cancel Booking");
            System.out.println("4. Logout");
            System.out.println("=====================");

            int choice = AppScanner.readInt("Enter your choice (1-4): ");

            switch (choice) {
                case 1 -> handleViewEvents();
                case 2 -> handleBookEvent();
                case 3 -> handleCancelBooking();
                case 4 -> {
                    System.out.println("Logging out... Goodbye, " + currentUser.getName() + "!");
                    active = false;
                }
                default -> System.out.println("Invalid option. Please choose between 1 and 4.");
            }
        }
    }

    /**
     * Requirement: Display only Current and Upcoming events.
     * Past events are strictly hidden from users.
     */
    private void handleViewEvents() {
        System.out.println("\n----------------- CURRENT & UPCOMING EVENTS -----------------");
        try {
            List<Event> events = eventService.getUserEvents();
            if (events.isEmpty()) {
                System.out.println("No current or upcoming events available at the moment.");
                return;
            }

            printEventsTable(events);

        } catch (SQLException e) {
            System.out.println("System Database Error: " + e.getMessage());
        }
    }

    private void handleBookEvent() {
        System.out.println("\n----------------- BOOK AN EVENT -----------------");
        try {
            List<Event> availableEvents = eventService.getUserEvents();
            if (availableEvents.isEmpty()) {
                System.out.println("No events are currently open for booking.");
                return;
            }

            printEventsTable(availableEvents);

            int eventId = AppScanner.readInt("\nEnter Event ID to book: ");

            System.out.println("\nSelect Payment Method:");
            System.out.println("1. UPI");
            System.out.println("2. CARD");
            System.out.println("3. CASH");
            int payChoice = AppScanner.readInt("Enter choice (1-3): ");
            String paymentMethod = switch (payChoice) {
                case 1 -> "UPI";
                case 2 -> "CARD";
                default -> "CASH";
            };

            Booking booking = bookingService.bookEvent(currentUser.getUserId(), eventId, paymentMethod);
            System.out.println("\nSUCCESS: Booking Confirmed!");
            System.out.println("Booking ID: " + booking.getBookingId());
            System.out.println("Event: " + booking.getEvent().getEventName());
            System.out.println("Total Paid: ₹" + booking.getTotalAmount() + " via " + paymentMethod);
            System.out.println("Status: " + booking.getStatus());

        } catch (BookingException e) {
            System.out.println("Booking Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private void handleCancelBooking() {
        System.out.println("\n----------------- CANCEL BOOKING -----------------");
        try {
            List<Booking> bookings = bookingService.getUserBookings(currentUser.getUserId());
            if (bookings.isEmpty()) {
                System.out.println("You have no booking records.");
                return;
            }

            System.out.printf("%-10s %-25s %-12s %-12s\n", "Booking ID", "Event Name", "Amount", "Status");
            System.out.println("---------------------------------------------------------------");
            for (Booking b : bookings) {
                String eName = (b.getEvent() != null) ? b.getEvent().getEventName() : "ID #" + b.getEventId();
                System.out.printf("%-10d %-25s ₹%-11.2f %-12s\n", 
                                  b.getBookingId(), eName, b.getTotalAmount(), b.getStatus());
            }

            int bookingId = AppScanner.readInt("\nEnter Booking ID to cancel (or 0 to return): ");
            if (bookingId == 0) return;

            boolean success = bookingService.cancelBooking(bookingId, currentUser.getUserId());
            if (success) {
                System.out.println("SUCCESS: Booking #" + bookingId + " has been cancelled. Payment refunded.");
            } else {
                System.out.println("Could not cancel booking. Please try again.");
            }

        } catch (BookingException e) {
            System.out.println("Cancellation Error: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Database Error: " + e.getMessage());
        }
    }

    private void printEventsTable(List<Event> events) {
        System.out.printf("%-5s %-22s %-15s %-20s %-12s %-10s %-10s\n", 
                          "ID", "Event Name", "Type", "Venue", "Dates", "Price", "Status");
        System.out.println("--------------------------------------------------------------------------------------------------");
        for (Event e : events) {
            String venueStr = (e.getVenue() != null) ? e.getVenue().getVenueName() : "Venue #" + e.getVenueId();
            String dateRange = e.getStartDate().toLocalDate().toString();
            System.out.printf("%-5d %-22s %-15s %-20s %-12s ₹%-9.2f %-10s\n",
                              e.getEventId(), e.getEventName(), e.getEventType(), 
                              venueStr, dateRange, e.getTicketPrice(), e.getStatus());
        }
    }
}
