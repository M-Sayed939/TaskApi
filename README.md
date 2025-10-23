# Task Management REST API (TaskApi)

This is a secure, professional REST API for a task management system, built with Java and Spring Boot. It provides a complete set of endpoints for user authentication (registration and login) and full CRUD (Create, Read, Update, Delete) operations for tasks, with all task endpoints protected by JWT.

This project is built to demonstrate a senior-level approach to API design, including a clean layered architecture, JWT-based security, global exception handling, and unit testing.

## Table of Contents

1.  [Features](#features)
2.  [Technology Stack](#technology-stack)
3.  [Getting Started](#getting-started)
      * [Prerequisites](#prerequisites)
      * [1. Database Setup](#1-database-setup)
      * [2. Application Configuration](#2-application-configuration)
      * [3. Run the Application](#3-run-the-application)
4.  [API Endpoint Guide (Postman)](#api-endpoint-guide-postman)
      * [Part 1: Authentication (`/auth`)](#part-1-authentication-auth)
      * [Part 2: Task Management (`/tasks`)](#part-2-task-management-tasks)

-----

## Features

  * **Secure User Authentication:** Full sign-up (`/auth/register`) and login (`/auth/login`) functionality.
  * **JWT Security:** Stateless, token-based authentication using JSON Web Tokens (JWT) to protect all `/tasks` endpoints.
  * **Stateless Logout:** Includes a `/auth/logout` endpoint that returns a success message, relying on the client (frontend) to delete the token for a true stateless experience.
  * **Password Hashing:** All user passwords are securely hashed using **BCrypt**.
  * **Full Task CRUD:** Authenticated users can create, read, update, and delete their own tasks.
  * **Ownership & Security:** Users can only view, update, or delete tasks that they own. Accessing another user's tasks is forbidden (HTTP 403).
  * **Professional Error Handling:** A global `@ControllerAdvice` provides clean, standardized JSON error responses for all API errors (e.g., `400`, `401`, `403`).
  * **Layered Architecture:** Clear separation of concerns between Controllers, Services, and Repositories.
  * **DTO-Based Responses:** API responses use `TaskResponse` DTOs to prevent data leakage (like infinite recursion or hashed passwords).

-----

## Technology Stack

  * **Backend:** Java 17+, Spring Boot
  * **Security:** Spring Security, JSON Web Tokens (JJWT)
  * **Data Persistence:** Spring Data JPA (Hibernate)
  * **Database:** PostgreSQL
  * **Validation:** Jakarta Bean Validation
  * **Build Tool:** Apache Maven
  * **Testing:** JUnit 5
-----

## Getting Started

### Prerequisites

  * Java Development Kit (JDK) 17 or higher
  * Apache Maven
  * PostgreSQL
  * A testing tool like Postman

### 1\. Database Setup

You must create a database and a dedicated user for the application in PostgreSQL.

1.  Connect to PostgreSQL as a superuser (e.g., `psql -U postgres`).

2.  Run the following SQL commands:

    ```sql
    CREATE DATABASE task_db;

    CREATE USER task_user WITH PASSWORD 'your_secure_password';

    GRANT ALL PRIVILEGES ON DATABASE task_db TO task_user;

    -- This is crucial for Hibernate to be able to create tables
    GRANT ALL ON SCHEMA public TO task_user;
    ```

### 2\. Application Configuration

1.  Clone this repository to your local machine.

2.  Open the file: `src/main/resources/application.properties`

3.  Update the file with your database password and, if you wish, a new JWT secret.

    ```properties
    # Server Configuration
    server.port=8081
    spring.application.name=task-api

    # Database Configuration
    spring.datasource.url=jdbc:postgresql://localhost:5432/task_db
    spring.datasource.username=task_user
    spring.datasource.password=your_secure_password

    # JPA & Hibernate Configuration
    spring.jpa.hibernate.ddl-auto=update
    spring.jpa.show-sql=true

    # JWT Security Configuration
    app.jwt.expiration-in-ms=86400000
    app.jwt.secret=N2p5v8y/A?D(G+KbPeShVmYq3s6v9y$B&E)H@McQfTjWnZr4u7x!A%C*F-JaNdRg
    ```

### 3\. Run the Application

You can build and run the application using Maven.

```bash
# Clean, compile, test, and package the application
mvn clean install

# Run the application
java -jar target/TaskApi-0.0.1-SNAPSHOT.jar 
```

(Note: The JAR file name may vary based on your `pom.xml` artifactId)

The API will be running at `http://localhost:8081`.

-----

## API Endpoint Guide (Postman)

### Part 1: Authentication (`/auth`)

#### 1\. Register a New User (Success)

  * **Method:** `POST`

  * **URL:** `http://localhost:8081/auth/register`

  * **Body:** `raw` (select `JSON`)

  * **JSON Payload:**

    ```json
    {
        "name": "Test",
        "email": "test@example.com",
        "password": "password123"
    }
    ```

  * **Expected Response:**

      * **Status:** `201 Created`
      * **Body:**
        ```json
        {
            "success": true,
            "message": "User registered successfully!"
        }
        ```

#### 2\. Register with Duplicate Email (Failure)

  * **Method:** `POST`
  * **URL:** `http://localhost:8081/auth/register`
  * **Body:** (Use the same JSON payload as above)
  * **Expected Response:**
      * **Status:** `400 Bad Request`
      * **Body:** (A JSON error object from the `GlobalExceptionHandler`)
        ```json
        {
            "status": 400,
            "message": "Error: Email is already in use!",
            "timestamp": "..."
        }
        ```

#### 3\. Log In (Success & Save Token)

This is the most important step. We will log in and use a Postman test script to automatically save the JWT for future requests.

  * **Method:** `POST`

  * **URL:** `http://localhost:8081/auth/login`

  * **Body:** `raw` (select `JSON`)

  * **JSON Payload:**

    ```json
    {
        "email": "test@example.com",
        "password": "password123"
    }
    ```

  * **Expected Response:**

      * **Status:** `200 OK`
      * **Body:**
        ```json
        {
            "accessToken": "ey...[a very long token]...",
            "tokenType": "Bearer"
        }
        ```

#### 4\. Log Out (Stateless)

This endpoint simply acknowledges the client's request to log out. The client (frontend) is responsible for deleting the token from its storage.

  * **Method:** `POST`
  * **URL:** `http://localhost:8081/auth/logout`
  * **Authorization:** (No token is required, but you can send it if you like. The server doesn't do anything with it.)
  * **Expected Response:**
      * **Status:** `200 OK`
      * **Body:**
        ```json
        {
            "success": true,
            "message": "User logged out successfully."
        }
        ```

-----

### Part 2: Task Management (`/tasks`)

For all requests in this section, you must add your authentication token.

**How to Add the Token:**

1.  Go to the **"Authorization"** tab for your new request.
2.  Select **"Bearer Token"** from the "Type" dropdown.
3.  In the "Token" field, type **`{{authToken}}`**. This will automatically use the token you saved in the login step.

#### 5\. Create a New Task (Success)

  * **Method:** `POST`

  * **URL:** `http://localhost:8081/tasks`

  * **Authorization:** `Bearer Token` (using `{{authToken}}`)

  * **Body:** `raw` (select `JSON`)

  * **JSON Payload:**

    ```json
    {
        "title": "My First Task",
        "description": "Complete the Postman test guide."
    }
    ```

  * **Expected Response:**

      * **Status:** `201 Created`
      * **Body:** (The `TaskResponse` DTO, **note:** no sensitive user info is exposed)
        ```json
        {
            "id": 1,
            "title": "My First Task",
            "description": "Complete the Postman test guide.",
            "status": "OPEN",
            "userId": 1
        }
        ```

#### 6\. Get All Tasks for User (Success)

  * **Method:** `GET`
  * **URL:** `http://localhost:8081/tasks`
  * **Authorization:** `Bearer Token` (using `{{authToken}}`)
  * **Expected Response:**
      * **Status:** `200 OK`
      * **Body:** (A list of your `TaskResponse` DTOs)
        ```json
        [
            {
                "id": 1,
                "title": "My First Task",
                "description": "Complete the Postman test guide.",
                "status": "OPEN",
                "userId": 1
            }
        ]
        ```

#### 7\. Update a Task's Status (Success)

  * **Method:** `PUT`

  * **URL:** `http://localhost:8081/tasks/1` (Use the ID of the task you created)

  * **Authorization:** `Bearer Token` (using `{{authToken}}`)

  * **Body:** `raw` (select `JSON`)

  * **JSON Payload:**

    ```json
    {
        "status": "COMPLETED"
    }
    ```

  * **Expected Response:**

      * **Status:** `200 OK`
      * **Body:** (The updated `TaskResponse` DTO)
        ```json
        {
            "id": 1,
            "title": "My First Task",
            "description": "Complete the Postman test guide.",
            "status": "COMPLETED",
            "userId": 1
        }
        ```

#### 8\. Delete a Task (Success)

  * **Method:** `DELETE`
  * **URL:** `http://localhost:8081/tasks/1` (Use the ID of the task you created)
  * **Authorization:** `Bearer Token` (using `{{authToken}}`)
  * **Expected Response:**
      * **Status:** `204 No Content`
      * **Body:** (Empty)

#### 9\. Get Tasks (No Token - Failure)

  * **Method:** `GET`
  * **URL:** `http://localhost:8081/tasks`
  * **Authorization:** `No Auth`
  * **Expected Response:**
      * **Status:** `401 Unauthorized`
