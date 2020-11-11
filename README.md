### This example contains 1 of 12 microservices -"parties-ms" and 1 of 2 API gateways - "customer-api"

### Project structure

##### module prefix "core" - library  
##### module suffix "protocol" - same as DDD shared kernel
##### module suffix "data" - DB layer. *-data contains DB schema src/main/sql/schema.sql 
##### module suffix "ms" - microservice
##### module suffix "api" - API Gateway

### Internal communication between microservices - Kafka
### API Gateway - json

### SPA frontend customer-api-spa

