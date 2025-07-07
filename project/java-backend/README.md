# EyeCare Management System - Java Backend

A complete Java Spring Boot backend for the EyeCare Management System.

## Features

- **User Authentication & Authorization** with JWT
- **Role-based Access Control** (Admin, Doctor, Patient)
- **Appointment Management** with conflict detection
- **Medical Records** management
- **Patient & Doctor** profile management
- **RESTful API** design
- **Database Integration** with JPA/Hibernate
- **Security** with Spring Security

## Technology Stack

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** for authentication
- **Spring Data JPA** for database operations
- **JWT** for token-based authentication
- **H2 Database** (development) / **MySQL** (production)
- **Maven** for dependency management

## Quick Start

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- MySQL (for production) or use H2 (for development)

### Installation

1. **Navigate to the Java backend directory:**
   ```bash
   cd java-backend
   ```

2. **Install dependencies:**
   ```bash
   mvn clean install
   ```

3. **Run the application:**
   ```bash
   mvn spring-boot:run
   ```

The server will start on `http://localhost:8080`

### Database Configuration

#### Development (H2 Database)
The application is configured to use H2 in-memory database by default. No additional setup required.

- **H2 Console:** http://localhost:8080/h2-console
- **JDBC URL:** `jdbc:h2:mem:eyecare`
- **Username:** `sa`
- **Password:** `password`

#### Production (MySQL)
To use MySQL, update `application.properties`:

```properties
# Uncomment and configure these lines
spring.datasource.url=jdbc:mysql://localhost:3306/eyecare_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect

# Comment out H2 configuration
# spring.datasource.url=jdbc:h2:mem:eyecare
# spring.datasource.driverClassName=org.h2.Driver
# spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
```

## API Endpoints

### Authentication
- `POST /api/auth/login` - User login
- `POST /api/auth/register` - User registration

### Appointments
- `GET /api/appointments` - Get appointments (with filters)
- `POST /api/appointments` - Create appointment
- `PUT /api/appointments/{id}` - Update appointment
- `DELETE /api/appointments/{id}` - Delete appointment

### Patients
- `GET /api/patients` - Get all patients (Doctor/Admin only)
- `GET /api/patients/{id}` - Get patient details
- `PUT /api/patients/{id}` - Update patient information

### Doctors
- `GET /api/doctors` - Get all doctors
- `GET /api/doctors/{id}` - Get doctor details
- `PUT /api/doctors/{id}` - Update doctor profile

### Medical Records
- `GET /api/medical-records` - Get medical records (with filters)
- `POST /api/medical-records` - Create medical record (Doctor/Admin only)
- `PUT /api/medical-records/{id}` - Update medical record (Doctor/Admin only)

### Health Check
- `GET /api/health` - Server health status

## Default Users

The application creates default users on startup:

### Admin Account
- **Username:** `admin`
- **Password:** `admin123`
- **Role:** Admin

### Doctor Account
- **Username:** `dr.smith`
- **Password:** `doctor123`
- **Role:** Doctor

## Security

- **JWT Authentication** with configurable expiration
- **Role-based authorization** using Spring Security
- **Password encryption** using BCrypt
- **CORS configuration** for frontend integration

## Project Structure

```
src/main/java/com/eyecare/
├── config/          # Configuration classes
├── controller/      # REST controllers
├── dto/            # Data Transfer Objects
├── model/          # JPA entities
├── repository/     # Data repositories
├── security/       # Security configuration
├── service/        # Business logic
└── EyeCareApplication.java
```

## Development

### Running Tests
```bash
mvn test
```

### Building for Production
```bash
mvn clean package
```

### Running the JAR
```bash
java -jar target/eyecare-backend-0.0.1-SNAPSHOT.jar
```

## Frontend Integration

The backend is designed to work with the React frontend. Make sure to:

1. Update the frontend API URL to `http://localhost:8080/api`
2. Start the Java backend on port 8080
3. Start the React frontend on port 5173

## Environment Variables

You can override default configurations using environment variables:

- `JWT_SECRET` - JWT signing secret
- `JWT_EXPIRATION` - JWT expiration time in milliseconds
- `CORS_ALLOWED_ORIGINS` - Allowed CORS origins

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License.