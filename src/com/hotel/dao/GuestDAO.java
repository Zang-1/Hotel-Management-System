package com.hotel.dao;

import com.hotel.model.Guest;
import com.hotel.util.FileHandler;
import java.util.List;

public class GuestDAO {
    private static final String FILE_PATH = "data/guests_data.dat";

    public List<Guest> loadAllGuests() {
        return FileHandler.readDataFromFile(FILE_PATH);
    }

    public boolean saveAllGuests(List<Guest> guests) {
        return FileHandler.saveDataToFile(guests, FILE_PATH);
    }
}
