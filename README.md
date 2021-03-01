# stock-management-microservices
Demo stock management system for a warehouse.

This project is my first approach to microservices architecture. It is created using two examples of  microservices architecture and divided into branches.

Both projects were created using Spring Cloud Netflix Eureka as a discovery server, Spring Cloud Gateway as tool routing requests to APIs, Spring Cloud LoadBalancer to provide client-side load-balancing in calls to another microservice, Resilience4j for circuit-breaker implementation.

1.	BRANCH MASTER/MICROSERVICES-DEMO

Where microservices are communicating in synchronous way, using RestTemplate.
Individual MySQL database for each service.

2.	BRANCH REACTIVE 

Using Spring WebFlux, where microservices are communicating in asynchronous, non-blocking way using WebClient.
Individual MongoDB database for each service.

# Architecture

Our demo microservices-based system consists of the following modules:

•	cloud-gateway – a module for running Spring Boot application that acts as a proxy/gateway in our architecture.

•	service-registration – embedded discovery server using Spring Cloud Netflix Eureka.

•	customer-service – a module that allows performing CRUD operation on customer repository.

•	product-service - a module that allows performing CRUD operation on product repository.

It communicates with:

-supplier-service in order to connect product with its supplier,

-inventory-service in order to create inventory for each new product added to warehouse supply.

•	invoice-service - a module that allows performing CRUD operation on invoice repository.

It communicates with:

-product-service in order to display product for each invoice,

-inventory-service in order to update inventory for each new invoice added to warehouse supply

•	order-service - a module that allows performing CRUD operation on order repository.

It communicates with:

-product-service in order to display product for each order

-inventory-service in order to update inventory for each new order placed on warehouse supply

•	inventory-service - a module that allows performing CRUD operation on inventory repository.

It communicates with:

-product-service in order to display product for each inventory

# Functionality
When adding new product in supply a new product inventory is created.

When ordering product from supplier, this products inventory is automatically updated.

When order of product is placed by customer, this products inventory is automatically updated. When not enough items in stock – customer is not able to place an order.

Filtering inventory by products, sales reports for each product, customer and supplier.





