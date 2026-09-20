package model;

import enums.PaymentStatus;
import java.time.LocalDateTime;

/**
 * Model class representing a Payment transaction.
 * Does not use transaction_id, matching database specifications.
 */
public class Payment {

    private int paymentId;
    private int bookingId;
    private double amount;
    private LocalDateTime paymentDate;
    private String paymentMethod;
    private PaymentStatus paymentStatus;

    public Payment() {
        this.paymentStatus = PaymentStatus.SUCCESS;
        this.paymentDate = LocalDateTime.now();
    }

    public Payment(int paymentId, int bookingId, double amount, LocalDateTime paymentDate, 
                   String paymentMethod, PaymentStatus paymentStatus) {
        this.paymentId = paymentId;
        this.bookingId = bookingId;
        this.amount = amount;
        this.paymentDate = paymentDate;
        this.paymentMethod = paymentMethod;
        this.paymentStatus = paymentStatus;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    @Override
    public String toString() {
        return "Payment #" + paymentId + " | Booking #" + bookingId + " | ₹" + amount + 
               " | Method: " + paymentMethod + " | Status: " + paymentStatus;
    }
}
