# PostgreSQL Database Setup Guide

Database Name: `event_management_system`

---

## How to Run the Query

### Option 1: Using psql CLI (Terminal)

```bash
# 1. First create database if not exists:
psql -U postgres -c "CREATE DATABASE event_management_system;"

# 2. Execute the schema.sql file:
psql -U postgres -d event_management_system -f query/schema.sql
```

### Option 2: Using pgAdmin GUI

1. Open **pgAdmin 4**.
2. Connect to your PostgreSQL server.
3. Right-click on **Databases** -> **Create** -> **Database...**
4. Name the database: `event_management_system` and click **Save**.
5. Select `event_management_system`, right-click and choose **Query Tool**.
6. Open the file [`schema.sql`](file:///d:/event/query/schema.sql) or copy its content.
7. Click the **Execute / Run (F5)** button.

---

## Tables Created

1. `users` - Customer accounts (User ID, name, email, password, phone, created_at).
2. `admin` - Admin accounts (Admin ID, name, email, password, created_at).
3. `venue` - Venues with location and seating capacity.
4. `event` - Events with types (WEDDING, CONCERT, etc.), dates, pricing, venue FK, admin FK.
5. `booking` - Customer bookings connecting user with event.
6. `payment` - Payment records (CASH, UPI, CARD) without transaction_id.
