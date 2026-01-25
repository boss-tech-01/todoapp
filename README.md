# Todo Management Backend Application

A backend-focused Todo Management application built with **Java and Spring Boot**, designed using clean layered architecture and domain based separation. The application exposes secure **RESTful APIs** intended for frontend consumption and demonstrates common backend patterns used in full-stack Java applications.

---

## 🚀 Features

- RESTful API design following layered architecture
- User authentication and authorization using **JWT**
- Role-based access control
- MongoDB integration with Spring Data MongoDB
- DTO-based request and response handling
- Application bootstrap logic for default admin initialization
- Global exception handling with consistent error responses

---

## 🛠 Tech Stack

- **Language:** Java
- **Framework:** Spring Boot
- **Security:** Spring Security, JWT
- **Database:** MongoDB
- **Persistence:** Spring Data MongoDB
- **Build Tool:** Maven
- **API Style:** REST

---

## 🧱 Project Architecture

The project follows a **layered architecture** with domain based separation to ensure maintainability and scalability:

- controller → API endpoints
- service → Business logic
- repository → Data access layer
- entity → Domain models
- payload → Request / Response DTOs & mappers
- security → JWT & Spring Security configuration
- exception → Global and custom exception handling
- jobs → Application bootstrap logic
- config → Application configuration

Each layer has a clear responsibility and communicates only with appropriate adjacent layers.

---

## 🔐 Security Overview

- JWT-based authentication
- Role-based authorization
- Secure password handling using Spring Security
- Protected endpoints based on user roles

Sensitive configuration values (e.g. JWT secrets) are externalized and excluded from version control.

---

## ⚙️ Configuration

This project uses an external configuration file for sensitive values.

Example file (committed to the repository):
- api-values.example.properties

Actual configuration file (ignored by Git):
- api-values.properties

Make sure to create your own `api-values.properties` file and provide the required values before running the application.

---

## ▶️ How to Run

1. Clone the repository: https://github.com/mohammad-eliyas-ahmadi/todoapp.git
2. Configure MongoDB (local or cloud)
3. Create `api-values.properties` based on the example file
4. Run the application
5. The application will start on http://localhost:8080

The application will start on the configured port and expose REST APIs ready for frontend integration.

---

## 📌 Notes

- This project is **backend-focused** and does not include a frontend UI.
- APIs are designed to be consumed by a future frontend client (e.g. React).
- Built as a learning and portfolio project to demonstrate backend and full-stack readiness.
