# Design Principles and Patterns

This document outlines how the refactored Internship Placement System adheres to SOLID principles and employs key design patterns to achieve maintainable, scalable, and flexible software architecture.

## SOLID Principles

### Single Responsibility Principle (SRP)
Each class has a single, well-defined responsibility:
- **Repositories** (e.g., `CsvUserRepository`): Handle data persistence and retrieval for specific entities.
- **Services** (e.g., `UserService`): Encapsulate business logic related to user operations.
- **Handlers** (e.g., `StudentMenuHandler`): Manage user interface interactions for specific user types.
- **Main Class** (`InternshipPlacementSystem`): Orchestrate the application flow without handling business logic or UI details.

### Open/Closed Principle (OCP)
Classes are open for extension but closed for modification:
- New user types can be added by implementing `IMenuHandler` without changing existing code.
- New data sources can be integrated by implementing repository interfaces.
- Services can be extended with new features without altering core logic.

### Liskov Substitution Principle (LSP)
Subclasses can be substituted for their base classes without affecting correctness:
- `Student`, `CompanyRepresentative`, and `CareerCenterStaff` properly extend `User` and can be used interchangeably where `User` is expected.
- Repository implementations (`CsvUserRepository`, etc.) can replace each other via their interfaces.

### Interface Segregation Principle (ISP)
Clients depend only on the methods they use:
- Separate interfaces for different concerns: `IUserRepository`, `IInternshipRepository`, `IApplicationRepository`, `IMenuHandler`.
- No bloated interfaces; each interface is focused on a specific aspect of functionality.

### Dependency Inversion Principle (DIP)
High-level modules depend on abstractions, not concretions:
- Services depend on repository interfaces, not concrete implementations.
- Handlers depend on service abstractions.
- Dependency injection is used to provide concrete implementations at runtime.

## Design Patterns

### Repository Pattern
- **Purpose**: Abstracts data access logic, providing a uniform interface for data operations.
- **Implementation**: `IUserRepository`, `CsvUserRepository`, etc., encapsulate CRUD operations for entities.
- **Benefits**: Decouples business logic from data storage, enables easy switching between data sources (e.g., CSV to database).

### Service Layer Pattern
- **Purpose**: Encapsulates business logic and acts as an intermediary between controllers/handlers and repositories.
- **Implementation**: `UserService`, `InternshipService`, `ApplicationService` contain domain logic and coordinate with repositories.
- **Benefits**: Centralizes business rules, improves testability, and maintains separation of concerns.

### Strategy Pattern
- **Purpose**: Defines a family of algorithms (menu handling strategies) and makes them interchangeable.
- **Implementation**: `IMenuHandler` interface with concrete implementations (`StudentMenuHandler`, `CompanyRepMenuHandler`, `CareerStaffMenuHandler`).
- **Benefits**: Allows dynamic selection of behavior based on user type, promotes extensibility for new user roles.

### Dependency Injection (DI)
- **Purpose**: Provides dependencies to classes from external sources rather than creating them internally.
- **Implementation**: Constructor injection in services and handlers; main class instantiates and injects dependencies.
- **Benefits**: Reduces coupling, improves testability (easy mocking), and enables flexible configuration.

## Security Design

### Password Security
- **Hashing**: SHA-256 with random salt for secure password storage
- **No Plain Text**: Passwords never stored in plain text in memory or persistent storage
- **Migration Support**: Backward compatibility for existing plain text passwords

### Input Validation
- **Comprehensive Checks**: All user inputs validated at service layer
- **Business Rules**: Domain-specific validation (GPA ranges, major lists, etc.)
- **Error Prevention**: Invalid data rejected before persistence

## Testing Design

### Manual Testing Approach
- **Comprehensive Procedures**: Detailed step-by-step testing scenarios covering all system functionality
- **Expected Behavior Documentation**: Clear validation criteria for correct system responses
- **Edge Case Coverage**: Thorough testing of error conditions, boundary values, and invalid inputs
- **Workflow Validation**: End-to-end verification of complete business processes
- **Data Persistence Testing**: Verification that changes survive application restarts
- **User Experience Validation**: Testing of UI navigation, error messages, and professional formatting

## Conclusion

The refactored system demonstrates a clean, modular architecture that adheres to SOLID principles and leverages proven design patterns. This results in:
- High cohesion within classes
- Loose coupling between components
- Ease of maintenance and extension
- Improved testability and reusability

The architecture supports future enhancements, such as adding new user types, integrating different data sources, or implementing additional features, without requiring extensive modifications to existing code.