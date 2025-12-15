# 🏨 Airbnb Hotel Management & Booking System

[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?style=flat-square&logo=spring-boot)](https://spring.io/projects/spring-boot)
[![Java](https://img.shields.io/badge/Java-17+-orange?style=flat-square&logo=java)](https://www.oracle.com/java/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-blue?style=flat-square&logo=postgresql)](https://www.postgresql.org/)
[![Stripe](https://img.shields.io/badge/Stripe-Payments-blueviolet?style=flat-square&logo=stripe)](https://stripe.com/)
![Visitors](https://api.visitorbadge.io/api/visitors?path=https%3A%2F%2Fgithub.com%2FARONAGENT%2FAirBnb_App_SpringBoot&label=Visitors&countColor=%23263759&style=flat-square)
[![GitHub issues](https://img.shields.io/github/issues/ARONAGENT/AirBnb_App_SpringBoot?style=flat-square)](https://github.com/ARONAGENT/AirBnb_App_SpringBoot/issues)
[![License](https://img.shields.io/badge/License-Proprietary-red?style=flat-square)](#-license)


> *"The best way to predict the future is to implement it."* — Alan Kay

---

## 📋 Overview

A robust, enterprise-grade hotel management and booking platform designed to handle **10,000+ hotels** with optimized dynamic pricing strategies. This system implements a monolithic architecture featuring advanced HotelMinPrice strategy that efficiently manages **900,000+ records** (90 days × 10,000 hotels) for lightning-fast search operations.

Built with modern software engineering principles, this application showcases JWT authentication, role-based access control, real-time payment processing with Stripe webhooks, Decorator pattern for flexible pricing, comprehensive inventory management, and intelligent cancellation policies.

### 🎯 Key Highlights

- 🔐 **Secure Authentication** - JWT-based authentication with role-based access control (RBAC)
- 💰 **Dynamic Pricing Engine** - Decorator pattern implementation for flexible pricing strategies
- 💳 **Payment Integration** - Stripe webhook integration for real-time payment processing
- 📊 **Optimized Search** - HotelMinPrice strategy for efficient querying across massive datasets
- 🏢 **Inventory Management** - Comprehensive room availability and booking management
- 📝 **API Documentation** - Interactive Swagger UI for seamless API exploration
- ✅ **Input Validation** - Robust validation layer ensuring data integrity

---

## ✨ Features

### Core Functionality
- **User Management**: Complete user registration, authentication, and profile management
- **Hotel Operations**: CRUD operations for hotels with manager-specific access control
- **Room Management**: Dynamic room creation, updates, and inventory tracking
- **Booking System**: End-to-end booking flow with availability checks and conflict resolution
- **Payment Processing**: Secure payment handling via Stripe with webhook confirmation
- **Cancellation Management**: Flexible cancellation policies with automated refund processing
- **Dynamic Pricing**: Multiple pricing strategies using Decorator pattern

### Technical Features
- **Scalable Architecture**: Monolithic design optimized for 10,000+ hotels
- **Performance Optimization**: Efficient search algorithms handling 900K+ price records
- **Real-time Updates**: Webhook-based payment status synchronization
- **Data Validation**: Comprehensive input validation at all API endpoints
- **API Documentation**: Auto-generated Swagger documentation
- **Error Handling**: Graceful error handling with meaningful responses

---
## ✨🔥 Bookings Flow
<img width="1212" height="812" alt="bookings flow" src="https://github.com/user-attachments/assets/5b16e77e-6747-45d6-ae47-8702ff94b178" />



## 🛠️ Technologies

### Tech Stack

| Technology | Version | Purpose |
|------------|---------|---------|
| **Java** | 17+ | Core programming language |
| **Spring Boot** | 3.x | Application framework |
| **Spring Security** | 6.x | Authentication & authorization |
| **Spring Data JPA** | 3.x | Data persistence layer |
| **PostgreSQL** | 14+ | Primary database |
| **Hibernate** | 6.x | ORM framework |
| **JWT** | Latest | Token-based authentication |
| **Stripe API** | Latest | Payment processing |
| **Swagger/OpenAPI** | 3.x | API documentation |
| **Maven** | 3.8+ | Dependency management |
| **Lombok** | Latest | Boilerplate code reduction |
| **Jackson** | Latest | JSON processing |

### Architecture Patterns
- **Monolithic Architecture** - Unified deployment model
- **Repository Pattern** - Data access abstraction
- **Decorator Pattern** - Flexible pricing strategies
- **Service Layer Pattern** - Business logic separation
- **DTO Pattern** - Data transfer optimization

---

## 📸 Screenshots

| # | Screenshot | Description |
|---|------------|-------------|
| 0 | <img width="1202" height="835" alt="0 0 ER Diagram11" src="https://github.com/user-attachments/assets/2beda9a9-532a-4707-81bb-b579371ef097" /> | **ER Diagram** - Database schema |
| 1 | ![Handles Input Validation](https://github.com/user-attachments/assets/2ec6b1b6-c735-4391-9351-2d8ab3819619) | **Handles Input Validation** - Request validation |
| 2 | ![signUp the User](https://github.com/user-attachments/assets/7d465115-abe8-4bc5-882a-468748f19a5e) | **User Signup** - Registration flow |
| 3 | ![Login the User to Get Access the Token](https://github.com/user-attachments/assets/de9d6a8d-7b51-4510-80c0-503a0fd0f016) | **User Login** - JWT authentication |
| 4 | ![create Hotel By Hotel Manager](https://github.com/user-attachments/assets/023c9563-5224-44b5-a62c-f825c2c8db55) | **Create Hotel** - Hotel creation |
| 5 | ![Get Hotel By ID](https://github.com/user-attachments/assets/796ddbf9-2ef8-4bc3-a821-fbbb3edff68c) | **Get Hotel** - Retrieve details |
| 6 | ![Update Hotel Info Put Mapping](https://github.com/user-attachments/assets/a1b5fca9-497d-4233-89b3-7edd1f74d263) | **Update Hotel** - Modify details |
| 7 | ![Create Room for Hotel with id 1](https://github.com/user-attachments/assets/f35fd767-e5e9-40d5-881b-120e166b1ad5) | **Create Room** - Room setup |
| 8 | ![Update Hotel Room Info](https://github.com/user-attachments/assets/aa604d8d-8777-4841-b38e-a409c20ee5aa) | **Update Room** - Room modifications |
| 9 | ![Inventory View of Room Database](https://github.com/user-attachments/assets/cb2f35bf-8d16-43f8-915d-673fd530607a) | **Inventory View** - Database overview |
| 10 | ![Payment Status Of Bookings Flow](https://github.com/user-attachments/assets/7368afde-735e-4706-a6ba-6fcc87eca0b9) | **Payment Status** - Booking payments |
| 11 | ![Stripe Payment Confirm Status](https://github.com/user-attachments/assets/75da3b55-d39d-4dcd-979a-e5bcc376533f) | **Stripe Confirmation** - Payment verified |

### 🎥 Video Demonstrations
- **Hotel Activation Flow** - Complete hotel activation process &  **Inventory Creation** - Annual inventory setup (365 days) 


https://github.com/user-attachments/assets/8455f206-9942-4fa8-a631-ed89ba7fb948


- **Booking Flow** - End-to-end booking demonstration

https://github.com/user-attachments/assets/52312b78-292f-4bfd-bd1a-164d6b09dab9


---

## 🚀 Installation

### Prerequisites

Ensure you have the following installed:
- Java 17 or higher
- Maven 3.8+
- PostgreSQL 14+
- Git
- Stripe Account (for payment processing)

### Setup Instructions

1. **Clone the repository**
   ```bash
   git clone https://github.com/ARONAGENT/AirBnb_App_SpringBoot.git
   cd AirBnb_App_SpringBoot
   ```

2. **Configure Database**
   
   Create a PostgreSQL database:
   ```sql
   CREATE DATABASE airbnb_db;
   ```

3. **Configure Application Properties**
   
   Update `src/main/resources/application.properties`:
   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:postgresql://localhost:5432/airbnb_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   
   # JWT Configuration
   jwt.secret=your_secret_key
   jwt.expiration=86400000
   
   # Stripe Configuration
   stripe.api.key=your_stripe_secret_key
   stripe.webhook.secret=your_webhook_secret
   ```

4. **Build the Application**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

6. **Access the Application**
   - API Base URL: `http://localhost:8080`
   - Swagger UI: `http://localhost:8080/swagger-ui.html`

---

## 💻 Usage

### API Endpoints

#### Authentication
```bash
# Register User
POST /api/auth/signup

# Login User
POST /api/auth/login
```

#### Hotel Management
```bash
# Create Hotel (Manager only)
POST /api/hotels

# Get Hotel by ID
GET /api/hotels/{id}

# Update Hotel
PUT /api/hotels/{id}

# Delete Hotel
DELETE /api/hotels/{id}
```

#### Room Management
```bash
# Create Room
POST /api/hotels/{hotelId}/rooms

# Update Room
PUT /api/rooms/{id}

# Get Room Inventory
GET /api/rooms/inventory
```

#### Booking Management
```bash
# Create Booking
POST /api/bookings

# Get Booking Status
GET /api/bookings/{id}

# Cancel Booking
PUT /api/bookings/{id}/cancel
```

#### Payment Processing
```bash
# Stripe Webhook Endpoint
POST /api/payments/webhook
```

### Example Request

**User Registration:**
```json
POST /api/auth/signup
Content-Type: application/json

{
  "username": "john_doe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "role": "USER"
}
```

For detailed API documentation, visit the Swagger UI after running the application.

---

## 🤝 Contributing

Contributions are currently not accepted as this is a proprietary project. However, feedback and suggestions are welcome!

If you find any bugs or have feature requests, please open an issue on GitHub.

---

## 📄 License
**Copyright (c) 2025 ROHAN UKE**
```
This project and its source code are the exclusive property of the author.
Unauthorized copying, modification, distribution, or commercial use is strictly prohibited.
Limited use is granted for learning, reviewing, and non-commercial demonstration purposes only.
No warranties are provided; use at your own risk.
For permissions beyond this notice, contact: **[your-email@example.com]**
```
---

## 🙏 Acknowledgments

- **Spring Framework Team** - For the incredible Spring Boot ecosystem
- **Stripe** - For robust payment processing APIs
- **PostgreSQL Community** - For the reliable database system
- **Open Source Community** - For the countless libraries and tools that made this project possible
- **Swagger/OpenAPI** - For excellent API documentation capabilities

Special thanks to everyone who provided feedback and suggestions during development.

---

## 📞 Contact

For any inquiries, permissions, or collaboration opportunities:

 [![GitHub](https://img.shields.io/badge/GitHub-ARONAGENT-181717?style=flat-square&logo=github)](https://github.com/ARONAGENT)
[![Repository](https://img.shields.io/badge/Repository-Airbnb_App-blue?style=flat-square&logo=github)](https://github.com/ARONAGENT/AirBnb_App_SpringBoot)

---

### Built with ❤️ by [ARONAGENT](https://github.com/ARONAGENT)

**🌟 Star this repo if you find it helpful! ⭐**

---
<div align="center">
*"Code is like humor. When you have to explain it, it's bad."* — Cory House

</div>
