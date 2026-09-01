# Store Application

The Store application keeps track of customers, orders, and products in a PostgreSQL database.

## Assumptions

This README assumes you're using a POSIX environment. It's possible to run the application on Windows as well:

- Instead of `./gradlew`, use `gradlew.bat`
- The syntax for creating the Docker container is different on Windows
- You can also install PostgreSQL on bare metal if preferred

## Prerequisites

The application uses PostgreSQL 16.2 running on `localhost:5433` (note the non-standard port).

It assumes:

- Username: `admin`
- Password: `admin`
- Database: `store`

You can start the PostgreSQL instance using Docker:

```shell
docker run -d \
  --name postgres \
  --restart always \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=store \
  -v postgres:/var/lib/postgresql/data \
  -p 5433:5432 \
  postgres:16.2 \
  postgres -c wal_level=logical
```

Alternatively, the included `docker-compose.yml` can be used:

```shell
docker compose up -d
```

## Running the Application

Start the application with:

```shell
./gradlew bootRun
```

On Windows:

```powershell
.\gradlew.bat bootRun
```

Liquibase automatically manages the database schema and migrations when the application starts.

Sample customers, orders, and products are provided.

Additional sample data can be created by following the documentation in `utils/README.md`.

## Running Tests

Run the complete test suite with:

```shell
./gradlew clean test
```

On Windows:

```powershell
.\gradlew.bat clean test
```

The project also generates a JaCoCo test coverage report.

## Data Model

### Customer

A customer has:

- An ID
- A name
- Zero or more orders

### Order

An order has:

- An ID
- A description
- A customer
- One or more products

### Product

A product has:

- An ID
- A description
- Zero or more associated orders

Orders and products have a many-to-many relationship represented by the `order_product` join table.

The API uses DTOs to avoid serializing circular entity relationships.

## API

The existing API endpoints are preserved as defined by the supplied API specification.

### Customers

#### Get all customers

```http
GET /customer
```

#### Search customers by name

```http
GET /customer?name=<substring>
```

The search is case-insensitive and matches the supplied value as a substring of the customer's name.

#### Create a customer

```http
POST /customer
```

### Orders

#### Get all orders

```http
GET /order
```

Orders include their associated customer and products.

#### Get an order by ID

```http
GET /order/{id}
```

Returns `404 Not Found` when the order does not exist.

#### Create an order

```http
POST /order
```

An order is created with a customer and one or more product IDs.

### Products

#### Get all products

```http
GET /products
```

Each product includes the IDs of orders containing that product.

#### Get a product by ID

```http
GET /products/{id}
```

Each product includes the IDs of orders containing that product.

#### Create a product

```http
POST /products
```

## Performance

The production scenario described in the assessment involves high latency between the application server and database server.

The initial entity relationships use lazy loading. When returning DTOs containing related entities, this can result in N+1 database queries.

To reduce unnecessary database round trips, the order, customer, and product read operations use JPA `EntityGraph` fetching where appropriate.

This allows the relationships required by the API response to be loaded as part of the database query rather than triggering additional queries for each individual entity.

This is particularly important when database latency is high, because reducing the number of database round trips can have a significant impact on response time.

## Database Migrations

Liquibase is used to manage database schema changes.

Product functionality is introduced through a Liquibase migration which creates:

- The `product` table
- The `order_product` join table
- An index on `order_product.product_id`

Sample product data and order-product relationships are also provided through a subsequent migration.

## Docker

The application can be packaged as a Docker image.

Build the application:

```shell
./gradlew bootJar
```

Build the Docker image:

```shell
docker build -t store:local .
```

The Docker image uses Java 17 and runs the Spring Boot application on port `8080`.

## CI/CD

The project includes a GitHub Actions CI pipeline.

The pipeline:

1. Checks out the source code
2. Sets up Java 17
3. Starts PostgreSQL 16.2 as a service
4. Runs the Gradle test suite
5. Builds the Spring Boot application
6. Builds the Docker image
7. Publishes the Docker image to GitHub Container Registry when changes are pushed to `main`

This provides automated verification before the application is delivered as a Dockerized image.

## Project Structure

```text
src/
├── main/
│   ├── java/
│   │   └── com/example/store/
│   │       ├── controller/
│   │       ├── dto/
│   │       ├── entity/
│   │       ├── mapper/
│   │       └── repository/
│   └── resources/
│       └── db/
│           └── changelog/
├── test/
│   └── java/
│       └── com/example/store/
├── utils/
├── Dockerfile
├── docker-compose.yml
├── build.gradle
└── OpenAPI.yaml
```

## Assessment Tasks

The following functionality has been implemented:

- **Task 1:** Find an order by ID
- **Task 2:** Search customers by a case-insensitive name substring
- **Task 3:** Optimize database access for read endpoints to reduce round trips
- **Task 4:** Add products, product creation/retrieval, order-product relationships, and product information to order responses
- **Bonus:** Add a GitHub Actions CI/CD pipeline and Docker image delivery

The implementation preserves the existing `/order` and `/customer` API paths defined by the assessment rather than changing the API contract.

## API Documentation

The complete API specification is available in `OpenAPI.yaml`.

## Notes

The project represents a production application, so particular attention has been paid to database access and minimizing unnecessary network round trips.

The assessment intentionally leaves some implementation decisions open. The implementation choices above are intended to balance correctness, maintainability, and performance while keeping the existing API contract intact.