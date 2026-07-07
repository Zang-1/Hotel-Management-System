package com.hotel.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Reservation (Booking) model class.
 * Demonstrates: Encapsulation, Composition (has Guest and Room), Constructor, Polymorphism usage
 */
public class Reservation implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Status { PENDING, CONFIRMED, CHECKED_IN, CHECKED_OUT, CANCELLED }

    // Encapsulation
    private String reservationId;
    // Composition: contains Guest and Room objects
    private Guest guest;
    private Room room;
    private LocalDate checkInDate;
    private LocalDate checkOutDate;
    private double totalAmount;
    private Status status;
    private String notes;

    // Constructor
    public Reservation(String reservationId, Guest guest, Room room,
                       LocalDate checkInDate, LocalDate checkOutDate) {
        this.reservationId = reservationId;
        this.guest         = guest;
        this.room          = room;
        this.checkInDate   = checkInDate;
        this.checkOutDate  = checkOutDate;
        this.status        = Status.CONFIRMED;
        this.notes         = "";
        // Polymorphism: calls room.calculatePricePerNight() which behaves differently
        // depending on whether room is Standard, Deluxe, or Suite
        this.totalAmount   = calculateTotal();
    }

    /**
     * Uses polymorphism: room.calculatePricePerNight() returns different values
     * for StandardRoom, DeluxeRoom, or SuiteRoom.
     */
    public double calculateTotal() {
        long nights = ChronoUnit.DAYS.between(checkInDate, checkOutDate);
        return nights * room.calculatePricePerNight();
    }

    public long getNumberOfNights() {
        return ChronoUnit.DAYS.between(checkInDate, checkOutDate);
    }

    // Getters
    public String getReservationId() { return reservationId; }
    public Guest getGuest()          { return guest; }
    public Room getRoom()            { return room; }
    public LocalDate getCheckInDate(){ return checkInDate; }
    public LocalDate getCheckOutDate(){ return checkOutDate; }
    public double getTotalAmount()   { return totalAmount; }
    public Status getStatus()        { return status; }
    public String getNotes()         { return notes; }

    // Setters
    public void setReservationId(String id)       { this.reservationId = id; }
    public void setGuest(Guest guest)             { this.guest = guest; }
    public void setRoom(Room room)                { this.room = room; }
    public void setCheckInDate(LocalDate d)       { this.checkInDate = d; recalculate(); }
    public void setCheckOutDate(LocalDate d)      { this.checkOutDate = d; recalculate(); }
    public void setStatus(Status status)          { this.status = status; }
    public void setNotes(String notes)            { this.notes = notes; }

    private void recalculate() {
        if (checkInDate != null && checkOutDate != null && room != null) {
            this.totalAmount = calculateTotal();
        }
    }

    @Override
    public String toString() {
        return String.format("[%s] %s — Room %s (%s → %s) $%.0f",
            reservationId, guest.getName(), room.getRoomId(),
            checkInDate, checkOutDate, totalAmount);
    }
}
