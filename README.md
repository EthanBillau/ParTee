# ParTee - Golf Course Reservation System

## Team Members
- Ethan Billau (ebillau)
- Anoushka Chakravarty (chakr181)
- Nikhil Kodali (kodali3)
- Connor Landzettl (clandzet)
- Aman Wakankar (awakanka)

---

## Project Overview

ParTee is a comprehensive golf course reservation system with secure user authentication, dynamic server configuration, and an intuitive GUI. The system supports user account management, tee time booking, reservation tracking, events, course configuration management, and an AI-powered chatbot assistant. All components are thread-safe and include persistent data storage with encrypted passwords.

### Key Features

- **Secure Authentication**: BCrypt password hashing (work factor 12) with constant-time comparison
- **Flexible Login**: Users can authenticate with either username or email address
- **Dynamic Configuration**: Server host/port configurable via properties file
- **AI Assistant**: Integrated Gemini-powered chatbot for golf-related questions
- **Thread-Safe Operations**: ReentrantReadWriteLock for concurrent client support
- **Persistent Storage**: File-based data persistence across sessions
- **Multi-Client Support**: Server handles multiple simultaneous connections

---

## Development Timeline

### November 2025
- **Phase 1**: Core database backend implementation
  - Singleton Database pattern with thread-safe operations
  - User, Reservations, TeeTime, Event, and CourseSettings models
  - File persistence for all data types
  - Comprehensive test coverage (150+ tests)
  - All interfaces properly implemented per requirements
  
- **Phase 2**: Client-server architecture
  - Multi-threaded server with ServerWorker per client
  - Protocol-based communication
  - Network layer with graceful error handling
  - Client and Server components with full interfaces
  
- **Phase 3**: GUI implementation
  - Swing-based user interfaces for all operations
  - Login, registration, and password recovery
  - Reservation management and tee time booking
  - Admin controls and event approval
  - AI chatbot integration

### December 2025
- **Security Enhancements**
  - BCrypt password hashing implementation
  - Duplicate username/email prevention
  - Secure password comparison (timing attack resistant)
  
- **User Experience Improvements**
  - Email-based login support
  - Logout functionality with confirmation
  - Enhanced form validation with clear error messages
  - Larger GUI windows for better accessibility (700x500+)
  - Complete signup forms in all entry points
  
- **Configuration Management**
  - Server host/port configuration via server.properties
  - No hard-coded IP addresses
  - Easy deployment to different environments

---

## Getting Started

### Prerequisites

- **Java**: JDK 17 or later
- **Maven**: 3.6 or later
- **API Keys**: Gemini API key for chatbot functionality (optional)

### Installation

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd ParTee
   ```

2. **Configure server connection** (optional, defaults to localhost:5050)
   
   Create or edit `server.properties` in the project root:
   ```properties
   server.host=localhost
   server.port=5050
   ```
   
   For remote servers, update the host value:
   ```properties
   server.host=192.168.1.100
   server.port=5050
   ```

3. **Configure AI chatbot** (optional)
   
   Create `.env` file in project root:
   ```
   GEMINI_API_KEY=your_api_key_here
   ```
   
   **Important**: Do not commit your API key to version control!

---

## Building and Testing

### Build the Project

```bash
mvn clean compile
```

### Run Tests

Run all tests:
```bash
mvn test
```

Run specific test class:
```bash
mvn test -Dtest=DatabaseTest
```

Run non-GUI tests only:
```bash
mvn test -Dtest=UserTest,UserManagerTest,DatabaseTest,ReservationsTest,TeeTimeTest,ServerTest,ServerWorkerTest
```

---

## Running the Application

### Starting the Server

In a terminal window:
```bash
mvn exec:java -Dexec.mainClass="com.project.golf.server.ServerMain"
```

Or if already compiled:
```bash
java -cp target/classes com.project.golf.server.ServerMain
```

The server will start listening on the configured port (default: 5050).

### Starting the Client

In a separate terminal window:
```bash
mvn exec:java -Dexec.mainClass="com.project.golf.client.ClientMain"
```

Or if already compiled:
```bash
java -cp target/classes com.project.golf.client.ClientMain
```

The client will connect to the server and display the login GUI.

### Multiple Clients

You can start additional client instances in new terminal windows to test multi-client functionality.

---

## Architecture

### System Design

```
Clients → Server → ServerWorker (per client) → Database (Singleton) → File Storage
```

### Core Components

#### Data Layer
- **Database**: Thread-safe singleton managing all data
- **User**: Account information with admin flags
- **Reservations**: Booking records with payment tracking
- **TeeTime**: Golf-specific time slots with capacity management
- **Event**: Generic event system with capacity limits
- **CourseSettings**: Operational configuration (hours, pricing, layout)

#### Network Layer
- **Server**: Multi-threaded connection acceptor
- **ServerWorker**: Per-client request handler (Runnable)
- **Client**: Server communication interface
- **Protocol**: Pipe-delimited command format

#### Presentation Layer
- **LoginGUI**: Authentication and password recovery
- **MainMenuGUI**: Primary navigation hub
- **MakeReservationGUI**: Tee time booking interface
- **ManageReservationsGUI**: View and cancel bookings
- **AccountOptionsGUI**: Profile management
- **AdminControlGUI**: Administrative functions
- **EventApprovalGUI**: Event request handling
- **ChatBotPanelGemini25Flash**: AI assistant integration

### Design Patterns

- **Singleton**: Database ensures single source of truth
- **Thread-per-Client**: ServerWorker handles each connection
- **MVC**: Separation of data, logic, and presentation
- **Interface-Based**: All public classes implement interfaces

### Thread Safety

- **Database**: ReentrantReadWriteLock allows concurrent reads
- **TeeTime**: Synchronized booking methods prevent conflicts
- **Event**: Synchronized capacity management
- **ServerWorker**: Independent per-client execution

---

## Data Persistence

### File Format

All data is stored in the project root directory in plain text format:

1. **users.txt**
   - Format: `username,hashedPassword,firstName,lastName,email,hasPaid,isAdmin`
   - Passwords are BCrypt-hashed with work factor 12

2. **reservations.txt**
   - Format: `reservationId,username,date,time,partySize,teeBox,price,isPaid`
   - Tracks all booking information and payment status

3. **teetimes.txt**
   - Format: `teeTimeId,date,time,teeBox,maxPartySize,pricePerPerson`
   - Available time slots for booking

4. **settings.txt**
   - Format: `courseName,openingTime,closingTime,defaultPrice,interval,maxParty,numBoxes,advanceDays,Mon,Tue,Wed,Thu,Fri,Sat,Sun`
   - Course operational configuration

5. **events.txt**
   - Format: `eventId,eventName`
   - Event definitions (simplified for current version)

### Persistence Strategy

- **Automatic Loading**: Database loads all data on first getInstance() call
- **Manual Saving**: Call Database.getInstance().saveToFile() to persist changes
- **Atomic Operations**: Each file write is complete or rolled back
- **Server Auto-Save**: Server periodically saves data to prevent loss

---

## API and Interfaces

### Database Operations

All operations are thread-safe and support concurrent access:

- **User Management**: addUser, removeUser, findUser, validateLogin, getAllUsers
- **Reservation Management**: addReservation, removeReservation, findReservation, getReservationsByUser, getReservationsByDate
- **TeeTime Management**: addTeeTime, removeTeeTime, findTeeTime, getTeeTimesByDate, bookTeeTime
- **Event Management**: addEvent, removeEvent, findEvent, getAllEvents
- **Settings Management**: getCourseSettings, setCourseSettings
- **Persistence**: saveToFile, loadFromFile, clearAllData

### Client-Server Protocol

Commands are pipe-delimited strings with the format:
```
COMMAND|param1|param2|...
```

Common commands:
- `LOGIN|username|password`
- `ADD_USER|username|password|firstName|lastName|email`
- `LIST_TEETIMES|date`
- `BOOK_TEETIME|teeTimeId|partySize|username`
- `GET_RESERVATIONS|username`
- `CANCEL_RESERVATION|reservationId`

---

## Testing

### Test Coverage

- **Total Test Classes**: 10+
- **Total Test Cases**: 150+
- **Coverage Areas**:
  - Unit tests for all core classes
  - Integration tests for database operations
  - Concurrency tests for thread safety
  - Protocol tests for client-server communication
  - GUI tests for user interfaces

### Testing Approach

- **Unit Tests**: Each class tested in isolation
- **Integration Tests**: Database interaction validation
- **Concurrency Tests**: Multi-threaded operation verification
- **Edge Cases**: Null handling, invalid input, boundary conditions
- **Persistence Tests**: Save/load cycle validation

### Test Isolation

- JUnit 5 framework with @BeforeEach and @AfterEach
- Database singleton reset between tests
- CountDownLatch for coordinated concurrent operations

---

## Project Structure

```
ParTee/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── project/
│   │               └── golf/
│   │                   ├── client/          # Client-side components
│   │                   ├── server/          # Server-side components
│   │                   ├── gui/             # User interface components
│   │                   ├── database/        # Data management
│   │                   ├── models/          # Data models
│   │                   ├── utils/           # Utility classes
│   │                   └── events/          # Event system
│   └── test/
│       └── java/
│           └── com/
│               └── project/
│                   └── golf/
│                       └── tests/           # All test classes
├── lib/                                      # External libraries
├── pom.xml                                   # Maven configuration
├── server.properties                         # Server configuration
├── .env.example                             # Environment template
└── README.md                                 # This file
```

---

## Configuration Files

### server.properties

Controls server network settings:
```properties
server.host=localhost
server.port=5050
```

Change these values to deploy to different environments without code changes.

### .env

Contains sensitive configuration (not committed to repository):
```
GEMINI_API_KEY=your_key_here
```

Use `.env.example` as a template.

---

## Security Features

### Password Management
- BCrypt hashing with work factor 12
- Constant-time password comparison
- No plaintext password storage

### Data Validation
- Duplicate username/email prevention
- Input sanitization for all user data
- SQL-injection resistant (using file-based storage)

### Session Management
- Server-side session tracking
- Secure logout with state cleanup
- Connection timeout handling

---

## Known Limitations

- File-based storage (no SQL database)
- Single-server deployment (no clustering)
- Event persistence simplified (full implementation pending)
- AI chatbot requires internet connection and API key

---

## Future Enhancements

- Database migration to SQL for better scalability
- Payment processing integration
- Advanced reporting and analytics
- Mobile application support
- Email notification system enhancement
- Event management full implementation
- Multi-server clustering support

---

## Submission History

- **Phase 1**: November 10, 2025 - Database backend
- **Phase 2**: November 21, 2025 - Client-server architecture  
- **Phase 3**: December 6, 2025 - GUI implementation

---

## License

Academic project for CS 180, Purdue University.

---

## Support

For issues or questions, contact the development team via the course communication channels.
