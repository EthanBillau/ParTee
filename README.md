# CS 180 Team Project - Golf Course Reservation System (ParTee)

## Team Members
- Ethan Billau (ebillau)
- Anoushka Chakravarty (chakr181)
- Nikhil Kodali (kodali3)
- Connor Landzettl (clandzet)
- Aman Wakankar (awakanka)

---

## Project Overview
This project implements a comprehensive golf course reservation system with secure user authentication, dynamic server configuration, and an intuitive GUI. The system supports user account management, tee time booking, reservation tracking, events, and course configuration management. All components are thread-safe and include persistent data storage with encrypted passwords.

## Recent Improvements (December 2025)

### Security Enhancements
- **Password Hashing**: All passwords are now hashed using BCrypt (work factor 12) before storage
- **Duplicate Prevention**: System prevents duplicate usernames and email addresses during registration
- **Secure Authentication**: Constant-time password comparison to prevent timing attacks

### Improved User Experience
- **Login with Email**: Users can now login with either username or email address
- **Logout Functionality**: Added logout button with confirmation dialog in main menu
- **Better Form Validation**: Enhanced input validation with clear error messages
- **Larger Windows**: Increased GUI window sizes for better accessibility (700x500+)
- **Full Signup Form**: NoAccountGUI now provides a complete account creation form

### Dynamic Configuration
- **Flexible Server IP**: Server host/port can be configured via `server.properties` file
- **No Hard-Coded IPs**: All server connections use dynamic configuration
- **Easy Deployment**: Change server location without modifying code

---

## Server Configuration

The system now supports dynamic server configuration through a properties file. This allows you to change the server host and port without modifying code.

### Configuring Server Connection

1. Create or edit the `server.properties` file in the project root:
```properties
# Server Configuration
server.host=localhost
server.port=5050
```

2. To connect to a remote server, change `localhost` to the server's IP address:
```properties
server.host=192.168.1.100
server.port=5050
```

3. The changes will take effect the next time you start the client

### Example Configurations

**Local Testing:**
```properties
server.host=localhost
server.port=5050
```

**Remote Server:**
```properties
server.host=192.168.1.50
server.port=5050
```

**Cloud Deployment:**
```properties
server.host=your-server.example.com
server.port=8080
```

---

## Compilation and Execution Instructions

### Prerequisites
- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher

### Compiling the Project
```bash
javac -cp ".:lib/junit-platform-console-standalone-6.0.1.jar" *.java
```

### Running the Tests
Execute all test cases using JUnit:
```bash
java -jar lib/junit-platform-console-standalone-6.0.1.jar --class-path . --scan-class-path
```

### Running Individual Test Classes
```bash
java -cp ".:lib/junit-platform-console-standalone-6.0.1.jar" org.junit.platform.console.ConsoleLauncher --select-class DatabaseTest
```

### Execution
To begin, compile all files, then in order to test the work of a given class, the test version of the class can be used. Simply running the testcases will check for the functionality of the class. In order to test functionality with inputs, the Database class should be used. As the hub connecting the various classes Database also handles persistence, utilizing file I/O to read and write memory components. Thus, utilizing the Database class would allow one to check that data from all classes is being properly handled and stored.

---

## Submission Details

### Vocareum Workspace Submission
- **Submitted by:** Ethan Billau
- **Date:** Nov 10, 2025

---

## Detailed Class Descriptions

### 1. Database.java
**Purpose:** Centralized, thread-safe database that manages all users, reservations, events, tee times, and course settings. Implements the Singleton pattern to ensure only one database instance exists throughout the application.

**Methods:**
- **Singleton Access:**
  - `getInstance()` - Returns the single database instance (thread-safe lazy initialization)
  - `resetInstance()` - Resets singleton for testing purposes only
  
- **User Management:**
  - `addUser(User)` - Adds new user to database
  - `removeUser(String username)` - Removes user by username
  - `findUser(String username)` - Retrieves user by username
  - `getAllUsers()` - Returns all users
  - `validateLogin(String username, String password)` - Authenticates user credentials
  
- **Reservation Management:**
  - `addReservation(Reservations)` - Adds new reservation
  - `removeReservation(String reservationId)` - Removes reservation by ID
  - `findReservation(String reservationId)` - Finds specific reservation
  - `getReservationsByUser(String username)` - Gets all reservations for a user
  - `getReservationsByDate(String date)` - Gets all reservations for a date
  - `getAllReservations()` - Returns all reservations
  
- **TeeTime Management:**
  - `addTeeTime(TeeTime)` - Adds new tee time slot
  - `removeTeeTime(String teeTimeId)` - Removes tee time
  - `findTeeTime(String teeTimeId)` - Finds specific tee time
  - `getTeeTimesByDate(String date)` - Gets all tee times for a date
  - `getAllTeeTimes()` - Returns all tee times
  
- **Event Management:**
  - `addEvent(Event)` - Adds new event
  - `removeEvent(String eventId)` - Removes event
  - `findEvent(String eventId)` - Finds specific event
  - `getAllEvents()` - Returns all events
  
- **CourseSettings Management:**
  - `getCourseSettings()` - Returns current course configuration
  - `setCourseSettings(CourseSettings)` - Updates course configuration
  
- **Persistence:**
  - `saveToFile()` - Saves all data to disk (5 files)
  - `loadFromFile()` - Loads all data from disk (automatic on first getInstance())
  - `clearAllData()` - Clears all data (primarily for testing)

**Testing:** DatabaseTest.java contains ~42 comprehensive test cases covering:
- Singleton pattern verification (ensures only one instance)
- All CRUD operations for users, reservations, events, and tee times
- CourseSettings get/set operations
- Thread safety with concurrent operations
- Data persistence (save and load cycles)
- Null and invalid input handling
- Edge cases and error conditions

**Relationship to Other Classes:**
- Central hub that stores and manages all data objects
- Accessed via `Database.getInstance()` by Server in Phase 2
- Stores: User, Reservations, TeeTime, Event, CourseSettings objects
- Provides thread-safe access for multiple simultaneous client connections
- Singleton ensures ALL components access the same data

**File Persistence:**
- `users.txt` - User account data
- `reservations.txt` - Reservation records
- `teetimes.txt` - Available tee time slots
- `settings.txt` - Course configuration

---

### 2. DatabaseInterface.java
**Purpose:** Defines the contract for all Database operations, ensuring consistent API (same functionality/format) across potential database implementations. **All public methods in Database.java must be declared in this interface as per project requirements.**

**Methods Required:**

**User Management Methods:**
- `boolean addUser(User user)` - Adds new user to database
- `boolean removeUser(String username)` - Removes user by username
- `User findUser(String username)` - Retrieves user by username
- `ArrayList<User> getAllUsers()` - Returns all users
- `boolean validateLogin(String username, String password)` - Authenticates user credentials

**Reservation Management Methods:**
- `boolean addReservation(Reservations reservation)` - Adds new reservation
- `boolean removeReservation(String reservationId)` - Removes reservation by ID
- `Reservations findReservation(String reservationId)` - Finds specific reservation
- `ArrayList<Reservations> getReservationsByUser(String username)` - Gets all reservations for a user
- `ArrayList<Reservations> getReservationsByDate(String date)` - Gets all reservations for a date
- `ArrayList<Reservations> getAllReservations()` - Returns all reservations

**TeeTime Management Methods:**
- `boolean addTeeTime(TeeTime teeTime)` - Adds new tee time slot
- `boolean removeTeeTime(String teeTimeId)` - Removes tee time
- `TeeTime findTeeTime(String teeTimeId)` - Finds specific tee time
- `ArrayList<TeeTime> getTeeTimesByDate(String date)` - Gets all tee times for a date
- `ArrayList<TeeTime> getAllTeeTimes()` - Returns all tee times

**Event Management Methods:**
- `boolean addEvent(Event event)` - Adds new event
- `boolean removeEvent(String eventId)` - Removes event
- `Event findEvent(String eventId)` - Finds specific event
- `ArrayList<Event> getAllEvents()` - Returns all events

**CourseSettings Management Methods:**
- `CourseSettings getCourseSettings()` - Returns current course configuration
- `void setCourseSettings(CourseSettings settings)` - Updates course configuration

**Persistence Methods:**
- `void saveToFile() throws IOException` - Saves all data to disk
- `void loadFromFile() throws IOException` - Loads all data from disk
- `void clearAllData()` - Clears all data (for testing)

**Relationship to Other Classes:**
- Implemented by Database.java
- Follows project requirement: "Every program class with non-private methods must have a dedicated interface"
- Enables future database implementations without changing dependent code
- Ensures all Database operations are properly exposed through the interface contract

---

### 3. DatabaseTest.java
**Purpose:** Comprehensive JUnit test suite for Database class functionality.

**Test Coverage (42+ tests):**
- **Singleton Tests:** Verify getInstance() returns same instance, data persists across calls
- **User Operations:** Add, remove, find, validate login, handle duplicates, null checks
- **Reservation Operations:** Add, remove, find, search by user/date, null handling
- **TeeTime Operations:** Add, remove, find, search by date, date filtering
- **Event Operations:** Add, remove, find, null handling
- **CourseSettings Operations:** Get/set settings, null validation
- **Persistence Tests:** Save/load cycle verification, file creation, data integrity
- **Concurrency Tests:** Thread safety with multiple simultaneous operations
- **Edge Cases:** Empty database, duplicate IDs, invalid inputs

**Relationship to Other Classes:**
- Tests Database.java implementation
- Uses Database.resetInstance() for test isolation
- Creates User, Reservations, TeeTime, Event, CourseSettings objects for testing

---

### 4. User.java
**Purpose:** Represents a user account in the system with authentication credentials and profile information.

**Fields:**
- `username` - Unique username (String)
- `password` - Password for authentication (String)
- `firstName` - User's first name (String)
- `lastName` - User's last name (String)
- `email` - Email address (String)
- `hasPaid` - Payment status (boolean)
- `isAdmin` - Admin privileges flag (boolean) - distinguishes between customers and course managers

**Constructors:**
- `User(String username, String password, String firstName, String lastName, String email, boolean hasPaid)` - Creates user with default isAdmin = false
- `User(String username, String password, String firstName, String lastName, String email, boolean hasPaid, boolean isAdmin)` - Creates user with all fields including admin status

**Methods:**
- **Getters:** getUsername(), getPassword(), getFirstName(), getLastName(), getEmail(), hasPaid(), isAdmin()
- **Setters:** setUsername(), setPassword(), setFirstName(), setLastName(), setEmail(), setHasPaid(), setIsAdmin()
- **Persistence:**
  - `toFileString()` - Converts user to CSV format for storage
  - `fromFileString(String)` - Static method to reconstruct User from CSV (used during database load)
- **Utility:**
  - `toString()` - Human-readable string representation

**Testing:** UserTest.java validates:
- Constructor creates user with all fields
- All getters return correct values
- All setters update values correctly
- File persistence (toFileString/fromFileString roundtrip)
- isAdmin flag correctly distinguishes user types

**Relationship to Other Classes:**
- Implements UserInterface
- Stored in Database via addUser/removeUser
- Used by Database.validateLogin() for authentication
- Referenced in Reservations.username field
- Admin users will have elevated privileges in Phase 2 server

---

### 5. UserInterface.java
**Purpose:** Defines the contract for User objects. **All public methods in User.java must be declared in this interface as per project requirements.**

**Methods Required:**

**Getter Methods:**
- `String getUsername()` - Returns the username
- `String getPassword()` - Returns the password
- `String getFirstName()` - Returns the first name
- `String getLastName()` - Returns the last name
- `String getEmail()` - Returns the email address
- `boolean hasPaid()` - Returns payment status
- `boolean isAdmin()` - Returns admin status

**Setter Methods:**
- `void setUsername(String username)` - Sets the username
- `void setPassword(String password)` - Sets the password
- `void setFirstName(String firstName)` - Sets the first name
- `void setLastName(String lastName)` - Sets the last name
- `void setEmail(String email)` - Sets the email
- `void setHasPaid(boolean hasPaid)` - Sets payment status
- `void setIsAdmin(boolean isAdmin)` - Sets admin status

**Persistence Methods:**
- `String toFileString()` - Converts user to CSV format for file storage

**Relationship to Other Classes:**
- Implemented by User.java
- Ensures consistent User API across the application
- Follows project requirement for interfaces on all classes with non-private methods

---

### 6. UserTest.java
**Purpose:** JUnit test suite for User class.

**Test Coverage:**
- Constructor initialization (both constructors)
- All getter methods
- All setter methods
- File string persistence (roundtrip conversion)
- Admin flag functionality
- toString() method output

**Relationship to Other Classes:**
- Tests User.java implementation
- Verifies User objects work correctly before integration with Database

---

### 7. UserManager.java
**Purpose:** Provides high-level user account management operations (wrapper around Database user operations).

**Methods:**
- `createUser(username, password, firstName, lastName, email)` - Creates new user account
- `deleteUser(username)` - Deletes user account
- `authenticateUser(username, password)` - Validates login credentials
- `getAllUsers()` - Retrieves all users
- `saveUsersToFile()` - Triggers database save operation
- `loadUsersFromFile()` - Triggers database load operation

**Testing:** UserManagerTest.java verifies:
- User creation
- User deletion
- Authentication (valid and invalid credentials)
- User retrieval

**Relationship to Other Classes:**
- Implements UserManagerInterface
- Uses Database.getInstance() to perform operations
- Wrapper that simplifies user management operations
- Can be used by Server in Phase 2 for cleaner code organization

---

### 8. UserManagerInterface.java
**Purpose:** Interface for UserManager operations. **All public methods in UserManager.java must be declared in this interface.**

**Methods Required:**
- `boolean createUser(String username, String password, String firstName, String lastName, String email)` - Creates new user
- `boolean deleteUser(String username)` - Deletes user
- `boolean authenticateUser(String username, String password)` - Validates credentials
- `ArrayList<User> getAllUsers()` - Retrieves all users
- `void saveUsersToFile()` - Saves user data
- `void loadUsersFromFile()` - Loads user data

**Relationship to Other Classes:**
- Implemented by UserManager.java
- Follows project requirement for interfaces

---

### 9. UserManagerTest.java
**Purpose:** Test suite for UserManager.

**Test Coverage:**
- User creation and deletion
- Authentication (valid/invalid credentials)
- Retrieval of all users
- File persistence operations

**Relationship to Other Classes:**
- Tests UserManager.java
- Indirectly tests Database user operations

---

### 10. Reservations.java
**Purpose:** Represents a booking/reservation made by a user for a tee time.

**Fields:**
- `reservationId` - Unique identifier (String)
- `username` - User who made reservation (String)
- `date` - Reservation date in YYYY-MM-DD format (String)
- `time` - Reservation time in HH:MM format (String)
- `partySize` - Number of golfers (int)
- `teeBox` - Starting hole/location (String)
- `price` - Total price for party (double)
- `isPaid` - Payment status (boolean)

**Constructor:**
- `Reservations(String reservationId, String username, String date, String time, int partySize, String teeBox, double price)` - Creates reservation with isPaid defaulting to false
- Overloaded constructor may include isPaid parameter

**Methods:**
- **Getters:** getReservationId(), getUsername(), getDate(), getTime(), getPartySize(), getTeeBox(), getPrice(), isPaid()
- **Setters:** setPrice(double), setIsPaid(boolean)
- **Persistence:**
  - `toFileString()` - CSV format for storage
  - `fromFileString(String)` - Static method to reconstruct from CSV
- **Utility:**
  - `toString()` - Human-readable format

**Testing:** ReservationsTest.java validates:
- Constructor with all parameters
- All getter methods
- Setter methods (price, isPaid)
- File persistence (roundtrip)
- Multiple reservations with different data
- Edge cases (null values, negative prices)

**Relationship to Other Classes:**
- Implements ReservationsInterface
- Created by TeeTime.bookTeeTime() when booking occurs
- Stored in Database via addReservation()
- Retrieved by Database methods (findReservation, getReservationsByUser, etc.)
- Links User (via username) to TeeTime (via date/time/teeBox)
- Tracks payment status for Phase 3 payment processing

---

### 11. ReservationsInterface.java
**Purpose:** Defines contract for Reservations objects. **All public methods in Reservations.java must be declared in this interface.**

**Methods Required:**

**Getter Methods:**
- `String getReservationId()` - Returns reservation ID
- `String getUsername()` - Returns username
- `String getDate()` - Returns date
- `String getTime()` - Returns time
- `int getPartySize()` - Returns party size
- `String getTeeBox()` - Returns tee box
- `double getPrice()` - Returns price
- `boolean isPaid()` - Returns payment status

**Setter Methods:**
- `void setPrice(double price)` - Sets price
- `void setIsPaid(boolean isPaid)` - Sets payment status

**Persistence Methods:**
- `String toFileString()` - Converts to CSV format

**Relationship to Other Classes:**
- Implemented by Reservations.java
- Follows project requirement for interfaces

---

### 12. ReservationsTest.java
**Purpose:** Comprehensive test suite for Reservations class.

**Test Coverage (12+ tests):**
- Constructor validation
- All getter methods
- Setter functionality (price, isPaid)
- File persistence (toFileString/fromFileString roundtrip)
- Edge cases (null handling, negative values)
- toString() method

**Relationship to Other Classes:**
- Tests Reservations.java

---

### 13. TeeTime.java
**Purpose:** Represents a specific tee time slot on the golf course. This is the core domain model for golf bookings - each TeeTime represents a specific date/time when golfers can start their round.

**Key Features:**
- Golf-specific domain model (unlike generic Event class)
- Thread-safe booking operations with synchronized methods
- Automatic unique ID generation
- Capacity management and availability tracking
- Integrated reservation creation

**Fields:**
- Static counter for ID generation
- `teeTimeId` - Unique identifier (format: TT1, TT2, etc.)
- `date` - Date of tee time (YYYY-MM-DD format)
- `time` - Start time (HH:MM format, 24-hour)
- `teeBox` - Starting hole (e.g., "Hole 1", "Back Nine")
- `maxPartySize` - Maximum golfers allowed (typically 4)
- `pricePerPerson` - Cost per golfer
- `reservations` - ArrayList of all reservations for this slot

**Constructors:**
- `TeeTime(String date, String time, String teeBox, int maxPartySize, double pricePerPerson)` - Auto-generates ID
- `TeeTime(String teeTimeId, String date, String time, String teeBox, int maxPartySize, double pricePerPerson)` - For loading from file with existing ID

**Methods:**
- **Property Getters:**
  - `getTeeTimeId()`, `getDate()`, `getTime()`, `getTeeBox()`
  - `getMaxPartySize()`, `getPricePerPerson()`
  
- **Availability (synchronized):**
  - `getReservedSpots()` - Total golfers currently booked
  - `getAvailableSpots()` - Remaining capacity
  - `isAvailable(int partySize)` - Check if space available
  - `isFullyBooked()` - Check if no spots left
  
- **Booking (synchronized):**
  - `bookTeeTime(int partySize, String username)` - Creates reservation if space available, returns Reservations object or null
  - `cancelReservation(String reservationId)` - Frees up spots
  
- **Reservation Access (synchronized):**
  - `getReservations()` - All reservations for this tee time
  - `getReservation(String reservationId)` - Specific reservation by ID
  
- **Configuration (synchronized):**
  - `setPricePerPerson(double)` - Update pricing
  
- **Persistence:**
  - `toFileString()` - CSV format
  - `fromFileString(String)` - Static reconstruction method
  
- **Utility:**
  - `toString()` - Human-readable format

**Testing:** TeeTimeTest.java contains 52+ comprehensive tests:
- Constructor and ID generation
- All getter methods
- Availability calculations (reserved, available, fully booked)
- Booking operations (single, multiple, overbooking prevention)
- Cancellation functionality
- Invalid input handling (zero/negative party size, null username)
- Thread safety (concurrent booking tests)
- Price calculations
- File persistence (roundtrip)

**Relationship to Other Classes:**
- Implements TeeTimeInterface
- Stored in Database via addTeeTime/removeTeeTime
- Creates Reservations objects when bookings occur
- More golf-specific than generic Event class
- Used by Server in Phase 2 to manage available slots
- Retrieved by Database.getTeeTimesByDate() for client display

**Design Rationale:**
- **Why TeeTime vs Event?**
  - Event is generic (could be any type of event)
  - TeeTime is domain-specific to golf courses
  - TeeTime has golf concepts (tee box, party sizes)
  - Makes code more readable and maintainable
  - Better matches business domain

---

### 14. TeeTimeInterface.java
**Purpose:** Defines the contract for TeeTime operations. **All public methods in TeeTime.java must be declared in this interface.**

**Methods Required:**

**Property Getters:**
- `String getTeeTimeId()` - Returns tee time ID
- `String getDate()` - Returns date
- `String getTime()` - Returns time
- `String getTeeBox()` - Returns tee box
- `int getMaxPartySize()` - Returns maximum party size
- `double getPricePerPerson()` - Returns price per person

**Availability Methods:**
- `int getReservedSpots()` - Returns number of spots already booked
- `int getAvailableSpots()` - Returns remaining capacity
- `boolean isAvailable(int partySize)` - Checks if party can be accommodated
- `boolean isFullyBooked()` - Checks if completely booked

**Booking and Cancellation:**
- `Reservations bookTeeTime(int partySize, String username)` - Creates and returns reservation
- `boolean cancelReservation(String reservationId)` - Cancels reservation

**Reservation Management:**
- `ArrayList<Reservations> getReservations()` - Returns all reservations
- `Reservations getReservation(String reservationId)` - Returns specific reservation

**Configuration:**
- `void setPricePerPerson(double price)` - Updates pricing

**Persistence:**
- `String toFileString()` - Converts to CSV format

**Relationship to Other Classes:**
- Implemented by TeeTime.java
- Follows project requirement for interfaces on all classes with non-private methods

---

### 15. TeeTimeTest.java
**Purpose:** Extensive test suite for TeeTime class functionality.

**Test Coverage (52+ tests organized by category):**
- **Constructor Tests:** Initialization, ID generation
- **Availability Tests:** Reserved/available spots calculation, fully booked detection
- **Booking Tests:** Single/multiple bookings, capacity enforcement, overbooking prevention
- **Cancellation Tests:** Spot release, rebook after cancel
- **Validation Tests:** Invalid party sizes, null usernames
- **Thread Safety Tests:** Concurrent booking scenarios
- **Pricing Tests:** Price updates, calculation verification
- **Persistence Tests:** File string roundtrip, invalid data handling

**Relationship to Other Classes:**
- Tests TeeTime.java implementation
- Creates Reservations objects during booking tests
- Verifies integration with reservation system

---

### 16. CourseSettings.java
**Purpose:** Stores operational configuration and settings for the golf course. This class provides the data needed for server-side management features like setting hours of operation and course layout.

**Key Features:**
- Stores all course operational parameters
- Used by Server in Phase 2 to manage course operations
- Persists configuration across restarts
- Validation for all settings (times, prices, etc.)

**Fields:**
- `courseName` - Name of golf course
- `openingTime` - Daily opening time (HH:MM format)
- `closingTime` - Daily closing time (HH:MM format)
- `defaultPricePerPerson` - Base price per golfer
- `teeTimeInterval` - Minutes between tee times (e.g., 15)
- `maxPartySize` - Maximum golfers per group (typically 4)
- `numberOfTeeBoxes` - Number of holes/starting positions (9, 18, etc.)
- `advanceBookingDays` - How far ahead bookings allowed (7 standard, 90 for teams of 5)
- `daysOfOperation` - Map of which days course is open (Monday-Sunday)

**Constructors:**
- `CourseSettings()` - Default constructor with standard values
- `CourseSettings(String courseName, String openingTime, String closingTime, ...)` - Parameterized constructor

**Methods:**
- **Course Info:**
  - `getCourseName()`, `setCourseName(String)`
  
- **Operating Hours:**
  - `getOpeningTime()`, `setOpeningTime(String)`
  - `getClosingTime()`, `setClosingTime(String)`
  - `isWithinOperatingHours(String time)` - Check if time is valid
  
- **Days of Operation:**
  - `isOpenOnDay(String dayOfWeek)` - Check if open on specific day
  - `setDayOperation(String dayOfWeek, boolean isOpen)` - Set open/closed
  
- **Pricing:**
  - `getDefaultPricePerPerson()`, `setDefaultPricePerPerson(double)`
  
- **Course Configuration:**
  - `getTeeTimeInterval()`, `setTeeTimeInterval(int)`
  - `getMaxPartySize()`, `setMaxPartySize(int)`
  - `getNumberOfTeeBoxes()`, `setNumberOfTeeBoxes(int)`
  
- **Booking Rules:**
  - `getAdvanceBookingDays()`, `setAdvanceBookingDays(int)`
  
- **Persistence:**
  - `toFileString()` - CSV with all settings
  - `fromFileString(String)` - Static reconstruction
  
- **Utility:**
  - `toString()` - Human-readable format

**Testing:** CourseSettingsTest.java validates (40+ tests):
- Default constructor initialization
- Parameterized constructor
- All getter/setter pairs
- Time format validation (HH:MM)
- Operating hours validation
- Price validation (non-negative)
- Days of operation management
- File persistence (roundtrip with all data)
- Invalid input handling (null, empty, malformed)
- Realistic configuration scenarios

**Relationship to Other Classes:**
- Implements CourseSettingsInterface
- Stored in Database (singleton)
- Retrieved via Database.getCourseSettings()
- Updated via Database.setCourseSettings()
- Used by Server in Phase 2 to:
  - Generate tee times based on hours and intervals
  - Validate booking requests (check operating hours)
  - Enforce advance booking limits
  - Set default prices for new tee times
  - Determine course layout (number of tee boxes)

**Design Rationale:**
- **Why CourseSettings is essential:**
  - Requirements say "Set hours of operation" → needs persistent storage
  - Requirements say "Set seating arrangement" → for golf = tee box configuration
  - Server needs configuration data to operate
  - Admin can change settings via GUI in Phase 3
  - Configuration-driven vs hard-coded = flexible and maintainable

---

### 17. CourseSettingsInterface.java
**Purpose:** Defines contract for CourseSettings operations. **All public methods in CourseSettings.java must be declared in this interface.**

**Methods Required:**

**Course Information:**
- `String getCourseName()` - Returns course name
- `void setCourseName(String courseName)` - Sets course name

**Operating Hours:**
- `String getOpeningTime()` - Returns opening time
- `void setOpeningTime(String openingTime)` - Sets opening time
- `String getClosingTime()` - Returns closing time
- `void setClosingTime(String closingTime)` - Sets closing time
- `boolean isWithinOperatingHours(String time)` - Validates time

**Days of Operation:**
- `boolean isOpenOnDay(String dayOfWeek)` - Checks if open on day
- `void setDayOperation(String dayOfWeek, boolean isOpen)` - Sets day status

**Pricing:**
- `double getDefaultPricePerPerson()` - Returns default price
- `void setDefaultPricePerPerson(double price)` - Sets default price

**Course Configuration:**
- `int getTeeTimeInterval()` - Returns interval in minutes
- `void setTeeTimeInterval(int interval)` - Sets interval
- `int getMaxPartySize()` - Returns max party size
- `void setMaxPartySize(int maxSize)` - Sets max party size
- `int getNumberOfTeeBoxes()` - Returns number of tee boxes
- `void setNumberOfTeeBoxes(int numBoxes)` - Sets number of tee boxes

**Booking Rules:**
- `int getAdvanceBookingDays()` - Returns advance booking limit
- `void setAdvanceBookingDays(int days)` - Sets advance booking limit

**Persistence:**
- `String toFileString()` - Converts to CSV format

**Relationship to Other Classes:**
- Implemented by CourseSettings.java
- Follows project requirement for interfaces

---

### 18. CourseSettingsTest.java
**Purpose:** Comprehensive test suite for CourseSettings.

**Test Coverage (40+ tests):**
- Constructor tests (default and parameterized)
- All getter/setter validation
- Time format validation
- Operating hours checking
- Days of operation management
- Price validation
- File persistence verification
- Integration scenarios
- toString() method

**Relationship to Other Classes:**
- Tests CourseSettings.java
- Verifies configuration management works correctly

---

### 19. EventInterface.java
**Purpose:** Interface for capacity-based events that manage reservations. Generic event interface that can represent any type of scheduled event with capacity limits.

**Key Features:**
- Defines event properties (ID, name, start time, duration, capacity)
- Defines booking and cancellation operations
- Specifies pricing methods
- Requires persistence capability

**Methods Required:**

**Property Getters:**
- `String getId()` - Returns event ID
- `String getName()` - Returns event name
- `String getStart()` - Returns start time
- `int getDurationMinutes()` - Returns duration
- `int getTotalCapacity()` - Returns total capacity
- `int getAvailableCapacity()` - Returns available spots

**Pricing:**
- `double getPricePerPerson()` - Returns base price
- `double getPriceForParty(int partySize)` - Calculates party price

**Booking:**
- `Reservations bookReservation(int partySize, String username)` - Creates reservation
- `boolean cancelReservation(String reservationId)` - Cancels reservation
- `Reservations getReservation(String reservationId)` - Retrieves specific reservation

**Availability:**
- `boolean isAvailableForParty(int size)` - Checks capacity

**Data Access:**
- `ArrayList<Reservations> getAllReservations()` - Gets all reservations

**Persistence:**
- `void saveToFile()` - Saves event state

**Relationship to Other Classes:**
- Implemented by Event.java
- More generic alternative to TeeTime
- Creates and manages Reservations objects

**Design Note:**
- Event is generic and could represent any capacity-limited event
- TeeTime is preferred for actual golf bookings (domain-specific)
- Event can be used for special tournaments or non-golf events

---

### 20. Event.java
**Purpose:** Implementation of EventInterface for basic capacity-based events.

**Key Features:**
- Capacity-based system (tracks total spots, not individual seats)
- Thread-safe booking with synchronized methods
- Auto-generates unique IDs for events and reservations
- Integrates with Reservations class
- File persistence

**Fields:**
- Static counters for ID generation
- Event properties (id, name, start time, duration, capacity)
- Base price per person
- List of reservations

**Constructor:**
- `Event(String name, String start, int durationMinutes, int totalCapacity, double pricePerPerson)` - Creates new event with auto-generated ID
- Overloaded constructor with ID parameter for loading from file

**Methods:**
- All methods required by EventInterface
- `setPricePerPerson(double)` - Updates pricing
- Thread-safe synchronized methods for booking/cancellation
- `toString()` - Human-readable format

**Testing:** EventTest.java validates:
- Event creation and property getters
- Capacity calculations (total, available)
- Booking operations (success, failure when full)
- Cancellation and spot release
- Thread safety with concurrent operations
- Price calculations
- File persistence

**Relationship to Other Classes:**
- Implements EventInterface
- Creates Reservations objects when bookings occur
- Stored in Database
- Generic alternative to TeeTime for non-golf events

---

### 21. EventTest.java
**Purpose:** Test suite for Event class.

**Test Coverage:**
- Constructor and initialization
- Property getters
- Booking operations (success/failure)
- Capacity management
- Cancellations
- Thread safety
- Pricing calculations
- toString() method

**Relationship to Other Classes:**
- Tests Event.java

---

## System Architecture

### Data Flow Overview
```
Phase 1 (Previous):
User/Reservations/TeeTime/Events/CourseSettings
          ↓
   Database (Singleton)
          ↓
   File Persistence (5 .txt files)

Phase 2 (Current):
Clients → Server → Database.getInstance() → Data Classes
                      ↓
                 File Storage
```

### Thread Safety Design
- **Database:** ReentrantReadWriteLock allows multiple concurrent reads, exclusive writes
- **TeeTime:** Synchronized methods prevent booking conflicts
- **Event:** Synchronized booking/cancellation

### Singleton Pattern
- **Database** uses Singleton to ensure single source of truth
- Thread-safe lazy initialization via synchronized `getInstance()`
- All components access same database instance
- Critical for Phase 2 when multiple client connections access data simultaneously

### Domain Model Hierarchy
```
Reservations ← Created by TeeTime.bookTeeTime() or Event.bookReservation()
     ↓
TeeTime (golf-specific) OR Event (generic)
     ↓
Stored in Database
     ↓
Accessed by Server (Phase 2)
```

### Interface Implementation Summary
All classes properly implement their corresponding interfaces as required:
- Database implements DatabaseInterface (all public methods included)
- User implements UserInterface (all getters, setters, and toFileString)
- UserManager implements UserManagerInterface
- Reservations implements ReservationsInterface
- TeeTime implements TeeTimeInterface (all booking and availability methods)
- CourseSettings implements CourseSettingsInterface (all configuration methods)
- Event implements EventInterface

---

## Design Decisions and Rationale

### Why Singleton for Database?
- **Problem:** Multiple database instances would cause data inconsistency
- **Solution:** Singleton pattern ensures single database throughout application
- **Benefit:** All components access same data, critical for multi-client server

### Why TeeTime in Addition to Event?
- **Event:** Generic, could represent anything
- **TeeTime:** Domain-specific for golf, clearer business meaning
- **Benefit:** More maintainable, easier to understand, better matches golf domain
- **Usage:** TeeTime for regular golf bookings, Event for tournaments

### Why CourseSettings?
- **Requirements:** "Set hours of operation", "Set seating arrangement"
- **Problem:** Server needs configuration data that must persist
- **Solution:** CourseSettings stores all operational parameters
- **Benefit:** Configuration-driven (not hard-coded), admin can change via GUI

### Why ReentrantReadWriteLock vs Simple Synchronized?
- **Better Performance:** Multiple threads can read simultaneously
- **Exclusive Writes:** Writes still prevent all other access
- **Realistic:** Matches real-world usage (many reads, fewer writes)

### Why Comprehensive Interfaces?
- **Requirement:** Project mandates "Every program class with non-private methods must have a dedicated interface"
- **Benefit:** Enables future implementations without changing dependent code
- **Benefit:** Forces clear API design and documentation
- **Benefit:** Supports dependency injection and testing

---

## File Structure and Persistence

### Data Files (Created in working directory):
1. **users.txt** - Format: `username,password,firstName,lastName,email,hasPaid,isAdmin`
2. **reservations.txt** - Format: `reservationId,username,date,time,partySize,teeBox,price,isPaid`
3. **teetimes.txt** - Format: `teeTimeId,date,time,teeBox,maxPartySize,pricePerPerson`
4. **settings.txt** - Format: `courseName,openingTime,closingTime,defaultPrice,interval,maxParty,numBoxes,advanceDays,Mon,Tue,Wed,Thu,Fri,Sat,Sun`
5. **events.txt** - Simplified format for Phase 1: `eventId,eventName` (will be enhanced in Phase 2)

### Persistence Strategy:
- **Automatic Load:** Database loads all data on first `getInstance()` call
- **Manual Save:** Call `Database.getInstance().saveToFile()` to persist changes
- **Atomic Operations:** Each file write is atomic (complete or rollback)
- **Phase 2 Enhancement:** Server will auto-save periodically
- **Note:** Event persistence is simplified in Phase 1 (only ID and name). Full event state persistence will be implemented in Phase 2 when events are more fully utilized.

---

## Testing Strategy

### Test Coverage Summary:
- **Total Test Classes:** 8
- **Total Test Cases:** 150+
- **Total Test Code Lines:** 1,800+
- **Coverage:** All classes with methods tested comprehensively

### Testing Approach:
1. **Unit Tests:** Each class tested in isolation
2. **Integration Tests:** Database interaction tests
3. **Concurrency Tests:** Thread safety verification
4. **Persistence Tests:** Save/load cycle validation
5. **Edge Cases:** Null handling, invalid input, boundary conditions

### Test Categories:
- **Functional Tests:** Verify correct behavior
- **Error Tests:** Verify proper error handling
- **Boundary Tests:** Test limits (capacity, party size, etc.)
- **Concurrency Tests:** Verify thread safety
- **Persistence Tests:** Verify data survives save/load

### Test Execution:
- All tests use JUnit 5 framework
- Tests use @BeforeEach and @AfterEach for proper isolation
- Database tests reset singleton between tests to ensure independence
- Thread safety tests use CountDownLatch for coordinated concurrent operations

---

## Project Files Summary

### Core Classes (7):
1. Database.java
2. User.java --------------------------(com.project.golf.users) package
3. UserManager.java ------------------ (com.project.golf.users) package
4. Reservations.java
5. TeeTime.java
6. CourseSettings.java
7. Event.java -------------------------(com.project.golf. events) package

### Interfaces (7):
1. DatabaseInterface.java
2. UserInterface.java ------------------- (com.project.golf.users) package
3. UserManagerInterface.java --------- (com.project.golf.users) package
4. ReservationsInterface.java
5. TeeTimeInterface.java
6. CourseSettingsInterface.java
7. EventInterface.java ----------------(com.project.golf. events) package

### Test Classes (7): 

#### Contained in the com.project.golf.tests package
1. DatabaseTest.java
2. UserTest.java
3. UserManagerTest.java
4. ReservationsTest.java
5. TeeTimeTest.java
6. CourseSettingsTest.java
7. EventTest.java

### Documentation:
1. README.md (this file)

**Total Files:** 22 Java files + 1 README = 23 files

---

# CS 180 Team Project - Phase 2: Golf Course Reservation System

## Team Members
- Ethan Billau (ebillau)
- Anoushka Chakravarty (chakr181)
- Nikhil Kodali (kodali3)
- Connor Landzettl (clandzet)
- Aman Wakankar (awakanka)

---

## Project Overview
This project implements a comprehensive database backend for a golf course reservation system (Option 1). The system supports user account management, tee time booking, reservation tracking, events, and course configuration management. All components are thread-safe and include persistent data storage.

---

## Phase Overview
This phase implements a working server-client methodology for the project.

---

# Compilation and Execution Instructions

## Prerequisites
- **Java:** JDK 17 (or later) installed and on your `PATH`  
- **OS note:**  
  - **macOS / Linux:** use `:` in classpaths  
  - **Windows (PowerShell / CMD):** use `;` in classpaths  
- **Project structure (already provided):**
  - `com/` – all source files
  - `lib/junit-platform-console-standalone-6.0.1.jar` – JUnit test runner
  - `out/` – directory for compiled `.class` files (can be empty initially)

From now on, assume your terminal is **inside the project root** (the folder that contains `com`, `lib`, and `out`).

---

## Compiling the Project

### macOS / Linux
Compile all `.java` files into the `out` directory and include the JUnit jar on the classpath:

```bash
# From project root
javac -d out \
  -cp "lib/junit-platform-console-standalone-6.0.1.jar" \
  $(find com -name "*.java")
```

If compilation succeeds, the compiled classes will be created under `out/com/...`.

### Windows (PowerShell)
Collect all `.java` file paths and compile into `out`:

```powershell
# From project root (PowerShell)
$files = Get-ChildItem -Path .\com -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out -cp "lib\junit-platform-console-standalone-6.0.1.jar" $files
```

If compilation succeeds, the compiled classes will be created under `out\com\...`.

---

## Running the Tests
We use the JUnit Platform Console Standalone jar in `lib/`.

### macOS / Linux
```bash
# From project root
java -jar lib/junit-platform-console-standalone-6.0.1.jar \
  -cp "out" \
  --scan-class-path
```

### Windows (PowerShell)
```powershell
# From project root (PowerShell)
java -jar "lib\junit-platform-console-standalone-6.0.1.jar" `
  -cp "out" `
  --scan-class-path
```

What this does:
- Discovers all test classes under `out` (e.g. `com.project.golf.tests.*`)
- Runs tests and prints a summary of passed/failed tests in the terminal

### Running Individual Test Classes
To run a single test class, specify its fully-qualified class name with `--select-class`.

Example: run `com.project.golf.tests.DatabaseTest`

macOS / Linux:
```bash
java -jar lib/junit-platform-console-standalone-6.0.1.jar \
  -cp "out" \
  --select-class "com.project.golf.tests.DatabaseTest"
```

Windows (PowerShell):
```powershell
java -jar "lib\junit-platform-console-standalone-6.0.1.jar" `
  -cp "out" `
  --select-class "com.project.golf.tests.DatabaseTest"
```

Replace `DatabaseTest` with any other test class under `com.project.golf.tests`.

---

## Execution (Client–Server)

- **Server main class:** `com.project.golf.server.ServerMain`  
- **Client main class:** `com.project.golf.client.ClientMain`

The typical workflow is:
1. Start the server in one terminal.
2. Start the client in another terminal.
3. Interact with the client and observe server logs showing communication.

### 1. Start the Server (Terminal 1)

macOS / Linux:
```bash
# From project root
java -cp "out:lib/junit-platform-console-standalone-6.0.1.jar" \
  com.project.golf.server.ServerMain
```

Windows (PowerShell):
```powershell
# From project root
java -cp "out;lib\junit-platform-console-standalone-6.0.1.jar" `
  com.project.golf.server.ServerMain
```

- The server should start listening for client connections.
- Expect a startup message indicating port/status. Keep this terminal open.

### 2. Start the Client (Terminal 2)

macOS / Linux:
```bash
# From project root
java -cp "out:lib/junit-platform-console-standalone-6.0.1.jar" \
  com.project.golf.client.ClientMain
```

Windows (PowerShell):
```powershell
# From project root
java -cp "out;lib\junit-platform-console-standalone-6.0.1.jar" `
  com.project.golf.client.ClientMain
```

- The client will connect to the server and present an interface (e.g., login, menu options).

---

## Demonstrating Client–Server Communication (Two-Terminal Setup)

Follow these steps for a clear demo or grading:

1. Open two terminals:
   - Terminal 1: server
   - Terminal 2: client
2. In both terminals, `cd` to the project root.
3. Start the server in Terminal 1 (see "Start the Server" above).
   - Point out any message such as “Server listening on port …”.
4. Start the client in Terminal 2 (see "Start the Client" above).
5. In Terminal 2 (client), perform an action (examples):
   - Log in
   - Create a reservation
   - List tee times
6. Immediately point to Terminal 1 (server) and show the corresponding logs, for example:
   - Received LOGIN request for user ...
   - Fetching user from database...
7. This proves:
   - The client sends requests.
   - The server receives and processes them.
   - Responses are returned to the client and shown in Terminal 2.

### Optional – Multiple Clients
- Open an additional terminal (Terminal 3).
- Start another client using the same client command.
- Show that multiple clients can connect and that each action is logged on the server terminal.

### Shutdown
- Exit the client(s) using the provided menu/commands (e.g., “Quit” option).
- Stop the server by closing Terminal 1 or using any clean shutdown command provided by `ServerMain`.

---

## Submission Details
### Vocareum Workspace Submission
- **Submitted by:** Ethan Billau
- **Date:** Nov 21, 2025

---

## Detailed Class Descriptions (Phase 2)

### 23. Client.java (com.project.golf.client)
**Purpose:** Manages network connection to the server, formats protocol commands for user actions, handles server responses, and provides a high-level API for user operations (login, booking, listing, etc.).

**Key Features:**
- Establishes and manages persistent socket connection to server
- High-level methods for all user functions (login, addUser, listTeeTimes, book, etc.)
- Handles errors and connection loss gracefully

**Fields:**
- `Socket serverSocket`
- IO streams for network communication
- Connection and user session status fields

**Constructors:**
- `Client()` or equivalent setup/connection constructor

**Methods:**
- `connect(String host, int port)` – Open server connection
- `disconnect()` – Clean up and close
- `sendCommand(String cmd)` – Protocol send/receive
- High-level wrappers: `login(...)`, `addUser(...)`, `listTeeTimes(...)`, etc.

**Relationship to Other Classes:**
- Implements ClientInterface
- Used by ClientMain for user interaction

---

### 24. ClientInterface.java (com.project.golf.client)
**Purpose:** Defines the contract for all Client operations. It enforces a clear separation between low-level network handling (connecting/sending) and high-level application commands (booking/login). This interface ensures that the Client is consistent for the UI.
**All public methods in Client.java must be declared in this interface as per project requirements.**

**Methods Required:**

**Network IO Methods**
- `void connect(String host, int port)` - Establishes a network connection to the server at the specified host and port
- `void disconnect()` - Closes the connection to the server if it is open
- `String sendCommand(String command)` - Sends a raw command string to the server and returns the server's response
- `boolean login(String username, String password)` - Sends login credentials to the server and returns true if authentication succeeds

**TeeTime Management Methods**
- `String listTeeTimes(String date)` - Returns list of available tee time on the given date
- `String bookTeeTimes(String teeTimeId, int partySize, String username)` - Sends a request to book a tee time slot given a party size and username, then returns the server response

**Event Management Methods**
- `String listEvents()`
- `String bookEvent()`

**Reservation Management Methods**
- `String getReservations(String username)`
- `String cancelReservations(String reservationId)`

**High-Level IO Methods**
- `boolean addUser(String username, String password, String firstName, String lastName, String email, boolean hasPaid)` - Sends a request to add a new user to the server, and returns true if the request succeeds

**Relationship to Other Classes:**
- Implemented by Client.java
- Follows project requirement: "Every program class with non-private methods must have a dedicated interface"
- Enables future client implementations without changing dependent code
- Ensures all Client operations are properly exposed through the interface contract

---

### 25. ClientTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for the Client class, responsible for verifying all network communication and user-driven action flows from the client perspective. Checks that client methods correctly format and send protocol commands, and accurately process server responses.

**Test Coverage (42+ tests):**
- Connection Handling: Tests for successful server connection and clean disconnect.
- Protocol Compliance: Ensures each client method (login, addUser, listTeeTimes, book, etc.) formats protocol messages correctly and handles server feedback.
- Error Handling: Simulates network errors, server disconnects, and command failures.
- Session Transitions: Verifies stateful flows—login, action, logout cycles.

**Relationship to Other Classes:**
- Tests Client.java and, optionally, integration with a real or mock server.
- Used to validate user stories and workflows as experienced from the client-facing side.

---

### 26. ClientMain.java (com.project.golf.client)
**Purpose:** Application entry point. Handles CLI or GUI input from users, presents menus, processes input, and calls Client methods to interact with the server and show results.

**Methods:**
- `main(String[] args)` – Startup, main event loop, shutdown

**Relationship to Other Classes:**
- Uses Client.java to execute user-chosen commands

---

### 27. Server.java (com.project.golf.server)
**Purpose:** Multi-threaded server that manages all network communication with connected clients for the reservation system. Listens on a configurable TCP port and creates a dedicated ServerWorker thread for each new connection to handle protocol commands and client session state.

**Key Features:**
- Concurrently accepts and manages multiple client connections
- Delegates each connection to a ServerWorker for isolated processing
- Central coordination point for client-server request flow
- Graceful shutdown and resource cleanup

**Fields:**
- `serverSocket` – TCP ServerSocket for incoming connections
- `port` – Port number server listens on
- `workerList` – Collection of active ServerWorker threads
- `clientSockets` – ArrayList storing all active client sockets

**Constructors:**
- `Server(int port)` – Binds to specified port

**Methods:**
- `start()` – Main loop accepting clients and starting ServerWorkers
- `stop()` – Stops server, terminates worker threads, closes resources
- `getPort()` – Returns current listening port
- (Utility) `log(String message)` – For server-side logging

**Relationship to Other Classes:**
- Implements ServerInterface
- Instantiates a ServerWorker per client
- Accesses core backend (database, managers) via the Model
- Entry point is invoked by ServerMain

---

### 28. ServerInterface.java (com.project.golf.server)
**Purpose:** Defines the contract for the Server's lifecycle management. It ensures that any server implementation provides the necessary mechanisms to start listening for connections, shut down gracefully, and report its configuration.

**Methods Required:**

- void start() throws IOException: Starts the server on the configured port. This method typically spawns a thread to ensure the server runs without blocking the main application flow.
- void stop() throws IOException: Stops the server by closing the ServerSocket and interrupting active worker threads.
- int getPort(): Returns the port number the server is currently listening on.

**Relationship to Other Classes:**
- Implemented by Server.java.
- Ensures strict adherence to the project requirement that every class with non-private methods has an interface.

---

### 29. ServerMain.java (com.project.golf.server)
**Purpose:** Launches the server application. Parses command-line arguments for server configuration and instantiates the Server, then listens for connections.

**Methods:**
- `main(String[] args)` – Parses args, sets up, starts server, handles graceful shutdown.

**Relationship to Other Classes:**
- Instantiates Server and may provide program status or error output for administrative/debug use.

---

### 30. ServerWorker.java (com.project.golf.server)
**Purpose:** Represents a per-client handler thread that manages one client’s session, including protocol parsing, authentication, booking and data queries, sending responses, and closing the connection.

**Key Features:**
- Parses protocol messages from client and dispatches to correct backend
- Maintains per-session state (e.g., login status)
- Thread-safe IO and resource access with clean exception handling

**Fields:**
- `Socket clientSocket`
- IO streams to client
- Client session state (e.g., authenticated user object)

**Constructors:**
- `ServerWorker(Socket socket, ...)` – Accepts client connection and dependency references

**Methods:**
- `run()` – Main protocol handling loop per client
- `processCommand(String command)` – Parses and responds to client requests
- Helpers for structured message formatting, errors, resource cleanup

**Relationship to Other Classes:**
- Spawned by Server for each client
- Communicates with DB, managers, etc.

---

### 31. ServerWorkerInterface.java (com.project.golf.server)

**Purpose:**  
Defines the contract for the per-client worker responsible for handling communication with a single connected client. This interface ensures consistent behavior across all ServerWorker implementations, including session management, command processing, and cleanup. Unlike the ServerInterface, this interface handles per-connection logic rather than server lifecycle.

**Methods:**
- `void start()` — Begins execution of the worker, typically launching a thread or asynchronously invoking `run()`, allowing it to start processing client input.
- `void stop()` — Stops the worker by closing the client connection and terminating its command-processing loop. Ensures cleanup of resources.
- `void run()` — Core loop that continuously reads client commands, processes them, and sends appropriate responses back to the client.

**Relationship to Other Classes:**
- Extends Runnable interface to use run()
- Implemented by `ServerWorker.java`
- Instantiated by `Server.java` (one per client connection)
- Interacts with backend systems (user manager, booking manager, database, etc.)
- Required to satisfy the project rule that every class with non-private methods must have its own interface


---

### 32. ServerTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for the Server class, validating correct startup, client connection handling, and graceful shutdown. Ensures robust multi-threaded operation and resource management under varying loads and network conditions.

**Test Coverage:**
- Startup/Shutdown: Verifies server binds to port, starts listening, then releases all resources cleanly on shutdown.
- Client Connection: Confirms that the server accepts multiple simultaneous client connections.
- Port Handling: Tests for correct error handling with invalid or unavailable ports.
- Resource Release & Cleanup: Checks all sockets and worker threads are terminated after server stop.

**Relationship to Other Classes:**
- Tests Server.java lifecycle and concurrency under both typical and error scenarios.
- May use dummy clients or mocks to simulate real-world usage and stress conditions.

---

### 33. ServerWorkerTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for ServerWorker, covering all aspects of client session handling. Focuses on protocol parsing, per-client concurrency, correct response generation, and edge cases in session and error handling.

**Test Coverage:**
- Command Parsing: Tests each supported client protocol command (login, booking, cancellation, queries, logout).
- Session State: Verifies correct management of session transitions (pre/post login, logout).
- Concurrency: Runs multiple ServerWorkers in parallel to test thread safety and isolation.
- Protocol Errors: Ensures robust handling of malformed input, unknown commands, early disconnects, and invalid session state transitions.

**Relationship to Other Classes:**
- Tests integration between ServerWorker, protocol logic, and core backend/database.
- Simulates end-to-end flows by issuing real or mock protocol commands.

---

---

## System Architecture

### Data Flow Overview
```
Phase 1 (Finished):
User/Reservations/TeeTime/Events/CourseSettings
          ↓
   Database (Singleton)
          ↓
   File Persistence (5 .txt files)

Phase 2 (Current):
Clients → Server → Database.getInstance() → Data Classes
                      ↓
                 File Storage
```

### Thread Safety Design
- **Database:** ReentrantReadWriteLock allows multiple concurrent reads, exclusive writes
- **TeeTime:** Synchronized methods prevent booking conflicts
- **Event/:** Synchronized booking/cancellation

### Singleton Pattern
- **Database** uses Singleton to ensure single source of truth
- Thread-safe lazy initialization via synchronized `getInstance()`
- All components access same database instance
- Critical for Phase 2 when multiple client connections access data simultaneously

### Domain Model Hierarchy
```
Reservations ← Created by TeeTime.bookTeeTime() or Event.bookReservation()
     ↓
TeeTime (golf-specific) OR Event (generic)
     ↓
Stored in Database
     ↓
Accessed by Server (Phase 2)
```

### Interface Implementation Summary
All classes properly implement their corresponding interfaces as required:
- Database implements DatabaseInterface (all public methods included)
- User implements UserInterface (all getters, setters, and toFileString)
- UserManager implements UserManagerInterface
- Reservations implements ReservationsInterface
- TeeTime implements TeeTimeInterface (all booking and availability methods)
- CourseSettings implements CourseSettingsInterface (all configuration methods)
- Event implements EventInterface
- Client implements ClientInterface
- Server implements ServerInterface
- ServerWorker implements ServerWorkerInterface

---

## Design Decisions and Rationale
- ServerWorker was added as a class that would be instantiated to handle each client
##

---

## File Structure and Persistence

*Same as phase 1*

### Data Files (Created in working directory):
1. **users.txt** - Format: `username,password,firstName,lastName,email,hasPaid,isAdmin`
2. **reservations.txt** - Format: `reservationId,username,date,time,partySize,teeBox,price,isPaid`
3. **teetimes.txt** - Format: `teeTimeId,date,time,teeBox,maxPartySize,pricePerPerson`
4. **settings.txt** - Format: `courseName,openingTime,closingTime,defaultPrice,interval,maxParty,numBoxes,advanceDays,Mon,Tue,Wed,Thu,Fri,Sat,Sun`
5. **events.txt** - Simplified format for Phase 1: `eventId,eventName` (will be enhanced in Phase 2)

### Persistence Strategy:
- **Automatic Load:** Database loads all data on first `getInstance()` call
- **Manual Save:** Call `Database.getInstance().saveToFile()` to persist changes
- **Atomic Operations:** Each file write is atomic (complete or rollback)
- **Phase 2 Enhancement:** Server will auto-save periodically
- **Note:** Event persistence is simplified in Phase 1 (only ID and name). Full event state persistence will be implemented in Phase 2 when events are more fully utilized.

---

## Testing Strategy

### Test Coverage Summary:
- **Total Test Classes:** 
- **Total Test Cases:** 
- **Total Test Code Lines:** 
- **Coverage:** 

### Testing Approach:
1. **Unit Tests:** Each class tested in isolation
2. **Concurrency Tests:** Thread safety verification
3. **Persistence Tests:** Save/load cycle validation
4. **Edge Cases:** Null handling, invalid input, boundary conditions

### Test Categories:
- **Functional Tests:** Verify correct behavior
- **Error Tests:** Verify proper error handling
- **Boundary Tests:** Test limits (capacity, party size, etc.)
- **Concurrency Tests:** Verify thread safety
- **Persistence Tests:** Verify data survives save/load

### Test Execution:
- All tests use JUnit 5 framework
- Tests use @BeforeEach and @AfterEach for proper isolation
- Database tests reset singleton between tests to ensure independence
- Thread safety tests use CountDownLatch for coordinated concurrent operations

---

## Phase 2 Files Summary

### Core Classes (12):
1. Client.java
2. ClientMain.java
3. Database.java
4. Event.java
5. Reservations.java
6. TeeTime.java
7. Server.java
8. ServerMain.java
9. ServerWorker.java
10. CourseSettings.java
11. User.java
12. UserManager.java

### Interfaces (9):
1. ClientInterface.java
2. DatabaseInterface.java
3. EventInterface.java
4. ReservationsInterface.java
5. TeeTimeInterface.java
6. ServerInterface.java
7. CourseSettingsInterface.java
8. UserInterface.java
9. UserManagerInterface.java

### Test Classes (10): 
1. ClientTest.java
2. CourseSettingsTest.java
3. DatabaseTest.java
4. EventTest.java
5. ReservationsTest.java
6. ServerTest.java
7. ServerWorkerTest.java
8. TeeTimeTest.java
9. UserManagerTest.java
10. UserTest.java

### Documentation:
1. README.md (this file)

**Total Files:** 31 Java files + 1 README = 32 files

---

# CS 180 Team Project - Phase 3: Golf Course Reservation System

## Team Members
- Ethan Billau (ebillau)
- Anoushka Chakravarty (chakr181)
- Nikhil Kodali (kodali3)
- Connor Landzettl (clandzet)
- Aman Wakankar (awakanka)

---

## Project Overview
This project implements a comprehensive database backend for a golf course reservation system (Option 1). The system supports user account management, tee time booking, reservation tracking, events, and course configuration management. All components are thread-safe and include persistent data storage.

---

## Phase Overview
This phase implements a working server-client methodology and GUI access for all methods.

---

# Compilation and Execution Instructions

## Prerequisites
- **Java:** JDK 25 (or later) installed and on your `PATH`  
- **OS note:**  
  - **macOS / Linux:** use `:` in classpaths  
  - **Windows (PowerShell / CMD):** use `;` in classpaths  
- **Project structure (already provided):**
  - `com/` – all source files
  - `lib/junit-platform-console-standalone-6.0.1.jar` – JUnit test runner
  - `out/` – directory for compiled `.class` files (can be empty initially)

From now on, assume your terminal is **inside the project root** (the folder that contains `com`, `lib`, and `out`).

---

## Compiling the Project

### macOS / Linux
Compile all `.java` files into the `out` directory and include the JUnit jar on the classpath:

```bash
# From project root
javac -d out \
  -cp "lib/junit-platform-console-standalone-6.0.1.jar" \
  $(find com -name "*.java")
```

If compilation succeeds, the compiled classes will be created under `out/com/...`.

### Windows (PowerShell)
Collect all `.java` file paths and compile into `out`:

```powershell
# From project root (PowerShell)
$files = Get-ChildItem -Path .\com -Recurse -Filter *.java | ForEach-Object { $_.FullName }
javac -d out -cp "lib\junit-platform-console-standalone-6.0.1.jar" $files
```

If compilation succeeds, the compiled classes will be created under `out\com\...`.

---

## Running the Tests
We use the JUnit Platform Console Standalone jar in `lib/`.

### macOS / Linux
```bash
# From project root
java -jar lib/junit-platform-console-standalone-6.0.1.jar \
  -cp "out" \
  --scan-class-path
```

### Windows (PowerShell)
```powershell
# From project root (PowerShell)
java -jar "lib\junit-platform-console-standalone-6.0.1.jar" `
  -cp "out" `
  --scan-class-path
```

What this does:
- Discovers all test classes under `out` (e.g. `com.project.golf.tests.*`)
- Runs tests and prints a summary of passed/failed tests in the terminal

### Running Individual Test Classes
To run a single test class, specify its fully-qualified class name with `--select-class`.

Example: run `com.project.golf.tests.DatabaseTest`

macOS / Linux:
```bash
java -jar lib/junit-platform-console-standalone-6.0.1.jar \
  -cp "out" \
  --select-class "com.project.golf.tests.DatabaseTest"
```

Windows (PowerShell):
```powershell
java -jar "lib\junit-platform-console-standalone-6.0.1.jar" `
  -cp "out" `
  --select-class "com.project.golf.tests.DatabaseTest"
```

Replace `DatabaseTest` with any other test class under `com.project.golf.tests`.

---

## Execution (Client–Server)

- **Server main class:** `com.project.golf.server.ServerMain`  
- **Client main class:** `com.project.golf.client.ClientMain`

The typical workflow is:
1. Start the server in one terminal.
2. Start the client in another terminal.
3. Interact with the client and observe server logs showing communication.

### 1. Start the Server (Terminal 1)

macOS / Linux:
```bash
# From project root
java -cp "out:lib/junit-platform-console-standalone-6.0.1.jar" \
  com.project.golf.server.ServerMain
```

Windows (PowerShell):
```powershell
# From project root
java -cp "out;lib\junit-platform-console-standalone-6.0.1.jar" `
  com.project.golf.server.ServerMain
```

- The server should start listening for client connections.
- Expect a startup message indicating port/status. Keep this terminal open.

### 2. Start the Client (Terminal 2)

macOS / Linux:
```bash
# From project root
java -cp "out:lib/junit-platform-console-standalone-6.0.1.jar" \
  com.project.golf.client.ClientMain
```

Windows (PowerShell):
```powershell
# From project root
java -cp "out;lib\junit-platform-console-standalone-6.0.1.jar" `
  com.project.golf.client.ClientMain
```

- The client will connect to the server and present an interface (e.g., login, menu options).

---

## Demonstrating Client–Server Communication (Two-Terminal Setup)

Follow these steps for a clear demo or grading:

1. Open two terminals:
   - Terminal 1: server
   - Terminal 2: client
2. In both terminals, `cd` to the project root.
3. Start the server in Terminal 1 (see "Start the Server" above).
   - Point out any message such as “Server listening on port …”.
4. Start the client in Terminal 2 (see "Start the Client" above).
5. In Terminal 2 (client), perform an action (examples):
   - Log in
   - Create a reservation
   - List tee times
6. Immediately point to Terminal 1 (server) and show the corresponding logs, for example:
   - Received LOGIN request for user ...
   - Fetching user from database...
7. This proves:
   - The client sends requests.
   - The server receives and processes them.
   - Responses are returned to the client and shown in Terminal 2.

### Optional – Multiple Clients
- Open an additional terminal (Terminal 3).
- Start another client using the same client command.
- Show that multiple clients can connect and that each action is logged on the server terminal.

### Shutdown
- Exit the client(s) using the provided menu/commands (e.g., “Quit” option).
- Stop the server by closing Terminal 1 or using any clean shutdown command provided by `ServerMain`.

---

## Submission Details
### Vocareum Workspace Submission
- **Submitted by:** Ethan Billau
- **Date:** Nov 21, 2025

---

## Detailed Class Descriptions (Phase 2)

### 23. Client.java (com.project.golf.client)
**Purpose:** Manages network connection to the server, formats protocol commands for user actions, handles server responses, and provides a high-level API for user operations (login, booking, listing, etc.).

**Key Features:**
- Establishes and manages persistent socket connection to server
- High-level methods for all user functions (login, addUser, listTeeTimes, book, etc.)
- Handles errors and connection loss gracefully

**Fields:**
- `Socket serverSocket`
- IO streams for network communication
- Connection and user session status fields

**Constructors:**
- `Client()` or equivalent setup/connection constructor

**Methods:**
- `connect(String host, int port)` – Open server connection
- `disconnect()` – Clean up and close
- `sendCommand(String cmd)` – Protocol send/receive
- High-level wrappers: `login(...)`, `addUser(...)`, `listTeeTimes(...)`, etc.

**Relationship to Other Classes:**
- Implements ClientInterface
- Used by ClientMain for user interaction

---

### 24. ClientInterface.java (com.project.golf.client)
**Purpose:** Defines the contract for all Client operations. It enforces a clear separation between low-level network handling (connecting/sending) and high-level application commands (booking/login). This interface ensures that the Client is consistent for the UI.
**All public methods in Client.java must be declared in this interface as per project requirements.**

**Methods Required:**

**Network IO Methods**
- `void connect(String host, int port)` - Establishes a network connection to the server at the specified host and port
- `void disconnect()` - Closes the connection to the server if it is open
- `String sendCommand(String command)` - Sends a raw command string to the server and returns the server's response
- `boolean login(String username, String password)` - Sends login credentials to the server and returns true if authentication succeeds

**TeeTime Management Methods**
- `String listTeeTimes(String date)` - Returns list of available tee time on the given date
- `String bookTeeTimes(String teeTimeId, int partySize, String username)` - Sends a request to book a tee time slot given a party size and username, then returns the server response

**Event Management Methods**
- `String listEvents()`
- `String bookEvent()`

**Reservation Management Methods**
- `String getReservations(String username)`
- `String cancelReservations(String reservationId)`

**High-Level IO Methods**
- `boolean addUser(String username, String password, String firstName, String lastName, String email, boolean hasPaid)` - Sends a request to add a new user to the server, and returns true if the request succeeds

**Relationship to Other Classes:**
- Implemented by Client.java
- Follows project requirement: "Every program class with non-private methods must have a dedicated interface"
- Enables future client implementations without changing dependent code
- Ensures all Client operations are properly exposed through the interface contract

---

### 25. ClientTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for the Client class, responsible for verifying all network communication and user-driven action flows from the client perspective. Checks that client methods correctly format and send protocol commands, and accurately process server responses.

**Test Coverage (42+ tests):**
- Connection Handling: Tests for successful server connection and clean disconnect.
- Protocol Compliance: Ensures each client method (login, addUser, listTeeTimes, book, etc.) formats protocol messages correctly and handles server feedback.
- Error Handling: Simulates network errors, server disconnects, and command failures.
- Session Transitions: Verifies stateful flows—login, action, logout cycles.

**Relationship to Other Classes:**
- Tests Client.java and, optionally, integration with a real or mock server.
- Used to validate user stories and workflows as experienced from the client-facing side.

---

### 26. ClientMain.java (com.project.golf.client)
**Purpose:** Application entry point. Handles CLI or GUI input from users, presents menus, processes input, and calls Client methods to interact with the server and show results.

**Methods:**
- `main(String[] args)` – Startup, main event loop, shutdown

**Relationship to Other Classes:**
- Uses Client.java to execute user-chosen commands

---

### 27. Server.java (com.project.golf.server)
**Purpose:** Multi-threaded server that manages all network communication with connected clients for the reservation system. Listens on a configurable TCP port and creates a dedicated ServerWorker thread for each new connection to handle protocol commands and client session state.

**Key Features:**
- Concurrently accepts and manages multiple client connections
- Delegates each connection to a ServerWorker for isolated processing
- Central coordination point for client-server request flow
- Graceful shutdown and resource cleanup

**Fields:**
- `serverSocket` – TCP ServerSocket for incoming connections
- `port` – Port number server listens on
- `workerList` – Collection of active ServerWorker threads
- `clientSockets` – ArrayList storing all active client sockets

**Constructors:**
- `Server(int port)` – Binds to specified port

**Methods:**
- `start()` – Main loop accepting clients and starting ServerWorkers
- `stop()` – Stops server, terminates worker threads, closes resources
- `getPort()` – Returns current listening port
- (Utility) `log(String message)` – For server-side logging

**Relationship to Other Classes:**
- Implements ServerInterface
- Instantiates a ServerWorker per client
- Accesses core backend (database, managers) via the Model
- Entry point is invoked by ServerMain

---

### 28. ServerInterface.java (com.project.golf.server)
**Purpose:** Defines the contract for the Server's lifecycle management. It ensures that any server implementation provides the necessary mechanisms to start listening for connections, shut down gracefully, and report its configuration.

**Methods Required:**

- void start() throws IOException: Starts the server on the configured port. This method typically spawns a thread to ensure the server runs without blocking the main application flow.
- void stop() throws IOException: Stops the server by closing the ServerSocket and interrupting active worker threads.
- int getPort(): Returns the port number the server is currently listening on.

**Relationship to Other Classes:**
- Implemented by Server.java.
- Ensures strict adherence to the project requirement that every class with non-private methods has an interface.

---

### 29. ServerMain.java (com.project.golf.server)
**Purpose:** Launches the server application. Parses command-line arguments for server configuration and instantiates the Server, then listens for connections.

**Methods:**
- `main(String[] args)` – Parses args, sets up, starts server, handles graceful shutdown.

**Relationship to Other Classes:**
- Instantiates Server and may provide program status or error output for administrative/debug use.

---

### 30. ServerWorker.java (com.project.golf.server)
**Purpose:** Represents a per-client handler thread that manages one client’s session, including protocol parsing, authentication, booking and data queries, sending responses, and closing the connection.

**Key Features:**
- Parses protocol messages from client and dispatches to correct backend
- Maintains per-session state (e.g., login status)
- Thread-safe IO and resource access with clean exception handling

**Fields:**
- `Socket clientSocket`
- IO streams to client
- Client session state (e.g., authenticated user object)

**Constructors:**
- `ServerWorker(Socket socket, ...)` – Accepts client connection and dependency references

**Methods:**
- `run()` – Main protocol handling loop per client
- `processCommand(String command)` – Parses and responds to client requests
- Helpers for structured message formatting, errors, resource cleanup

**Relationship to Other Classes:**
- Spawned by Server for each client
- Communicates with DB, managers, etc.

---

### 31. ServerWorkerInterface.java (com.project.golf.server)

**Purpose:**  
Defines the contract for the per-client worker responsible for handling communication with a single connected client. This interface ensures consistent behavior across all ServerWorker implementations, including session management, command processing, and cleanup. Unlike the ServerInterface, this interface handles per-connection logic rather than server lifecycle.

**Methods:**
- `void start()` — Begins execution of the worker, typically launching a thread or asynchronously invoking `run()`, allowing it to start processing client input.
- `void stop()` — Stops the worker by closing the client connection and terminating its command-processing loop. Ensures cleanup of resources.
- `void run()` — Core loop that continuously reads client commands, processes them, and sends appropriate responses back to the client.

**Relationship to Other Classes:**
- Extends Runnable interface to use run()
- Implemented by `ServerWorker.java`
- Instantiated by `Server.java` (one per client connection)
- Interacts with backend systems (user manager, booking manager, database, etc.)
- Required to satisfy the project rule that every class with non-private methods must have its own interface


---

### 32. ServerTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for the Server class, validating correct startup, client connection handling, and graceful shutdown. Ensures robust multi-threaded operation and resource management under varying loads and network conditions.

**Test Coverage:**
- Startup/Shutdown: Verifies server binds to port, starts listening, then releases all resources cleanly on shutdown.
- Client Connection: Confirms that the server accepts multiple simultaneous client connections.
- Port Handling: Tests for correct error handling with invalid or unavailable ports.
- Resource Release & Cleanup: Checks all sockets and worker threads are terminated after server stop.

**Relationship to Other Classes:**
- Tests Server.java lifecycle and concurrency under both typical and error scenarios.
- May use dummy clients or mocks to simulate real-world usage and stress conditions.

---

### 33. ServerWorkerTest.java (com.project.golf.tests)
**Purpose:** JUnit test suite for ServerWorker, covering all aspects of client session handling. Focuses on protocol parsing, per-client concurrency, correct response generation, and edge cases in session and error handling.

**Test Coverage:**
- Command Parsing: Tests each supported client protocol command (login, booking, cancellation, queries, logout).
- Session State: Verifies correct management of session transitions (pre/post login, logout).
- Concurrency: Runs multiple ServerWorkers in parallel to test thread safety and isolation.
- Protocol Errors: Ensures robust handling of malformed input, unknown commands, early disconnects, and invalid session state transitions.

**Relationship to Other Classes:**
- Tests integration between ServerWorker, protocol logic, and core backend/database.
- Simulates end-to-end flows by issuing real or mock protocol commands.

---

---

## System Architecture

### Data Flow Overview
```
Phase 1 (Finished):
User/Reservations/TeeTime/Events/CourseSettings
          ↓
   Database (Singleton)
          ↓
   File Persistence (5 .txt files)

Phase 2 (Current):
Clients → Server → Database.getInstance() → Data Classes
                      ↓
                 File Storage
```

### Thread Safety Design
- **Database:** ReentrantReadWriteLock allows multiple concurrent reads, exclusive writes
- **TeeTime:** Synchronized methods prevent booking conflicts
- **Event/:** Synchronized booking/cancellation

### Singleton Pattern
- **Database** uses Singleton to ensure single source of truth
- Thread-safe lazy initialization via synchronized `getInstance()`
- All components access same database instance
- Critical for Phase 2 when multiple client connections access data simultaneously

### Domain Model Hierarchy
```
Reservations ← Created by TeeTime.bookTeeTime() or Event.bookReservation()
     ↓
TeeTime (golf-specific) OR Event (generic)
     ↓
Stored in Database
     ↓
Accessed by Server (Phase 2)
```

### Interface Implementation Summary
All classes properly implement their corresponding interfaces as required:
- Database implements DatabaseInterface (all public methods included)
- User implements UserInterface (all getters, setters, and toFileString)
- UserManager implements UserManagerInterface
- Reservations implements ReservationsInterface
- TeeTime implements TeeTimeInterface (all booking and availability methods)
- CourseSettings implements CourseSettingsInterface (all configuration methods)
- Event implements EventInterface
- Client implements ClientInterface
- Server implements ServerInterface
- ServerWorker implements ServerWorkerInterface

---

## Design Decisions and Rationale
- ServerWorker was added as a class that would be instantiated to handle each client
##

---

## File Structure and Persistence

*Same as phase 1*

### Data Files (Created in working directory):
1. **users.txt** - Format: `username,password,firstName,lastName,email,hasPaid,isAdmin`
2. **reservations.txt** - Format: `reservationId,username,date,time,partySize,teeBox,price,isPaid`
3. **teetimes.txt** - Format: `teeTimeId,date,time,teeBox,maxPartySize,pricePerPerson`
4. **settings.txt** - Format: `courseName,openingTime,closingTime,defaultPrice,interval,maxParty,numBoxes,advanceDays,Mon,Tue,Wed,Thu,Fri,Sat,Sun`
5. **events.txt** - Simplified format for Phase 1: `eventId,eventName` (will be enhanced in Phase 2)

### Persistence Strategy:
- **Automatic Load:** Database loads all data on first `getInstance()` call
- **Manual Save:** Call `Database.getInstance().saveToFile()` to persist changes
- **Atomic Operations:** Each file write is atomic (complete or rollback)
- **Phase 2 Enhancement:** Server will auto-save periodically
- **Note:** Event persistence is simplified in Phase 1 (only ID and name). Full event state persistence will be implemented in Phase 2 when events are more fully utilized.

---

## Testing Strategy

### Test Coverage Summary:
- **Total Test Classes:** 
- **Total Test Cases:** 
- **Total Test Code Lines:** 
- **Coverage:** 

### Testing Approach:
1. **Unit Tests:** Each class tested in isolation
2. **Concurrency Tests:** Thread safety verification
3. **Persistence Tests:** Save/load cycle validation
4. **Edge Cases:** Null handling, invalid input, boundary conditions

### Test Categories:
- **Functional Tests:** Verify correct behavior
- **Error Tests:** Verify proper error handling
- **Boundary Tests:** Test limits (capacity, party size, etc.)
- **Concurrency Tests:** Verify thread safety
- **Persistence Tests:** Verify data survives save/load

### Test Execution:
- All tests use JUnit 5 framework
- Tests use @BeforeEach and @AfterEach for proper isolation
- Database tests reset singleton between tests to ensure independence
- Thread safety tests use CountDownLatch for coordinated concurrent operations

---

## Phase 2 Files Summary

### Core Classes (12):
1. Client.java
2. ClientMain.java
3. Database.java
4. Event.java
5. Reservations.java
6. TeeTime.java
7. Server.java
8. ServerMain.java
9. ServerWorker.java
10. CourseSettings.java
11. User.java
12. UserManager.java

### Interfaces (9):
1. ClientInterface.java
2. DatabaseInterface.java
3. EventInterface.java
4. ReservationsInterface.java
5. TeeTimeInterface.java
6. ServerInterface.java
7. CourseSettingsInterface.java
8. UserInterface.java
9. UserManagerInterface.java

### Test Classes (10): 
1. ClientTest.java
2. CourseSettingsTest.java
3. DatabaseTest.java
4. EventTest.java
5. ReservationsTest.java
6. ServerTest.java
7. ServerWorkerTest.java
8. TeeTimeTest.java
9. UserManagerTest.java
10. UserTest.java

### Documentation:
1. README.md (this file)

**Total Files:** 31 Java files + 1 README = 32 files

---

