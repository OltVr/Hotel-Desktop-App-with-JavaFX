# Hotel Desktop App (JavaFX)

A desktop hotel management application built with Java and JavaFX. The project follows a layered architecture with a clear separation between UI (FXML + controllers), domain models, repositories (data access), DTOs, and services. Maven is used as the build tool and the repository includes the Maven wrapper for easy builds.

> NOTE: This README was created from the repository layout and typical patterns used in JavaFX/MVC applications. Verify the package names and database configuration in `src/main/resources` or `src` before running. Adjust database connection details as needed.

## Table of contents
- Overview
- Key features
- Architecture
- Project structure
- Requirements
- Build & Run
- Database (schema & sample data)
- Running from an IDE
- Contributing
- License

## Overview
This application provides a desktop UI for basic hotel operations:
- Manage rooms (create / edit / list / availability)
- Manage guests (create / edit)
- Create and manage reservations (book, check-in, check-out)
- Process simple payments/invoices
- View lists and basic reports

UI is built with JavaFX (FXML + controllers). Business logic is encapsulated in service classes. Data access is handled by repositories and DTOs are used to pass data between layers.

## Key features
- JavaFX-based UI (FXML + CSS)
- Layered architecture: controllers → services → repositories
- DTOs for decoupling UI and persistence models
- ObservableList and JavaFX properties for responsive UI updates
- Maven build with included wrapper (mvnw)

## Architecture
The project follows a layered approach:
- Presentation (JavaFX FXML, controllers) — handles user interaction and binds UI to data
- Service layer — business rules, validation, and orchestration of repositories
- Repository layer — data access (CRUD) and persistence details
- Model / DTOs — domain entities and transfer objects
- Utilities — DB connection management, mappers, formatters

Typical domain entities: Room, Guest, Reservation, Payment/Invoice. DTOs are used to present UI-friendly representations and to validate input.

## Project structure (typical)
- pom.xml
- mvnw, mvnw.cmd
- src/
  - main/
    - java/
      - com.yourorg.hotel/
        - app/ (Main Application class)
        - controller/ (JavaFX controllers)
        - model/ (domain entities)
        - dto/ (DTO classes)
        - repository/ (interfaces and implementations)
        - service/ (business logic)
        - util/ (DB, mappers, validators)
    - resources/
      - fxml/ (views)
      - css/ (stylesheets)
      - db/ (optional SQL scripts or sample data)

(Adjust actual package paths to match the code in `src/main/java`.)

## Requirements
- JDK 11+ (or the Java version indicated in pom.xml)
- Maven (optional; mvnw allows building without a local Maven)
- If using an external DB: the corresponding DB server and JDBC driver (check pom.xml)

## Build & Run
Clone the repository:
```bash
git clone https://github.com/OltVr/Hotel-Desktop-App-with-JavaFX.git
cd Hotel-Desktop-App-with-JavaFX
```

Build with the included Maven wrapper:
Linux / macOS:
```bash
./mvnw clean package
```
Windows:
```powershell
mvnw.cmd clean package
```

Run with Maven (if configured) or run the produced jar:
```bash
./mvnw javafx:run
# or
java -jar target/<artifact-id>-<version>.jar
```
Replace `<artifact-id>-<version>.jar` with the actual artifact name produced in `target/`.

If you encounter JavaFX runtime/module errors, either use the javafx-maven-plugin or ensure JavaFX modules are available for your JDK (or use a matching JavaFX SDK).

## Database — schema & sample data
The project may use an embedded DB (H2/SQLite) or external DB. If no DB scripts exist in `src/main/resources`, use this sample schema to initialize a local DB for development.

Sample SQL (SQLite / H2 compatible):
```sql
CREATE TABLE guest (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  first_name VARCHAR(100) NOT NULL,
  last_name VARCHAR(100) NOT NULL,
  phone VARCHAR(30),
  email VARCHAR(150)
);

CREATE TABLE room (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  number VARCHAR(20) NOT NULL UNIQUE,
  type VARCHAR(50),
  price_per_night DECIMAL(10,2),
  status VARCHAR(20)
);

CREATE TABLE reservation (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  guest_id INTEGER NOT NULL,
  room_id INTEGER NOT NULL,
  start_date DATE NOT NULL,
  end_date DATE NOT NULL,
  status VARCHAR(20),
  total_price DECIMAL(10,2),
  FOREIGN KEY (guest_id) REFERENCES guest(id),
  FOREIGN KEY (room_id) REFERENCES room(id)
);

CREATE TABLE payment (
  id INTEGER PRIMARY KEY AUTOINCREMENT,
  reservation_id INTEGER NOT NULL,
  amount DECIMAL(10,2) NOT NULL,
  date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  method VARCHAR(50),
  FOREIGN KEY (reservation_id) REFERENCES reservation(id)
);
```

Sample data:
```sql
INSERT INTO room (number, type, price_per_night, status) VALUES ('101', 'Single', 50.00, 'AVAILABLE');
INSERT INTO room (number, type, price_per_night, status) VALUES ('102', 'Double', 80.00, 'AVAILABLE');

INSERT INTO guest (first_name, last_name, phone, email) VALUES ('John', 'Doe', '555-0101', 'john@example.com');

INSERT INTO reservation (guest_id, room_id, start_date, end_date, status, total_price) VALUES (1, 1, '2025-06-01', '2025-06-05', 'CONFIRMED', 200.00);
```

If the repository contains `schema.sql` or `init.sql`, prefer those scripts.

## Running from an IDE
- Import the project as a Maven project in IntelliJ IDEA or Eclipse.
- Ensure the project SDK matches the Java version in pom.xml.
- Locate the main Application class (extends javafx.application.Application) and run it.
- If JavaFX modules are missing, configure the run configuration to add JavaFX or use the Maven plugin to run.

## Contributing
1. Fork the repository.
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Implement changes and add tests.
4. Commit and push your branch.
5. Open a pull request with a description of changes.

Guidelines:
- Keep controllers thin; push business logic to services.
- Use PreparedStatements if using JDBC.
- Add tests for service logic and integration tests for repositories (H2 or SQLite).
- Avoid committing sensitive configuration or credentials.

## License
Add a LICENSE file to the repository. If unsure, consider the MIT License for permissive usage.

## Notes
- Verify the exact package names, main Application class, and the database configuration in `src/main/resources` before running.
- Update this README to reflect the actual DB driver, artifact name and any project-specific run instructions found in `pom.xml`.

```
