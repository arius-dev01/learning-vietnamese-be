# Viet-Japanese Learning Backend

Backend application for Vietnamese-Japanese learning system built with Spring Boot.

## System Requirements

- Java 17 or higher
- Maven 3.6 or higher  
- MySQL 8.0 or higher
- IntelliJ IDEA (recommended) or any Java IDE

## Database Configuration

Update the database connection information in `src/main/resources/application.properties`:

**Note**: The application will automatically create the database and tables if they don't exist thanks to Hibernate configuration.

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/viet_japanese_learning
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```
