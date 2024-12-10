package model.dto;


import java.time.LocalDate;

public class CreateReservationDto {
    private String email;
    private int roomNumber;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;

    public CreateReservationDto(String email, int roomNumber, LocalDate checkInDate, LocalDate checkOutDate) {
        this.email = email;
        this.roomNumber = roomNumber;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
    }

    public String getUserEmail() {
        return email;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public LocalDate getCheckInDate() {
        return checkInDate;
    }

    public LocalDate getCheckOutDate() {
        return checkOutDate;
    }
}
