# 🏨 Airbnb - Hotel Booking System

Airbnb is a **Spring Boot-based RESTful Hotel Booking System** designed for modern hotel management and user-friendly booking experiences. It supports dynamic pricing, secure authentication, role-based access, and seamless Stripe integration for payments.

---

## 📚 Table of Contents

- [Overview](#overview)
- [Modules](#modules)
- [User Roles](#user-roles)
- [Authentication & Authorization](#authentication--authorization)
- [Hotel Browsing & Booking](#hotel-browsing--booking)
- [Admin Functionality](#admin-functionality)
- [Room & Inventory Management](#room--inventory-management)
- [Payment Flow](#payment-flow)
- [Guest Management](#guest-management)
- [Dynamic Pricing](#dynamic-pricing)
- [Scheduled Tasks](#scheduled-tasks)
- [API Endpoints](#api-endpoints)

---

## 📖 Overview

Airbnb enables users to:
- Search and book hotels.
- Manage bookings and guests.
- Make secure payments via **Stripe**.
- Enjoy dynamic pricing based on real-time strategies.
  
Built with:
- **Spring Boot (RESTful APIs)**
- **JWT Authentication**
- **Role-based Access**
- **Stripe Integration**
- **Scheduled Background Tasks**

---

## 📦 Modules

- **Authentication Module**
- **Hotel Management Module**
- **Room and Inventory Module**
- **Booking Module**
- **Guest Management Module**
- **Pricing Module**
- **Stripe Webhook Listener**
- **Scheduled Task Executor**

---

## 👤 User Roles

### GUEST
- Sign up/login
- Search and book hotels
- Manage bookings and guests

### HOTEL_MANAGER
- Create and manage hotels/rooms
- View bookings and generate reports
- Activate/deactivate hotel listings

---

## 🔐 Authentication & Authorization

- JWT-based security with refresh tokens
- Secure cookie storage for refresh tokens

### Endpoints:
- `POST /auth/signup` – Register new guest
- `POST /auth/login` – Login and receive tokens
- `POST /auth/refresh` – Refresh access token

---

## 🧳 Hotel Browsing & Booking (GUEST)

- `GET /hotels/searchAll` – Paginated hotel list
- `GET /hotels/search` – Filtered search
- `GET /hotels/info/{hotelId}` – Hotel details
- `POST /bookings/init` – Initialize booking
- `POST /bookings/{bookingId}/addGuests` – Add guests
- `POST /bookings/{bookingId}/payments` – Start payment
- `POST /bookings/{bookingId}/cancel` – Cancel booking
- `GET /bookings/{bookingId}/status` – Booking status

---

## 🛠️ Admin Functionality (HOTEL_MANAGER)

- `POST /admin/hotels` – Create hotel
- `PUT /admin/hotels/{id}` – Update hotel
- `PATCH /admin/hotels/activate/{id}` – Activate/deactivate
- `GET /admin/hotels/{id}/bookings` – View bookings
- `GET /admin/hotels/{id}/reports` – Generate reports

---

## 🏨 Room & Inventory Management

- `POST /admin/hotels/{hotelId}/rooms` – Add room
- `PUT /admin/hotels/{hotelId}/rooms/{roomId}` – Update room
- `PATCH /admin/inventory/rooms/{roomId}` – Update inventory
- `GET /admin/inventory/rooms/{roomId}` – View inventory

---

## 💳 Payment Flow (Stripe Integration)

1. Initiate payment – `POST /bookings/{id}/payments`
2. Stripe Checkout session created
3. Stripe sends event → `POST /webhook/payment`
4. Booking status updated to `CONFIRMED`
5. Refunds handled via Stripe on cancellations

---

## 👥 Guest Management

- `GET /users/guests` – List guests
- `POST /users/guests` – Add guest
- `PUT /users/guests/{id}` – Update guest
- `DELETE /users/guests/{id}` – Delete guest

---

## 📈 Dynamic Pricing

Implemented using the **Decorator Pattern** with multiple strategies:
- `BasePricingStrategy` – Default base price
- `SurgePricingStrategy` – Increases during high demand
- `OccupancyPricingStrategy` – Adjusts based on availability
- `UrgencyPricingStrategy` – Increases near check-in date
- `HolidayPricingStrategy` – Premium pricing on holidays

```java
PricingStrategy strategy = new BasePricingStrategy();
strategy = new SurgePricingStrategy(strategy);
strategy = new OccupancyPricingStrategy(strategy);
strategy = new UrgencyPricingStrategy(strategy);
strategy = new HolidayPricingStrategy(strategy);
BigDecimal finalPrice = strategy.calculatePrice(inventory);

```
## ⏲️ Scheduled Tasks

### 🔸 Expire unpaid bookings every 15 minutes

```java
@Scheduled(cron = "0 */15 * * * *") // every 15 mins
public void expireBooking() {
    // logic to expire unpaid bookings
}
```
### 🔸  Update room pricing hourly

```java
@Scheduled(cron = "0 0 * * * *") // every hour
public void updatePrices() {
    // logic to update dynamic pricing
}
```
---

## 🧠 Tech Stack

- **Java **  
- **Spring Boot**  
- **Spring Security (JWT)**  
- **Stripe API** – for handling payments  
- **MySQL / PostgreSQL** – flexible database support  
- **Lombok** – for reducing boilerplate code  
- **Scheduled Tasks** – for automated operations like expiring unpaid bookings and updating room pricing

---

