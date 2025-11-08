# Multi-Tenant Warehouse Management System

A backend system for managing physical storage warehouses across multiple warehouse companies (tenants). Each tenant can manage their warehouses, storage units, customers, and bookings with complete data isolation.

## Business Context

This system enables warehouse companies to rent storage units to businesses (customers) who need to store their goods. The platform handles multiple warehouse companies simultaneously while ensuring complete data isolation between tenants.

## Core Entities

- **Tenant**: Warehouse companies using the system
- **Warehouse**: Physical locations owned by tenants
- **Storage Unit**: Individual storage spaces with capacity tracking and status management
- **Customer**: Businesses renting storage from tenants
- **Booking**: Active rentals linking customers to storage units
- **Stored Item**: Items customers have placed in their rented units (planned)

## Key Features

### Multi-Tenancy
- Complete data isolation between tenants using tenant-scoped queries
- All operations validate tenant ownership before allowing access
- Prevents cross-tenant data leakage at the database query level

### Concurrency Control
- Optimistic locking on bookings to prevent race conditions
- Handles double-booking scenarios when multiple customers attempt to book the same unit
- Version control on storage units and bookings for conflict detection

### API Endpoints

#### Tenants
- `GET /api/v1/tenants/profile` - Get tenant profile
- `GET /api/v1/tenants/stats` - Get tenant statistics (warehouses, customers, bookings, occupancy rate)

#### Warehouses
- `GET /api/v1/warehouses` - List all warehouses for a tenant
- `GET /api/v1/warehouses/{id}` - Get specific warehouse details
- `POST /api/v1/warehouses` - Create new warehouse
- `PATCH /api/v1/warehouses/{id}` - Update warehouse details
- `DELETE /api/v1/warehouses/{id}` - Delete warehouse

#### Storage Units
- `GET /api/v1/storage` - List all storage units
- `GET /api/v1/storage/available` - Get available units with minimum capacity filter
- `GET /api/v1/storage/{id}` - Get specific unit details
- `POST /api/v1/storage` - Create new storage unit
- `PATCH /api/v1/storage/{id}` - Update unit (capacity, status)
- `DELETE /api/v1/storage/{id}` - Delete storage unit

#### Customers
- `GET /api/v1/customers` - List all customers for a tenant
- `GET /api/v1/customers/{id}` - Get specific customer
- `POST /api/v1/customers` - Create new customer
- `PATCH /api/v1/customers/{id}` - Update customer details
- `DELETE /api/v1/customers/{id}` - Delete customer

#### Bookings
- `GET /api/v1/bookings` - List all bookings for a tenant
- `GET /api/v1/bookings/{id}` - Get specific booking
- `GET /api/v1/bookings/expiring` - Get bookings expiring by a given date
- `POST /api/v1/bookings` - Create new booking
- `PATCH /api/v1/bookings/{id}` - Update booking (status, end date, rate)
- `DELETE /api/v1/bookings/{id}` - Delete booking

## Technical Stack

- **Framework**: Spring Boot 3.5.5
- **Language**: Java 21
- **Database**: PostgreSQL
- **ORM**: Spring Data JPA with Hibernate
- **Build Tool**: Maven
- **Mapping**: MapStruct for DTO-Entity conversion

## Architecture Decisions

### Multi-Tenant Data Isolation
Instead of using separate databases or schemas per tenant, this implementation uses a shared database with tenant ID columns. All repository queries are tenant-scoped, ensuring data isolation at the application level.

**Why this approach?**
- Simpler deployment and maintenance
- Cost-effective for moderate tenant counts
- Easier to implement cross-tenant analytics if needed later
- Query-level filtering prevents accidental data leakage

### Concurrency Handling
The system uses optimistic locking with JPA's `@Version` annotation on storage units and bookings. When a booking conflict occurs (two customers trying to book the same unit), the second transaction fails with an `OptimisticLockException`.

**Implementation:**
```java
@Version
private Integer version = 0;
```

When a conflict is detected, the system throws a `BookingConflictException` with a user-friendly message, allowing the customer to select another unit.

### Status Management
Storage units track their status through an enum:
- `AVAILABLE`: Unit is ready for booking
- `BOOKED`: Unit has been reserved
- `OCCUPIED`: Unit is currently in use
- `MAINTENANCE`: Unit is temporarily unavailable

Bookings track their lifecycle:
- `ACTIVE`: Current rental period
- `CANCELLED`: Booking was terminated early
- `COMPLETED`: Rental period ended normally

### Validation and Business Rules
- Capacity reductions are prevented on occupied or booked units
- Only positive capacity values are allowed
- Storage units can only be booked if in AVAILABLE status
- Customers must belong to the same tenant as the storage unit they're booking

## Database Schema

The system uses PostgreSQL with the following key relationships:
- Tenants own multiple warehouses
- Warehouses contain multiple storage units
- Tenants have multiple customers
- Customers create bookings for storage units
- Bookings reference both customers and storage units

All foreign key relationships maintain referential integrity while allowing lazy loading for performance.

## Running the Application

### Prerequisites
- Java 21
- Docker (for PostgreSQL)
- Maven

### Setup

1. Start PostgreSQL using Docker Compose:
```bash
docker-compose up -d
```

2. The application expects a database with these credentials:
   - Database: `warehouse_db`
   - Port: `5433`

3. Run the application:
```bash
./mvnw spring-boot:run
```

The application will start on `http://localhost:8080`.

## Project Structure

```
src/main/java/com/warehouse/
├── bookings/           # Booking management
├── customers/          # Customer management
├── storage/            # Storage unit operations
├── tenants/            # Tenant profile and statistics
├── warehouses/         # Warehouse management
└── common/
    ├── dto/            # Data Transfer Objects
    ├── exceptions/     # Custom exceptions
    ├── mapper/         # MapStruct mappers
    └── response/       # Response handlers
```

## Work in Progress

This project is actively being developed. Current phase focuses on API layer completion and advanced backend concepts.

### Completed
- Core domain models and relationships
- Multi-tenant data isolation
- Basic CRUD operations for all entities
- Optimistic locking for booking conflicts
- Tenant statistics and utilization metrics
- Global exception handling
- Standardized API responses

### In Progress
- Input validation with Spring Validation
- Integration tests with TestContainers
- API documentation with OpenAPI/Swagger

### Planned Features
- **Event-Driven Architecture**: Async notifications for booking expiration
- **Caching Layer**: Redis integration for frequently accessed data
- **Advanced Concurrency**: Distributed locking for high-traffic scenarios
- **Stored Items**: Track individual items within storage units
- **Audit Logging**: Track all changes to critical entities
- **Rate Limiting**: API rate limiting per tenant
- **Metrics and Monitoring**: Prometheus and Grafana integration
- **Search Functionality**: Elasticsearch for advanced warehouse/unit search

## Learning Goals

This project demonstrates practical knowledge of:
- Multi-tenant architecture patterns
- Handling race conditions in distributed systems
- Database design for business domains
- RESTful API design
- Transaction management and data consistency
- Error handling and validation strategies

## License

MIT License - See LICENSE file for details
