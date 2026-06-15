# Shopee — Franchise Management System

A full-stack inventory and order management system for bakery franchise operations.

## Tech Stack

| Layer     | Technology                          |
|-----------|-------------------------------------|
| Backend   | Java 17, Spring Boot 3, PostgreSQL  |
| Frontend  | React 18, Vite, Axios               |
| Auth      | JWT (access + refresh tokens)       |
| Real-time | WebSocket / polling fallback        |

---

## Prerequisites

- Java 17+
- Maven 3.8+
- Node.js 18+
- PostgreSQL 14+

---

## Getting Started

### 1. Database

```sql
CREATE DATABASE shopee_db;
```

### 2. Backend

```bash
cd shopee-backend
# Edit src/main/resources/application.properties with your DB credentials
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8081`.

### 3. Frontend

```bash
cd shopee-frontend
cp .env.example .env          # then edit VITE_API_BASE_URL if needed
npm install
npm run dev
```

The app will be available at `http://localhost:5173`.

---

## Project Structure

```
shopee-backend/   – Spring Boot REST API
shopee-frontend/  – React SPA
```

---

## Default Credentials

| Role            | Email                     | Password  |
|-----------------|---------------------------|-----------|
| Super Admin     | admin@shopee.com          | admin123  |
| Franchise Admin | franchise@shopee.com      | admin123  |

> Change these after first login.

---

## License

MIT

