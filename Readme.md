# Bidding System Backend
## Overview
This is a microservices-based backend for a bidding system. It enables vendors to create and manage auctions for products and allows customers to view and place bids on products. The system automatically selects the auction winner at the end of the bidding slot and notifies them via email.

## Features
##### Vendor Functionality:

- Create and manage auctions for products.
- Set base prices for products.
- Extend the auction end time if needed.
##### Customer Functionality:

- Search for active auctions by category or using custom filters.
- Place bids on products, ensuring the bid is higher than or equal to the base price.
  
Auction Management:

Each auction has a start and end time.
Automatic winner selection based on the highest bid.
Email notifications sent to the auction winners.

Assumptions
There are two types of users: VENDOR and CUSTOMER.
Vendors: Create auctions for their products.
Customers: Browse active auctions and place bids.
A bid must be equal to or greater than the product's base price; otherwise, an error will be thrown.
The auction winner is notified automatically via email once the auction ends.


| Technology Stack |  |
| ------ | ------ |
| Backend | http://localhost:8080/auction-system/api/docs/swagger-ui/index.html#/account-controller/createAccount |
| Database | http://localhost:8080/auction-system/api/ |
| Authentication & Authorization: | http://localhost:8080/h2-console/login.do?jsessionid=90ac46d456c598727489bf24ad70248f |
| Email Service: | Java mail service |


**How to Run the Application**

- Clone the repository:
```sh
git clone <repository-url>
```
- Navigate to the project directory:
```sh
cd bidding-system
```

Start the application:

|        | URL |
| ------ | ------ |
| Swagger | http://localhost:8080/auction-system/api/docs/swagger-ui/index.html#/account-controller/createAccount |
| Application-URL | http://localhost:8080/auction-system/api/ |
| H2-Database URL Drive | http://localhost:8080/h2-console/login.do?jsessionid=90ac46d456c598727489bf24ad70248f |
