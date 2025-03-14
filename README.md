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

### Prerequisites
Make sure you have installed:
- **Java 17+**
- **Maven 3+**
- **MySQL**
- **Kafka**
- **Docker & Docker Compose**

---

## Setup Instructions

### Step 1: Clone the Repository
1. Clone the repository:
   ```sh
   git clone https://github.com/your-repo/iot-device-management.git
   cd iot-device-management
   ```
   
2. Start Services
   - Using Docker
    ```sh
   docker-compose up -d
   ```
   - Using mvn
    ```sh
   mvn clean install 
   java -jar target/iot-0.0.1-SNAPSHOT.jar
   ```

3. Test APIs

   - Register User
    ```sh
    curl -X POST http://localhost:8080/api/auth/register \
    -H "Content-Type: application/json" \
    -d '{
    "username": "admin",
    "password": "admin",
    "role" : "ADMIN"
   }'
   ```

   - Login Request
    ```sh
    curl -X POST http://localhost:8080/api/auth/login \
    -H "Content-Type: application/json" \
    -d '{
    "username": "admin",
    "password": "admin"
    }'
   ```

    - Register a Device
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

      - Fetch Device Details
    ```sh
    curl -X GET http://localhost:8080/api/devices/{device_id} \
    -H "Authorization: Bearer YOUR_JWT_TOKEN"
   ```

   - Update Device Details
   ```sh
   curl -X PUT http://localhost:8080/devices/{device_id} \
     -H "Content-Type: application/json" \
     -d '{
        "name": "Temperature Sensor",
        "status": "OFFLINE",
        "metadata": {
        "location": "Warehouse A"
         }
      }'
   ```

   - Send Command to a Device
    ```sh
    curl -X POST http://localhost:8080/api/devices/{device_id}/send-command \
    -H "Authorization: Bearer YOUR_JWT_TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
    "command": "TURN_ON"
    }'
   ```

4. Kafka Testing Guide
   ## Topics Involved:
   - thingwire.devices.events → Listens for device events.
   - thingwire.devices.commands → Receives commands sent to devices.
   - thingwire.devices.responses → Devices publish their responses here.

   ### Listen to Messages on a Kafka Topic:

   - Listen for Device Events (thingwire.devices.events)
     ```sh
      ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic thingwire.devices.events --from-beginning
     ```

   - Listen for Device Commands (thingwire.devices.commands)
     ```sh
      ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic thingwire.devices.commands --from-beginning
     ```
   - Listen for Device Responses (thingwire.devices.responses)
     ```sh
      ./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic thingwire.devices.responses --from-beginning
     ```
   ### Publish a Message to Kafka Topics

   - Send a Device Response to thingwire.devices.responses
      ```sh
      ./kafka-console-producer.sh --broker-list localhost:9092 --topic thingwire.devices.commands --property "parse.key=true" --property "key.separator=:"
     ```
     Once the prompt appears (>), enter:
   
      ```sh
      {device-id}:OFFLINE
     ```
      Check Fetch Device Details API to confirm the changes


5. Running Tests
    ```sh
    mvn test
   ```
6. Stopping the Services
    ```sh
    docker-compose down
   ```

7. Postman Scripts

   [IoT Management.postman_collection.json](./IoT%20Management.postman_collection.json)
