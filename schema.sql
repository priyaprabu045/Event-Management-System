-- ====================================================================
-- EVENT MANAGEMENT SYSTEM (EMS) - DATABASE SCHEMA & INITIAL DATA
-- Database: event_management_system
-- RDBMS: PostgreSQL
-- ====================================================================

-- 1. DROP EXISTING TABLES & TYPES (Clean Reset if Needed)
DROP TABLE IF EXISTS payment CASCADE;
DROP TABLE IF EXISTS booking CASCADE;
DROP TABLE IF EXISTS event CASCADE;
DROP TABLE IF EXISTS venue CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS admin CASCADE;

DROP TYPE IF EXISTS payment_status CASCADE;
DROP TYPE IF EXISTS payment_method CASCADE;
DROP TYPE IF EXISTS booking_status CASCADE;
DROP TYPE IF EXISTS event_status CASCADE;
DROP TYPE IF EXISTS event_type CASCADE;

-- ====================================================================
-- 2. CREATE ENUM TYPES
-- ====================================================================

CREATE TYPE event_type AS ENUM (
    'WEDDING',
    'DJ_PARTY',
    'BIRTHDAY_PARTY',
    'CONCERT',
    'CORPORATE',
    'OTHER'
);

CREATE TYPE event_status AS ENUM (
    'UPCOMING',
    'ONGOING',
    'COMPLETED',
    'CANCELLED'
);

CREATE TYPE booking_status AS ENUM (
    'CONFIRMED',
    'CANCELLED',
    'COMPLETED'
);

CREATE TYPE payment_method AS ENUM (
    'CASH',
    'UPI',
    'CARD'
);

CREATE TYPE payment_status AS ENUM (
    'SUCCESS',
    'FAILED',
    'REFUNDED'
);

-- ====================================================================
-- 3. CREATE TABLES
-- ====================================================================

-- --------------------------------------------------------------------
-- Table: users (Customer Users)
-- --------------------------------------------------------------------
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone VARCHAR(15) UNIQUE,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------------
-- Table: admin (System Administrators)
-- --------------------------------------------------------------------
CREATE TABLE admin (
    admin_id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------------
-- Table: venue (Event Locations & Capacity)
-- --------------------------------------------------------------------
CREATE TABLE venue (
    venue_id SERIAL PRIMARY KEY,
    venue_name VARCHAR(150) NOT NULL,
    location VARCHAR(200) NOT NULL,
    capacity INT NOT NULL CHECK (capacity > 0),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- --------------------------------------------------------------------
-- Table: event (Events created by Admin)
-- --------------------------------------------------------------------
CREATE TABLE event (
    event_id SERIAL PRIMARY KEY,
    event_name VARCHAR(150) NOT NULL,
    event_type event_type NOT NULL,
    start_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_date TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    start_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    end_time TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    description TEXT,
    ticket_price NUMERIC(10, 2) NOT NULL CHECK (ticket_price >= 0),
    status event_status NOT NULL DEFAULT 'UPCOMING',
    venue_id INT NOT NULL,
    admin_id INT NOT NULL,
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    -- Constraints
    CONSTRAINT fk_event_venue FOREIGN KEY (venue_id) REFERENCES venue(venue_id) ON DELETE RESTRICT,
    CONSTRAINT fk_event_admin FOREIGN KEY (admin_id) REFERENCES admin(admin_id) ON DELETE RESTRICT,
    CONSTRAINT check_event_date CHECK (end_date >= start_date)
);

-- Indexes for performance
CREATE INDEX idx_event_status ON event(status);
CREATE INDEX idx_event_date ON event(start_date, end_date);
CREATE INDEX idx_event_venue ON event(venue_id);

-- --------------------------------------------------------------------
-- Table: booking (Customer Event Bookings)
-- --------------------------------------------------------------------
CREATE TABLE booking (
    booking_id SERIAL PRIMARY KEY,
    user_id INT NOT NULL,
    event_id INT NOT NULL,
    booking_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    total_amount NUMERIC(10, 2) NOT NULL CHECK (total_amount >= 0),
    status booking_status NOT NULL DEFAULT 'CONFIRMED',

    -- Constraints
    CONSTRAINT fk_booking_user FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT,
    CONSTRAINT fk_booking_event FOREIGN KEY (event_id) REFERENCES event(event_id) ON DELETE RESTRICT
);

-- Indexes for quick lookup
CREATE INDEX idx_booking_user ON booking(user_id);
CREATE INDEX idx_booking_event ON booking(event_id);

-- --------------------------------------------------------------------
-- Table: payment (Booking Payments)
-- Note: No transaction_id, as per project requirements
-- --------------------------------------------------------------------
CREATE TABLE payment (
    payment_id SERIAL PRIMARY KEY,
    booking_id INT NOT NULL UNIQUE,
    amount NUMERIC(10, 2) NOT NULL CHECK (amount >= 0),
    payment_date TIMESTAMP WITHOUT TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    payment_method payment_method NOT NULL,
    payment_status payment_status NOT NULL DEFAULT 'SUCCESS',

    CONSTRAINT fk_payment_booking FOREIGN KEY (booking_id) REFERENCES booking(booking_id) ON DELETE RESTRICT
);

-- ====================================================================
-- 4. INSERT INITIAL DUMMY / SEED DATA
-- ====================================================================

-- Insert Default Admin
INSERT INTO admin (name, email, password) 
VALUES ('System Admin', 'admin@gmail.com', 'admin123');

-- Insert Sample Users
INSERT INTO users (name, email, password, phone) VALUES 
('Priya', 'priya@gmail.com', 'priya123', '9876543210'),
('Kumar', 'kumar@gmail.com', 'kumar123', '9876543211');

-- Insert Sample Venues
INSERT INTO venue (venue_name, location, capacity) VALUES 
('Grand Mahal', 'Sivakasi', 500),
('Royal Convention Hall', 'Madurai', 1000),
('City Party Hall', 'Virudhunagar', 300);

-- Insert Sample Events
INSERT INTO event (
    event_name, event_type, 
    start_date, end_date, 
    start_time, end_time, 
    description, ticket_price, 
    status, venue_id, admin_id
) VALUES 
(
    'Birthday Celebration', 'BIRTHDAY_PARTY', 
    '2026-10-10 00:00:00', '2026-10-10 00:00:00', 
    '2026-10-10 18:00:00', '2026-10-10 21:00:00', 
    'Grand Birthday party event', 500.00, 
    'UPCOMING', 1, 1
),
(
    'Wedding Ceremony', 'WEDDING', 
    '2026-11-15 00:00:00', '2026-11-16 00:00:00', 
    '2026-11-15 09:00:00', '2026-11-16 22:00:00', 
    'Traditional wedding celebration', 1000.00, 
    'UPCOMING', 2, 1
);

-- ====================================================================
-- END OF SCHEMA SCRIPT
-- ====================================================================
