# RESTful API for money transfers between accounts

This is simplified RESTful API for money transfers
between acounts of same ("INTERNAL") or different users ("SEPA"). 

## Tech stack
JDK 11, Spark, Guice, Mybatis, H2 in-memory database


## Build
>gradlew clean build

## Run
Prerequisite: Java JDK 11

>java -jar build/libs/spark-transfer.jar

## SERVER
http://localhost:8080

## Postman collection
Postman collection Spark.postman_collection.json can 
be imported to Postman for easy testing.

## Sample flow

Create customer:
POST http://localhost:8080/customers
{
 "name":"Name Surname"
}

Create account:
POST http://localhost:8080/customers/1/accounts
{
 "name":"LT111222333444555"
}

Create payment:
POST http://localhost:8080/customers/-1/payments
{
"amount": 0.01,
"message": "uz pietus",
"fromAccount":"LT477000000000001",
"toAccount": "LT111222333444555"
}

Execute payment:
PUT http://localhost:8080/customers/-1/payments/1

