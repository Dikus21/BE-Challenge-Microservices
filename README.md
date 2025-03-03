# BE-Challenge-Microservices

This repository demonstrates the transition of a monolithic application into a **microservices architecture** using **Spring Boot, Spring Kafka, Spring Gateway, and Quartz Scheduler**. This architecture enhances **scalability, modularity, and inter-service communication**, ensuring better system performance and reliability.

---

## 📌 **Challenge Chapter 7: Microservices Architecture & API Gateway**

In this chapter, the application was **divided into three microservices**:

1. **Order Service** - Handles order-related operations such as order creation, retrieval, and management.
2. **Product Service** - Manages product-related functionalities, including product listing, inventory updates, and pricing.
3. **User Service** - Responsible for user authentication, registration, and profile management.

### **🔹 Key Implementations**
- **Microservices Separation**: The system is now independently scalable and resilient to failures.
- **Spring Gateway Integration**:
    - Acts as the single entry point for all microservices.
    - Routes requests to respective services.
    - Provides **load balancing** and **security features** (such as authentication & rate limiting).
- **Swagger Documentation Update**:
    - The API documentation was modified to support microservices, enabling developers to **test and explore** endpoints seamlessly.

---

## 📌 **Challenge Chapter 8: Implementing Kafka and Quartz Scheduler**

To enhance **real-time communication and scheduling**, **Spring Kafka** and **Quartz Scheduler** were integrated into the microservices architecture.

### **🔹 Key Implementations**
#### **1️⃣ Kafka for Event-Driven Communication**
Kafka was introduced for **asynchronous messaging** between microservices, improving **scalability and decoupling**. The following Kafka-based features were implemented:
- **Producer & Consumer Setup**: Services now publish and consume events efficiently.
- **Subscription Handling**: Users can subscribe to notifications for product promotions.
- **Promo Notification Mechanism**: A Kafka topic was created to **broadcast promotional notifications** to subscribed users.

#### **2️⃣ Quartz for Task Scheduling**
Quartz Scheduler was integrated for handling **automated scheduled tasks**, such as:
- **Periodic Promo Notifications**: Automatically triggers scheduled messages to notify users about discounts or promotions.
- **Order Processing Jobs**: Allows background tasks, such as order status updates.

---

## ⚙ **Technology Stack**
- **Spring Boot** - Framework for developing microservices.
- **Spring Cloud Gateway** - API Gateway for routing, load balancing, and security.
- **Spring Kafka** - Messaging system for inter-service communication.
- **Quartz Scheduler** - Job scheduling for automated tasks.
- **Postman** - API documentation and testing tool.
