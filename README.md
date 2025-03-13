# IoT Device Management API

## Overview
This Spring Boot application provides a REST API for managing IoT devices.  
It integrates with MySQL for data storage and Apache Kafka for event-driven communication.

## Features
- Register, update, and delete IoT devices
- Store device details in MySQL
- Send commands to devices via Kafka
- Secure API with JWT Authentication
- Dockerized setup for easy deployment

## Tech Stack
- Spring Boot 3.x
- MySQL (Database)
- Kafka (Event Streaming)
- JWT (Authentication)
- Docker & Docker Compose

---

## Setup Instructions

### Step 1: Clone the Repository
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/iot-device-management.git
   cd iot-device-management
   ```
   
2. Start Services Using Docker
    ```sh
   docker-compose up -d
   ```

3. Test APIs
   Login Request
    ```sh
    curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
    "username": "admin",
    "password": "admin"
    }'
   ```

    Register a Device
    ```sh
    curl -X POST http://localhost:8080/api/devices \
    -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
    "name": "Temperature Sensor",
    "status": "ONLINE",
    "metadata": {
        "location": "Warehouse A"
    }
    }'
   ```

   Fetch Device Details
    ```sh
    curl -X GET http://localhost:8080/api/devices/{device_id} \
    -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

   Send Command to a Device
    ```sh
    curl -X POST http://localhost:8080/api/devices/{device_id}/send-command \
    -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
    "command": "TURN_ON"
    }'
   ```

4. Running Tests
    ```sh
    mvn test
   ```
5. Stopping the Services
    ```sh
    docker-compose down
   ```
