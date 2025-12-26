# Role-Based Access Control (RBAC) REST API

A **production-ready backend REST API** built using **Java and Spring Boot**, implementing **JWT-based authentication** and **Role-Based Access Control (RBAC)** to secure application resources.

This project demonstrates how real-world applications control access to APIs based on user roles such as **ADMIN**, **MANAGER**, and **USER**.

---

##  Project Overview

In modern applications, not all users should have the same level of access.  
This project solves that problem by enforcing **role-based authorization**, ensuring that only permitted users can access sensitive endpoints.

The system authenticates users using **JWT tokens** and authorizes requests based on assigned roles.

---

##  Key Features

- User registration and login
- JWT-based authentication (access & refresh tokens)
- Role-based authorization (ADMIN, MANAGER, USER)
- Secure API access using Spring Security
- Admin-only CRUD operations for users
- Admin-only role management and role assignment
- Protected resources accessible based on roles
- Centralized exception handling
- Request validation
- Secure password hashing using BCrypt

---

##  Tech Stack

- **Java**
- **Spring Boot**
- **Spring Security**
- **JWT (JSON Web Tokens)**
- **JPA / Hibernate**
- **PostgreSQL**
- **Maven**

---

## Architecture

The project follows a clean **layered architecture**:

- **Controller Layer** – Handles HTTP requests and responses
- **Service Layer** – Contains business logic
- **Repository Layer** – Database access using JPA
- **Security Layer** – JWT authentication and role-based authorization

DTOs are used to prevent exposing internal entities directly.

---

## Authentication & Authorization Flow

1. User registers or logs in
2. Backend validates credentials
3. JWT token is generated and returned
4. Client sends JWT token with each request
5. Spring Security filter validates token
6. User role is checked for requested API
7. Request is either **allowed** or **blocked**

---

## Database Design

The system uses a normalized relational database schema:

- **users**
- **roles**
- **user_roles** (many-to-many mapping)

This allows flexible role assignment and scalability.

---

##  Role-Based Access Rules

| Role     | Permissions |
|---------|-------------|
| USER    | Access own protected resources |
| MANAGER | Access manager-level resources |
| ADMIN   | Full access (user & role management) |

---

## Configuration

Sensitive configurations (database credentials, JWT secrets) are managed using **environment variables**.

Default roles (**ADMIN**, **MANAGER**, **USER**) are seeded automatically at application startup.

---

##  How to Run the Project

1. Clone the repository
   ```bash
   git clone https://github.com/your-username/your-repo-name.git
