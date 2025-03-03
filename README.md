# BE-Challenge-Microservices

This chapter focuses on transitioning the application into a microservices architecture by implementing Spring Kafka and Spring Gateway to enhance scalability, modularity, and communication between services.

## Challenge Chapter 7

The application was divided into three microservices: Order Service, Product Service, and User Service. Each microservice is responsible for handling specific functionalities, such as order management, order management, and user management, respectively. By separating the application into microservices, the system can scale independently, improve fault tolerance, and facilitate team collaboration.

Spring Gateway was introduced to provide a unified entry point for the microservices, enabling API routing, load balancing, and security features. By implementing Spring Gateway, the application can efficiently manage incoming requests, distribute traffic across services, and enforce security policies, ensuring system reliability and performance. Additionally, Swagger documentation was updated to reflect the new microservices architecture, providing users with an interactive API interface for better usability and testing.