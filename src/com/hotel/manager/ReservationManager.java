package com.hotel.manager;

import com.hotel.dao.ReservationDAO;
import com.hotel.model.Reservation;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReservationManager — Business logic for Booking management.
 */
public class ReservationManager {
    private List<Reservation> reservations;
    private final ReservationDAO reservationDAO;
    private final RoomManager roomManager;

    public ReservationManager(RoomManager roomManager) {
        this.roomManager      = roomManager;
        this.reservationDAO   = new ReservationDAO();
        this.reservations     = reservationDAO.loadAllReservations();
    }

    public boolean createReservation(Reservation res) {
        if (findById(res.getReservationId()) != null) return false;
        reservations.add(res);
        return save();
    }

    public boolean isRoomAvailableForDates(String roomId, LocalDate checkIn, LocalDate checkOut) {
        for (Reservation r : reservations) {
            if (r.getRoom().getRoomId().equals(roomId) && 
                r.getStatus() != Reservation.Status.CANCELLED && 
                r.getStatus() != Reservation.Status.CHECKED_OUT) {
                
                if (r.getCheckInDate().isBefore(checkOut) && r.getCheckOutDate().isAfter(checkIn)) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean updateReservation(Reservation updated) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservations.get(i).getReservationId().equals(updated.getReservationId())) {
                reservations.set(i, updated);
                return save();
            }
        }
        return false;
    }

    public boolean cancelReservation(String reservationId) {
        Reservation res = findById(reservationId);
        if (res == null) return false;
        res.setStatus(Reservation.Status.CANCELLED);
        return save();
    }

    public boolean checkIn(String reservationId) {
        Reservation res = findById(reservationId);
        if (res == null) return false;
        res.setStatus(Reservation.Status.CHECKED_IN);
        roomManager.setRoomAvailability(res.getRoom().getRoomId(), false);
        return save();
    }

    public boolean checkOut(String reservationId) {
        Reservation res = findById(reservationId);
        if (res == null) return false;
        res.setStatus(Reservation.Status.CHECKED_OUT);
        roomManager.setRoomAvailability(res.getRoom().getRoomId(), true);
        return save();
    }

    public Reservation findById(String id) {
        return reservations.stream()
            .filter(r -> r.getReservationId().equals(id))
            .findFirst().orElse(null);
    }

    public List<Reservation> searchReservations(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(reservations);
        String kw = keyword.toLowerCase();
        return reservations.stream()
            .filter(r -> r.getReservationId().toLowerCase().contains(kw)
                      || r.getGuest().getName().toLowerCase().contains(kw)
                      || r.getRoom().getRoomId().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    public List<Reservation> getAllReservations() { return new ArrayList<>(reservations); }

    public List<Reservation> getTodayArrivals() {
        LocalDate today = LocalDate.now();
        return reservations.stream()
            .filter(r -> r.getCheckInDate().equals(today))
            .collect(Collectors.toList());
    }

    public double getTotalRevenue() {
        return reservations.stream()
            .filter(r -> r.getStatus() != Reservation.Status.CANCELLED)
            .mapToDouble(Reservation::getTotalAmount).sum();
    }

    public String generateNextId() {
        int max = reservations.stream()
            .mapToInt(r -> {
                try { return Integer.parseInt(r.getReservationId().replaceAll("[^0-9]", "")); }
                catch (Exception e) { return 0; }
            }).max().orElse(0);
        return String.format("RES%04d", max + 1);
    }

    private boolean save() { return reservationDAO.saveAllReservations(reservations); }
}
