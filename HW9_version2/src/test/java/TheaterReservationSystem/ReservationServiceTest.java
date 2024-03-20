package TheaterReservationSystem;

import TheaterReservationSystem.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReservationServiceTest {

    private Theater theater;
    private ByteArrayOutputStream capturedOutput;

    @BeforeEach
    public void setUp() throws NoAccessibleRowsException {
        List<Integer> wheelchairRows = new ArrayList<>();
        wheelchairRows.add(6);
        wheelchairRows.add(10);

        theater = new Theater("Test Theater", 15, 10, wheelchairRows);

        // Redirect console output for testing
        capturedOutput = new ByteArrayOutputStream();
        System.setOut(new PrintStream(capturedOutput));
    }

    @Test
    public void testReserveSeatsValid() {
        ReservationService reservationService = new ReservationService(theater);
        reservationService.reserveSeats(theater, 2, "John", false);

        String actualOutput = capturedOutput.toString().trim();
        assertEquals("I’ve reserved 2 seats for you at the Roxy in row 7, John.", actualOutput);
    }



    @Test
    public void testReserveSeatsInvalidNumSeats() {
        ReservationService reservationService = new ReservationService(theater);
        reservationService.reserveSeats(theater, 0, "Alice", false);

        assertEquals("Invalid number of seats.", capturedOutput.toString().trim());
    }

    @Test
    public void testReserveSeatsNoSuitableSeats() {
        ReservationService reservationService = new ReservationService(theater);
        reservationService.reserveSeats(theater, 12, "Bob", false);
        //System.out.println(capturedOutput.toString().trim());

        assertEquals("Sorry, we can't reserve more than 10 seats in a single row.", capturedOutput.toString().trim());
    }

    @Test
    public void testCheckValidSeatNumberValid() {
        ReservationService reservationService = new ReservationService(theater);
        assertTrue(reservationService.checkValidSeatNumber("7"));
    }

    @Test
    public void testCheckValidSeatNumberInvalid() {
        ReservationService reservationService = new ReservationService(theater);
        assertFalse(reservationService.checkValidSeatNumber("20"));
    }

    @Test
    public void testDisplaySeatMap() {
        ReservationService reservationService = new ReservationService(theater);
        reservationService.displaySeatMap();
    }

    @Test
    public void testReservationConfirmation() {
        ReservationService reservationService = new ReservationService(theater);
        reservationService.reservationConfirmation();
        assertEquals("Reservation confirmed!", capturedOutput.toString().trim());
    }

    @Test
    public void testFindBestRow() {
        // Implement test cases to cover different scenarios for findBestRow method
    }

    @Test
    public void testEqualsAndHashCode() throws NoAccessibleRowsException {
        List<Integer> wheelchairRows = new ArrayList<>();
        wheelchairRows.add(6);
        wheelchairRows.add(10);

        Theater theater1 = new Theater("Test Theater", 15, 10, wheelchairRows);
        Theater theater2 = new Theater("Another Theater", 10, 8, wheelchairRows);

        ReservationService service1 = new ReservationService(theater1);
        ReservationService service2 = new ReservationService(theater1);
        ReservationService service3 = new ReservationService(theater2);

        assertTrue(service1.equals(service2));
        assertEquals(service1.hashCode(), service2.hashCode());
        assertFalse(service1.equals(service3));
        assertNotEquals(service1, "String");
        assertNotEquals(service1, null);
    }
    @Test
    public void testToString() throws NoAccessibleRowsException {
        List<Integer> wheelchairRows = new ArrayList<>();
        wheelchairRows.add(6);
        wheelchairRows.add(10);

        Theater theater = new Theater("Test Theater", 15, 10, wheelchairRows);
        ReservationService service = new ReservationService(theater);

        String expectedToString = "ReservationService{" +
                "theater=" + this.theater +
                '}';
        assertEquals(expectedToString, service.toString());
    }

    public void restoreStreams() {
        // Reset System.out
        System.setOut(System.out);
    }
}
