package model;

import java.time.LocalDateTime;

/**
 * Model class representing an event venue.
 * Demonstrates Encapsulation with private fields and public accessors.
 */
public class Venue {

    private int venueId;
    private String venueName;
    private String location;
    private int capacity;
    private LocalDateTime createdAt;

    public Venue() {
    }

    public Venue(int venueId, String venueName, String location, int capacity) {
        this.venueId = venueId;
        this.venueName = venueName;
        this.location = location;
        this.capacity = capacity;
    }

    public Venue(int venueId, String venueName, String location, int capacity, LocalDateTime createdAt) {
        this(venueId, venueName, location, capacity);
        this.createdAt = createdAt;
    }

    public int getVenueId() {
        return venueId;
    }

    public void setVenueId(int venueId) {
        this.venueId = venueId;
    }

    public String getVenueName() {
        return venueName;
    }

    public void setVenueName(String venueName) {
        this.venueName = venueName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return venueName + " (" + location + ", Capacity: " + capacity + ")";
    }
}
