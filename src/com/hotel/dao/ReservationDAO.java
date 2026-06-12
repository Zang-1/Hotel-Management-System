package com.hotel.dao;

import com.hotel.model.Reservation;
import com.hotel.util.FileHandler;
import java.util.List;

public class ReservationDAO {
    private static final String FILE_PATH = "data/reservations_data.dat";

    public List<Reservation> loadAllReservations() {
        return FileHandler.readDataFromFile(FILE_PATH);
    }

    public boolean saveAllReservations(List<Reservation> reservations) {
        return FileHandler.saveDataToFile(reservations, FILE_PATH);
    }
}
