# 🛒 ShopKart — E-Commerce Platform

A full-stack, enterprise-grade e-commerce application built with a Spring Boot REST API backend and a React SPA frontend. It supports complete customer shopping workflows (browse, cart, wishlist, checkout, orders, reviews) as well as comprehensive seller/admin management suites (products, variants, categories, coupons, order fulfillment, users).

---

## 📋 Table of Contents
- [Features](#-features)
- [Tech Stack](#-tech-stack)
- [Repository Layout](#-repository-layout)
- [Requirements](#-requirements)
- [Installation & Setup](#-installation--setup)
- [Quick Start](#-quick-start)
- [Configuration & Environment Variables](#-configuration--environment-variables)
- [API Endpoints Overview](#-api-endpoints-overview)
- [Usage Workflows (cURL Examples)](#-usage-workflows-curl-examples)
- [Error Handling Pattern](#-error-handling-pattern)
- [Architecture & Data Model](#-architecture--data-model)
- [Known Limitations](#-known-limitations)

---

## 🌟 Features

### 🛍️ Customer
* **Authentication**: Signup and login with JWT authentication.
* **Shopping**: Browse, search, and filter products by category.
* **Cart & Wishlist**: Real-time management of shopping cart and wishlist items.
* **User Profile**: Address book management and wallet balance.
* **Checkout & Orders**: Checkout with coupon discounts, wallet balance, order tracking, and cancellation.
* **Feedback**: Product ratings and reviews.

### 🛠️ Seller & Admin
* **Inventory Management**: Manage products, variants, SKUs, and image galleries (reordering and primary image setting).
* **Categories & Coupons**: Create and manage categories and dynamic promotional coupons.
* **Order Fulfillment**: Track and update order statuses through delivery lifecycles.
* **User Delegation**: Admin-only control over user active statuses and role updates (CUSTOMER, SELLER, ADMIN).

---

## 🛠️ Tech Stack

| Layer | Technology | Key Libraries / Frameworks |
| :--- | :--- | :--- |
| **Backend** | Java 17, Spring Boot 3.3.1 | Web, Data JPA, Security, Validation |
| **Database & Auth**| MySQL 8.0+, JWT | `jjwt 0.11.5`, MapStruct, Lombok |
| **API Docs** | OpenAPI 3.0 | `springdoc-openapi` (Swagger UI) |
| **Frontend** | React 19, TypeScript, Vite 8 | TanStack Router, TanStack Query, Axios |
| **Styling** | Tailwind CSS v4 | `@tailwindcss/vite` plugin |

---

## 📂 Repository Layout

```text
E_COMMERCE_BACKEND/                 # Spring Boot REST API
├── src/main/java/com/ecommerce/
│   ├── controllers/                # Thin REST controllers (@PreAuthorize, validation)
│   ├── services/                   # Business logic (Interfaces & Impl)
│   ├── repositories/               # Spring Data JPA Repositories
│   ├── models/                     # JPA Entities
│   ├── dto/                        # Request and Response DTOs
│   ├── mapper/                     # MapStruct Entity <-> DTO Mappers
│   ├── security/                   # JWT Auth Filter, SecurityConfig, UserDetails
│   ├── config/                     # Swagger, WebMVC, DataSeeder
│   └── exception/                  # Global Exception Handling
└── src/main/resources/application.properties

E_COMMERCE_FRONTEND/                # React SPA
├── src/routes/                     # File-based routes (TanStack Router)
├── src/components/                 # Shared UI components
├── src/lib/api/                    # Axios client & endpoint functions
├── src/lib/auth-context.tsx        # Auth state (JWT, user profile)
└── vite.config.ts                  # Dev server & API proxy config
```

---

## ⚙️ Requirements

* **Backend**: Java 17+, Maven, MySQL 8+
* **Frontend**: Node.js 18+, npm

---

## 🚀 Installation & Setup

### 1. Backend Setup
1. Clone the repository:
   ```bash
   git clone https://github.com/2310Eswar/E_COMMERCE_BACKEND.git
   cd E_COMMERCE_BACKEND
   ```
2. Create the database in MySQL:
   ```sql
   CREATE DATABASE e_commerce;
   ```
3. Export environment variables:
   ```bash
   export DB_URL="jdbc:mysql://localhost:3306/e_commerce?useSSL=false&serverTimezone=UTC"
   export DB_USERNAME=root
   export DB_PASSWORD=your_password
   export JWT_SECRET=$(openssl rand -hex 32)
   ```
4. Run the application:
   ```bash
   ./mvnw spring-boot:run
   ```
   *The backend starts at `http://localhost:8081`.*

---

### 2. Frontend Setup
1. Clone the frontend repository:
   ```bash
   git clone https://github.com/2310Eswar/E_COMMERCE_FRONTEND.git
   cd E_COMMERCE_FRONTEND
   ```
2. Install dependencies:
   ```bash
   npm install
   ```
3. Configure target backend API:
   ```bash
   echo "VITE_API_BASE_URL=http://localhost:8081" > .env
   ```
4. Start development server:
   ```bash
   npm run dev
   ```
   *The frontend starts at `http://localhost:5173`.*

---

## ⚡ Quick Start

1. Start MySQL and run the backend (`./mvnw spring-boot:run`).
2. Launch the frontend (`npm run dev`).
3. Visit `http://localhost:5173`, sign up for a new customer account, and browse items.
4. To grant Seller/Admin privileges, promote a user role via `PUT /api/user/admin/{userId}/role` (requires an existing `ADMIN` account).
5. Access raw, interactive API docs at `http://localhost:8081/swagger-ui.html`.

---

## 🔧 Configuration & Environment Variables

All settings in `src/main/resources/application.properties` are overridable via environment variables:

| Property | Environment Variable | Default / Description |
| :--- | :--- | :--- |
| `server.port` | `SERVER_PORT` | `8081` |
| `spring.datasource.url` | `DB_URL` | `jdbc:mysql://localhost:3306/e_commerce...` |
| `spring.datasource.username` | `DB_USERNAME` | `root` |
| `spring.datasource.password` | `DB_PASSWORD` | *(Override in non-local environments)* |
| `app.jwt.secret` | `JWT_SECRET` | *(32+ byte key, override in production)* |
| `app.jwt.expirationMs` | — | `86400000` (24 Hours) |
| `upload.dir` | `UPLOAD_DIR` | `uploads/` (Max file size: 5MB) |

---

## 📡 API Endpoints Overview

### Authentication (`/api/auth`)
* `POST /api/auth/signup` — Register new customer account
* `POST /api/auth/signin` — Authenticate user and receive JWT token

### Profile & Users (`/api/user`)
* `GET /api/user/profile` — Get active profile (Authenticated)
* `PUT /api/user/profile` — Update user profile (Authenticated)
* `POST /api/user/addresses` — Add an address (Authenticated)
* `GET /api/user/addresses` — List user addresses (Authenticated)
* `GET /api/user/admin/all` — List all users (ADMIN)
* `PUT /api/user/admin/{userId}/role?role=` — Change user role (ADMIN)
* `POST /api/user/wallet/add?amount=` — Add wallet funds (CUSTOMER)

### Products & Inventory (`/api/products`)
* `GET /api/products` — Paginated active product listing (Public)
* `GET /api/products/search?keyword=` — Keyword search (Public)
* `GET /api/products/category/{categoryId}` — Products by category (Public)
* `POST /api/products` — Create product listing (ADMIN, SELLER)
* `POST /api/products/{id}/main-image` — Upload main thumbnail (ADMIN, SELLER)
* `POST /api/products/{productId}/images` — Add gallery images (ADMIN, SELLER)
* `PATCH /api/products/{productId}/images/{imageId}/primary` — Set primary photo (ADMIN, SELLER)

### Cart & Wishlist
* `GET /api/cart` — Fetch user cart (CUSTOMER)
* `POST /api/cart/add` — Add variant to cart (CUSTOMER)
* `PUT /api/cart/items/{variantId}` — Update item quantity (CUSTOMER)
* `DELETE /api/cart/items/{variantId}` — Remove item from cart (CUSTOMER)
* `GET /api/wishlist` — List wishlist product IDs (CUSTOMER)

### Orders & Payments (`/api/orders`, `/api/payments`)
* `POST /api/orders/place/{addressId}?couponCode=` — Place order (CUSTOMER)
* `GET /api/orders/my-orders` — List current user orders (Authenticated)
* `PUT /api/orders/{orderId}/status?status=` — Update status (ADMIN, SELLER)
* `POST /api/payments` — Record/process payment transaction (Authenticated)

---

## 💻 Usage Workflows (cURL Examples)

### Complete Purchase Workflow
```bash
# 1. Sign in & store token
TOKEN=$(curl -s -X POST http://localhost:8081/api/auth/signin   -H "Content-Type: application/json"   -d '{"email":"jane@example.com","password":"Passw0rd!"}' | jq -r '.token')

# 2. Add product variant to cart
curl -X POST http://localhost:8081/api/cart/add   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{"variantId": 45, "quantity": 2}'

# 3. Add shipping address
curl -X POST http://localhost:8081/api/addresses   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{"line1":"221B Baker Street","city":"Chennai","state":"Tamil Nadu","postalCode":"600001","country":"India"}'

# 4. Place order with coupon code
curl -X POST "http://localhost:8081/api/orders/place/7?couponCode=WELCOME10"   -H "Authorization: Bearer $TOKEN"

# 5. Process order payment
curl -X POST http://localhost:8081/api/payments   -H "Authorization: Bearer $TOKEN"   -H "Content-Type: application/json"   -d '{"orderId": 101, "method": "CARD", "amount": 1499.00}'
```

---

## ⚠️ Error Handling Pattern

The backend's `@RestControllerAdvice` returns structured JSON responses for all exceptions:

```json
{
  "error": "VALIDATION_ERROR",
  "message": "Validation failed",
  "details": {
    "email": "must be a well-formed email address",
    "password": "Password must be at least 8 characters long"
  }
}
```

| HTTP Status | Error Code | Trigger Condition |
| :--- | :--- | :--- |
| `404 Not Found` | `NOT_FOUND` | Resource by ID does not exist. |
| `403 Forbidden` | `FORBIDDEN` | Role unauthorized for target action. |
| `400 Bad Request` | `VALIDATION_ERROR` | `@Valid` body or query param mismatch. |
| `500 Error` | `INTERNAL_ERROR` | Unhandled exception. |

---

## 🏗️ Architecture & Data Model

### Architecture Pattern
```text
Controller ──> Service (Interface) ──> ServiceImpl ──> Repository ──> Entity
    │                                                        │
    └──────────────> MapStruct Mapper <── DTOs <──────────────┘
```

### Key Entities
* **User**: Base account entity linked to `CustomerDetails`, `SellerDetails`, and `Addresses`.
* **Product**: Owned by a Seller; contains `Category`, `ProductVariants`, and `ProductImages`.
* **Order**: Ties together a Customer `User`, shipping `Address`, optional `Coupon`, and list of `OrderItems` with `OrderTracking` histories.

---

## 🚨 Known Limitations

1. **Local Disk Uploads**: File attachments are saved to the local directory (`uploads/`). A cloud object store (e.g., AWS S3) is recommended for scalable multi-instance deployments.
2. **CORS Configuration**: Dev setups use Vite's built-in proxy. Production deployment requires setting explicit backend CORS origins if hosted separately.
3. **API Versioning**: Endpoints are served directly under `/api/...` without version prefixes (e.g., `/api/v1/...`).
