import java.io.*;
import java.util.*;

class Flight implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id, name, origin, destination;
    private int availableSeats;
    private double price;

    public Flight(String id, String name, String origin, String destination, int availableSeats, double price) {
        this.id = id;
        this.name = name;
        this.origin = origin;
        this.destination = destination;
        this.availableSeats = availableSeats;
        this.price = price;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getOrigin() { return origin; }
    public String getDestination() { return destination; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPrice() { return price; }

    public void bookSeat(int count) { availableSeats -= count; }
    public void cancelSeat(int count) { availableSeats += count; }
    public void setPrice(double price) { this.price = price; }
    public void setAvailableSeats(int seats) { this.availableSeats = seats; }
}

class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    private String pnr, flightId, firstName, lastName, phoneNumber, address, passportNumber, tripType, travelClass;
    private double price;
    private int passengerCount;
    private String departureDate, returnDate;
    private boolean checkedIn = false;
    private String seatNumber = "Not Assigned";

    public Ticket(String flightId, String firstName, String lastName, String phoneNumber, String address, String passportNumber,
                  double price, int passengerCount, String tripType, String travelClass, String departureDate, String returnDate) {
        this.pnr = generatePNR();
        this.flightId = flightId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.address = address;
        this.passportNumber = passportNumber;
        this.price = price;
        this.passengerCount = passengerCount;
        this.tripType = tripType;
        this.travelClass = travelClass;
        this.departureDate = departureDate;
        this.returnDate = returnDate;
    }

    private String generatePNR() {
        return "PNR" + new Random().nextInt(100000, 999999);
    }

    public String getPNR() { return pnr; }
    public String getFlightId() { return flightId; }
    public String getFullName() { return firstName + " " + lastName; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getAddress() { return address; }
    public String getPassportNumber() { return passportNumber; }
    public double getPrice() { return price; }
    public int getPassengerCount() { return passengerCount; }
    public String getTripType() { return tripType; }
    public String getTravelClass() { return travelClass; }
    public String getDepartureDate() { return departureDate; }
    public String getReturnDate() { return returnDate; }
    public boolean isCheckedIn() { return checkedIn; }
    public String getSeatNumber() { return seatNumber; }

    public void setSeatNumber(String seatNumber) {
        this.seatNumber = seatNumber;
        this.checkedIn = true;
    }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }
    public void setAddress(String address) { this.address = address; }
}

class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String username, password;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public boolean checkPassword(String pwd) { return this.password.equals(pwd); }
}

class Admin extends User {
    private static final long serialVersionUID = 1L;

    public Admin(String username, String password) {
        super(username, password);
    }

    public void addFlight(List<Flight> flights, Flight flight) {
        flights.add(flight);
        System.out.println("Flight added successfully.");
    }
}

public class AirCrownMain {
    static Scanner scanner = new Scanner(System.in);
    static List<Flight> flights = new ArrayList<>();
    static Map<String, Ticket> tickets = new HashMap<>();
    static Map<String, User> users = new HashMap<>();
    static final String FLIGHT_FILE = "flights.dat";
    static final String TICKET_FILE = "tickets.dat";
    static final String USER_FILE = "users.dat";
    static User loggedInUser = null;

    public static void main(String[] args) {
        loadFlights();
        loadTickets();
        loadUsers();

        while (true) {
            System.out.println("\n--- AirCrown Airlines ---");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Search Flights");
            System.out.println("4. Book Ticket (Login Required)");
            System.out.println("5. Manage Booking");
            System.out.println("6. Admin: Add Flight");
            System.out.println("7. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> registerUser();
                case 2 -> loginUser();
                case 3 -> searchFlights();
                case 4 -> {
                    if (loggedInUser != null) bookTicket();
                    else System.out.println("You must login to book a ticket.");
                }
                case 5 -> manageBooking();
                case 6 -> {
                    if (loggedInUser instanceof Admin admin) {
                        addFlight(admin);
                    } else {
                        System.out.println("Only admins can add flights.");
                    }
                }
                case 7 -> {
                    saveFlights();
                    saveTickets();
                    saveUsers();
                    System.out.println("Exiting...");
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }
    }

    static void addFlight(Admin admin) {
        System.out.print("Enter Flight ID: ");
        String id = scanner.nextLine();
        System.out.print("Flight Name: ");
        String name = scanner.nextLine();
        System.out.print("Origin: ");
        String origin = scanner.nextLine();
        System.out.print("Destination: ");
        String destination = scanner.nextLine();
        System.out.print("Available Seats: ");
        int seats = scanner.nextInt();
        System.out.print("Price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();

        Flight newFlight = new Flight(id, name, origin, destination, seats, price);
        admin.addFlight(flights, newFlight);
        saveFlights();
    }

    static void registerUser() {
        System.out.print("Choose Username: ");
        String username = scanner.nextLine();
        if (users.containsKey(username)) {
            System.out.println("Username already exists.");
            return;
        }
        System.out.print("Choose Password: ");
        String password = scanner.nextLine();
        users.put(username, new User(username, password));
        saveUsers();
        System.out.println("Registration successful.");
    }

    static void loginUser() {
        System.out.print("Enter Username: ");
        String username = scanner.nextLine();
        System.out.print("Enter Password: ");
        String password = scanner.nextLine();

        User user = users.get(username);
        if (user != null && user.checkPassword(password)) {
            loggedInUser = user;
            System.out.println("Login successful. Welcome, " + username + "!");
        } else {
            System.out.println("Invalid credentials.");
        }
    }

    static void searchFlights() {
        System.out.print("From: ");
        String from = scanner.nextLine();
        System.out.print("To: ");
        String to = scanner.nextLine();

        System.out.println("Available Flights:");
        for (Flight flight : flights) {
            if (flight.getOrigin().equalsIgnoreCase(from) && flight.getDestination().equalsIgnoreCase(to)) {
                System.out.println("Flight ID: " + flight.getId() + ", Name: " + flight.getName() + ", Price: " + flight.getPrice());
            }
        }
    }

    static void bookTicket() {
        System.out.print("Enter Flight ID to book: ");
        String flightId = scanner.nextLine();
        Flight selectedFlight = null;
        for (Flight f : flights) {
            if (f.getId().equals(flightId)) {
                selectedFlight = f;
                break;
            }
        }
        if (selectedFlight == null) {
            System.out.println("Flight not found.");
            return;
        }

        System.out.print("First Name: ");
        String firstName = scanner.nextLine();
        System.out.print("Last Name: ");
        String lastName = scanner.nextLine();
        System.out.print("Phone Number: ");
        String phone = scanner.nextLine();
        System.out.print("Address: ");
        String address = scanner.nextLine();
        System.out.print("Passport No (mandatory for international flights): ");
        String passport = scanner.nextLine();
        System.out.print("Trip Type (OneWay/Round): ");
        String tripType = scanner.nextLine();
        System.out.print("Class (Economy/Business): ");
        String travelClass = scanner.nextLine();
        System.out.print("Departure Date: ");
        String depDate = scanner.nextLine();
        String returnDate = "";
        if (tripType.equalsIgnoreCase("Round")) {
            System.out.print("Return Date: ");
            returnDate = scanner.nextLine();
        }
        System.out.print("Number of Passengers: ");
        int count = scanner.nextInt();
        scanner.nextLine();

        if (selectedFlight.getAvailableSeats() < count) {
            System.out.println("Not enough seats available.");
            return;
        }

        double totalPrice = selectedFlight.getPrice() * count;
        selectedFlight.bookSeat(count);

        Ticket ticket = new Ticket(flightId, firstName, lastName, phone, address, passport, totalPrice, count,
                tripType, travelClass, depDate, returnDate);
        tickets.put(ticket.getPNR(), ticket);

        System.out.println("Booking successful! PNR: " + ticket.getPNR());
        saveFlights();
        saveTickets();
    }

    static void manageBooking() {
        System.out.print("Enter your PNR: ");
        String pnr = scanner.nextLine();
        Ticket ticket = tickets.get(pnr);

        if (ticket == null) {
            System.out.println("Ticket not found.");
            return;
        }

        System.out.println("1. View Ticket\n2. Update Contact\n3. Cancel Ticket\n4. Web Check-In");
        System.err.println("Enter option:");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1 -> {
                System.out.println("Name: " + ticket.getFullName());
                System.out.println("Flight ID: " + ticket.getFlightId());
                System.out.println("Trip: " + ticket.getTripType());
                System.out.println("Class: " + ticket.getTravelClass());
                System.out.println("Departure: " + ticket.getDepartureDate());
                if (!ticket.getReturnDate().isEmpty()) System.out.println("Return: " + ticket.getReturnDate());
                System.out.println("Checked-in: " + (ticket.isCheckedIn() ? "Yes" : "No"));
                System.out.println("Seat: " + ticket.getSeatNumber());
            }
            case 2 -> {
                System.out.print("New Phone Number: ");
                ticket.setPhoneNumber(scanner.nextLine());
                System.out.print("New Address: ");
                ticket.setAddress(scanner.nextLine());
                System.out.println("Details updated.");
            }
            case 3 -> {
                Flight flight = null;
                for (Flight f : flights) {
                    if (f.getId().equals(ticket.getFlightId())) flight = f;
                }
                if (flight != null) flight.cancelSeat(ticket.getPassengerCount());
                tickets.remove(pnr);
                System.out.println("Ticket cancelled.");
            }
            case 4 -> {
                if (ticket.isCheckedIn()) {
                    System.out.println("Already checked-in. Seat: " + ticket.getSeatNumber());
                    return;
                }
                System.out.print("Enter desired Seat Number: ");
                String seat = scanner.nextLine();
                ticket.setSeatNumber(seat);
                System.out.println("Check-in successful. Seat assigned: " + seat);
            }
            default -> System.out.println("Invalid choice.");
        }
        saveTickets();
        saveFlights();
    }

    static void loadUsers() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(USER_FILE))) {
            users = (Map<String, User>) ois.readObject();
        } catch (Exception e) {
            users = new HashMap<>();
        }
    }

    static void saveUsers() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(USER_FILE))) {
            oos.writeObject(users);
        } catch (IOException e) {
            System.out.println("Error saving users.");
        }
    }

    static void loadFlights() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(FLIGHT_FILE))) {
            flights = (List<Flight>) ois.readObject();
        } catch (Exception e) {
            flights = new ArrayList<>();
        }
    }

    static void saveFlights() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(FLIGHT_FILE))) {
            oos.writeObject(flights);
        } catch (IOException e) {
            System.out.println("Error saving flights.");
        }
    }

    static void loadTickets() {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TICKET_FILE))) {
            tickets = (Map<String, Ticket>) ois.readObject();
        } catch (Exception e) {
            tickets = new HashMap<>();
        }
    }

    static void saveTickets() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(TICKET_FILE))) {
            oos.writeObject(tickets);
        } catch (IOException e) {
            System.out.println("Error saving tickets.");
        }
    }
}
