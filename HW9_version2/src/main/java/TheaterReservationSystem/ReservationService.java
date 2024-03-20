package TheaterReservationSystem;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The ReservationService class provides methods to reserve seats in a theater based on user preferences.
 * It considers wheelchair accessibility and proximity to the center row for seat selection.
 */
public class ReservationService {
    private Theater theater;

    /**
     * cut by the middle point, used to find the row closest to the center
     */
    private static final int CUT_BY_MIDDLE = 2;
    private static final int MIN_SEAT = 0;

    /**
     * Constructs a new ReservationService object with certain theater
     * @param theater theater, as a Theater object
     */
    public ReservationService(Theater theater) {
        this.theater = theater;
    }

    /**
     * Reserves the specified number of seats in the given theater for a party.
     *
     * @param theater             The theater in which seats will be reserved.
     * @param numSeats            The number of seats to be reserved.
     * @param name                The name of the party reserving seats.
     * @param wheelchairAccessible Whether wheelchair accessible seats are required.
     */
    public void reserveSeats(Theater theater, int numSeats, String name, boolean wheelchairAccessible) {
        if (numSeats <= MIN_SEAT) {
            System.out.println("Invalid number of seats.");
            return;
        }

        if (numSeats > theater.getTotalSeatsInARow()) {
            System.out.println("Sorry, we can't reserve more than " + theater.getTotalSeatsInARow() + " seats in a single row.");
            return;
        }

        List<Row> candidateRows;
        if (wheelchairAccessible) {
            candidateRows = theater.getRows().stream()
                    .filter(Row::isWheelchairAccessible)
                    .collect(Collectors.toList());
        } else {
            candidateRows = theater.getRows().stream()
                    .filter(row -> !row.isWheelchairAccessible())
                    .collect(Collectors.toList());
        }

        Row bestRow = findBestRow(candidateRows, numSeats, wheelchairAccessible);
        if (bestRow == null) {
            System.out.println("Sorry, we don’t have that many seats together for you.");
            return;
        }

        List<Seat> reservedSeats = new ArrayList<>();
        int numReserved = MIN_SEAT;
        for (Seat seat : bestRow) {
            if (numReserved == numSeats) {
                break;
            }
            if (seat.getReservedFor() == null) {
                seat.reserve(name);
                reservedSeats.add(seat);
                numReserved++;
            }
        }

        if (numReserved == numSeats) {
            System.out.println("I’ve reserved " + numSeats + " seats for you at the Roxy in row " + bestRow.getRowNumber() + ", " + name + ".");
        } else {
            System.out.println("Sorry, we don’t have that many seats together for you.");
            reservedSeats.forEach(Seat::notReserved);
        }
    }


    /**
     * method to check if the seat number is valid
     * @param usersInputForSeat user's input of seat number
     * @return true if it is valid, otherwise false
     */

    public boolean checkValidSeatNumber(String usersInputForSeat) {
        try {
            int seat = Integer.parseInt(usersInputForSeat);
            return seat > MIN_SEAT && seat <= theater.getTotalRows();
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * show the seat map
     */
    public void displaySeatMap() {
        System.out.println(theater);
    }

    /**
     * Checks if the number of seats requested by the user is valid for reservation.
     *
     * @param theater The Theater object where the reservation is being made.
     * @param reservedSeatNumber The number of seats a user is trying to reserve.
     * @return true if the number of seats is valid for reservation, otherwise false.
     */
    public boolean isValidNumberForReservation(Theater theater, int reservedSeatNumber) {
        if (reservedSeatNumber <= MIN_SEAT || reservedSeatNumber > theater.getTotalSeatsInARow()) {
            return false;
        }
        return true;
    }


    /**
     * print the message to show that a reservation is made successfully.
     */
    public void reservationConfirmation() {
        System.out.println("Reservation confirmed!");
    }

    /**
     * Checks if the specified row has enough continuous empty seats to accommodate the requested number.
     *
     * @param row      The row to check for availability.
     * @param numSeats The number of continuous seats required.
     * @return True if there are enough continuous empty seats, otherwise false.
     */
    private boolean hasEnoughSeats(Row row, int numSeats) {
        List<Seat> availableSeatsInRow = row.stream()
                .filter(seat -> seat.getReservedFor() == null)
                .collect(Collectors.toList());

        for (int i = 0; i <= availableSeatsInRow.size() - numSeats; i++) {
            if (isContinuousEmpty(availableSeatsInRow.subList(i, i + numSeats))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if all the seats in the given list are empty (= not reserved).
     *
     * @param seats A list of seats to check.
     * @return True if all seats are empty, otherwise false.
     */
    private boolean isContinuousEmpty(List<Seat> seats) {
        return seats.stream().allMatch(seat -> seat.getReservedFor() == null);
    }

    //private int[] findContinuousSeats(Row row, int numSeats) {
    //    List<Seat> seats = row;
    //    for (int i = 0; i <= seats.size() - numSeats; i++) {
    //        if (isContinuousEmpty(seats.subList(i, i + numSeats))) {
    //            return new int[] { i, i + numSeats - 1 };
    //        }
    //    }
    //    return null;
    //}

    /**
     * Finds the best row based on the number of continuous seats required and the proximity to the center row.
     * It also considers whether the row should be wheelchair accessible or not.
     *
     * @param rows                List of rows to choose from.
     * @param numSeats            Number of continuous seats required.
     * @param wheelchairAccessible True if the row should be wheelchair accessible, otherwise false.
     * @return The best matching row or null if no suitable row is found.
     */
    private Row findBestRow(List<Row> rows, int numSeats, boolean wheelchairAccessible) {
        int centerRow = theater.getRows().size() / CUT_BY_MIDDLE;

        return rows.stream()
                .sorted(Comparator.comparingInt(row -> Math.abs(row.getRowNumber() - centerRow)))
                .filter(row -> hasEnoughSeats(row, numSeats))
                .filter(row -> wheelchairAccessible == row.isWheelchairAccessible())
                .findFirst()
                .orElse(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservationService that = (ReservationService) o;
        return Objects.equals(theater, that.theater);
    }

    @Override
    public int hashCode() {
        return Objects.hash(theater);
    }

    @Override
    public String toString() {
        return "ReservationService{" +
                "theater=" + theater +
                '}';
    }
}


