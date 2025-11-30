# Hotel Desktop App (JavaFX)

A desktop hotel management application built with Java and JavaFX. The project follows a layered architecture with clear separation between UI (controllers / FXML), domain models, repositories (data access), DTOs, and services. Maven is used as the build tool and the repository includes the Maven wrapper for easy builds.

> NOTE: This README was created from the repository layout and typical patterns used in JavaFX/MVC applications. Verify the package names and database configuration in `src/main/resources` or `src` before running. Adjust database connection details as needed.

---

Table of contents
- Overview
- Key features
- Architecture and packages
- Tech stack
- Project structure
- Quick start (build & run)
- Database: schema & sample data
- Running from IDE (IntelliJ / Eclipse)
- How the pieces interact (typical flows)
- Development notes & best practices
- Packaging & distribution
- Interview-ready talking points
- Contributing
- License

---

Overview
--------
This application provides a desktop UI to manage hotel operations such as:
- Managing rooms (create / edit / list / availability)
- Managing guests (create / edit)
- Creating and managing reservations (booking, check-in/check-out)
- Handling simple payments/invoices
- Viewing lists and basic reports

The UI is built with JavaFX (FXML + controllers). Business logic is implemented in service classes. Repositories handle persistence. DTOs are used to transfer data between layers and decouple UI and persistence models.

Key features
------------
- JavaFX-based desktop UI (FXML + CSS)
- Layered architecture: controllers → services → repositories
- DTOs to decouple UI from persistence entities
- ObservableLists and JavaFX properties for responsive UI updates
- Maven build with included wrapper (mvnw)
- Basic styling with CSS

Architecture & packages
-----------------------
The project uses a Model–View–Controller (MVC) style and additional layers:

- model (or domain)
  - Domain entities: Room, Guest, Reservation, Payment/Invoice, etc.
- dto
  - Data Transfer Objects used between UI/services and to validate user input
- repository
  - Interfaces and implementations for data access (CRUD)
  - Could be JDBC, plain file, or an embedded DB access layer (check the code)
- service
  - Business logic, validation and orchestration across repositories
- controller
  - JavaFX controllers tied to FXML views. Accept user input, call services, update UI.
- util (or common)
  - Helpers: DB connection management, mappers (entity ↔ dto), date/number formatters
- app (or main)
  - JavaFX Application class (entrypoint) to start the application and load the initial scene
- resources
  - FXML files, CSS stylesheets, SQL or initial data scripts (if any)

Tech stack
----------
- Java (version depends on pom.xml; commonly 11 or later)
- JavaFX (UI)
- Maven (build) + mvnw wrapper
- CSS for theming JavaFX
- Persistence: may be SQLite, H2, or plain JDBC (check `src` for exact implementation)
- Common libraries (if any): check `pom.xml` for dependencies (e.g., logging, JDBC driver)

Project structure (typical)
---------------------------
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
      - fxml/
      - css/
      - db/ (optional)
      - application.properties or config files (optional)

Quick start — build & run
------------------------
Prerequisites:
- JDK 11+ (match the project's configured Java in `pom.xml`)
- Git (to clone)
- If the project uses an external DB, ensure the DB server is running and credentials configured.

Clone the repository:
```bash
git clone https://github.com/OltVr/Hotel-Desktop-App-with-JavaFX.git
cd Hotel-Desktop-App-with-JavaFX
```

Build with Maven wrapper (recommended — uses included mvnw):
Linux / macOS:
```bash
./mvnw clean package
```
Windows (PowerShell/CMD):
```powershell
mvnw.cmd clean package
```

Run with Maven (if JavaFX plugin configured) or by running the jar:
- If the project uses javafx-maven-plugin:
```bash
./mvnw javafx:run
```
- Or run the packaged jar:
```bash
java -jar target/your-app-name.jar
```
Replace `your-app-name.jar` with the generated artifact from the `target` folder.

If you run into JavaFX runtime errors, ensure JavaFX modules are available for the JDK used or use the javafx-maven-plugin to fetch JavaFX dependencies.

Database: schema & sample data
------------------------------
The repository may use an embedded DB (H2/SQLite) or an external RDBMS. If you do not have a DB script in `src/main/resources`, you can use this sample schema to initialize a lightweight DB for local development.

Sample SQL schema (SQLite / H2 compatible)
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
  status VARCHAR(20) -- e.g. AVAILABLE, OCCUPIED, RESERVED
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

Sample data
```sql
INSERT INTO room (number, type, price_per_night, status) VALUES ('101', 'Single', 50.00, 'AVAILABLE');
INSERT INTO room (number, type, price_per_night, status) VALUES ('102', 'Double', 80.00, 'AVAILABLE');

INSERT INTO guest (first_name, last_name, phone, email) VALUES ('John', 'Doe', '555-0101', 'john@example.com');

INSERT INTO reservation (guest_id, room_id, start_date, end_date, status, total_price) VALUES (1, 1, '2025-06-01', '2025-06-05', 'CONFIRMED', 200.00);
```

If the project has an `init.sql` or `schema.sql` file in `src/main/resources`, use that instead.

Running from an IDE
-------------------
- Open the project in IntelliJ IDEA (recommended) or Eclipse.
- Import as Maven project.
- Ensure the project SDK matches the Java version used in `pom.xml`.
- Locate the `Main` application class (extends `javafx.application.Application`) and run it.
- If JavaFX is not on the module path, configure the run configuration to include JavaFX modules or use the javafx-maven-plugin to run.

How the pieces interact (typical flows)
--------------------------------------
1. UI: User clicks “Create Reservation” in a JavaFX scene (FXML).
2. Controller: ReservationController reads form fields, validates minimal input, builds ReservationDTO.
3. Service: ReservationService checks availability (queries RoomRepository / ReservationRepository), calculates total, performs business checks.
4. Repository: ReservationRepository persists reservation and updates room status.
5. Controller: Updates the ObservableList bound to a TableView and shows success dialog.

Concurrency & responsiveness:
- All blocking IO (DB access) should run on background threads (JavaFX Task / Service).
- UI updates must be performed on the JavaFX Application Thread (Platform.runLater or Task message bindings).

Development notes & best practices
---------------------------------
- Keep controllers thin: delegate heavy logic to services.
- Use DTOs to validate and sanitize user input before persisting.
- Use PreparedStatements (if JDBC) to avoid SQL injection.
- Use transactions for multi-step operations (e.g., create reservation + update room).
- Use ObservableList and JavaFX properties for automatic UI updates.
- Consider adding unit tests to services and integration tests for repositories (in-memory DB like H2).
- Add logging with SLF4J + Logback for production-level logs.

Packaging & distribution
------------------------
- Use Maven to build a jar. For a self-contained native distribution, consider `jlink` / `jpackage` or the javafx-maven-plugin to build platform-specific bundles.
- If distributing to end-users, bundle required JavaFX modules or build a native package to avoid requiring users to install Java.

Interview-ready talking points
------------------------------
When explaining this project in an interview, open these files quickly:
- Main Application class (show app start & scene loading)
- One FXML and its controller (show event handling and service call)
- A Service class (show business logic and repository usage)
- A Repository implementation (show DB access technique)
- pom.xml (show dependencies and build management)

Short elevator pitch:
- “This is a JavaFX desktop hotel management app with a layered architecture: UI (FXML + controllers), Services that implement business logic, Repositories for persistence, and DTOs to decouple layers. Maven manages builds and dependencies. The app demonstrates separation of concerns, data binding via ObservableList/Properties, and background DB operations to keep the UI responsive.”

Common interview Q&A (short)
- Q: Why separate service and repository?
  - A: Repositories are for data access, services encapsulate business rules and coordinate repositories. This separation improves testability and single-responsibility adherence.
- Q: How do you keep UI responsive during DB calls?
  - A: Use JavaFX Task or Service to run DB calls off the Application Thread; use Platform.runLater or bound properties to update UI afterward.
- Q: How would you test this app?
  - A: Unit test services (mock repositories), integration test repositories using an in-memory DB like H2, and use test fixtures for DTOs.

Contributing
------------
1. Fork the repo.
2. Create a feature branch: `git checkout -b feature/your-feature`
3. Make changes and add tests.
4. Commit and push your branch.
5. Open a pull request and describe the changes.

Before opening PRs:
- Ensure code is formatted consistently (use project's code style).
- Add or update unit tests if adding logic.
- Verify no hardcoded credentials are added.

License
-------
Add a license file (LICENSE) appropriate for your project (e.g., MIT, Apache-2.0). If you already have a preferred license, include it. If not, consider MIT for permissive usage.

What I added and what you should check
-------------------------------------
I prepared this README to be comprehensive for a JavaFX hotel management application following your repo structure and your description (model-controller-repo-dto-services). Please verify:
- The exact package names and main Application class name in `src/main/java`.
- The database type and the presence of any `application.properties` or config file in `src/main/resources`.
- Whether the project uses a JavaFX maven plugin; update run instructions accordingly in this README.

If you want, I can:
- Inspect the repository `src/` to extract exact file/class names and update the README to reference the real Main class, controllers, and services (I can open and list those files for you).
- Add example screenshots or embed small code snippets from the real project (once I inspect specific files).

--- 

Good luck with your interview — if you want I can now tailor a one-page cheat sheet of 5–7 sentences and the 5 files to open during the interview so you can quickly show the structure and answer likely questions.
