package com.hotel.dao;

import com.hotel.model.Room;
import com.hotel.util.FileHandler;
import java.util.List;

/**
 * Data Access Object for Room entities.
 * Separates persistence logic from business logic.
 */
public class RoomDAO {
    private static final String FILE_PATH = "data/rooms_data.dat";

    public List<Room> loadAllRooms() {
        return FileHandler.readDataFromFile(FILE_PATH);
    }

    public boolean saveAllRooms(List<Room> rooms) {
        return FileHandler.saveDataToFile(rooms, FILE_PATH);
    }
}
