package com.hotel.manager;

import com.hotel.dao.RoomDAO;
import com.hotel.exception.DuplicateDataException;
import com.hotel.model.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * RoomManager — Business logic for Room management.
 * Loads data on startup, saves on every modification.
 */
public class RoomManager {
    private List<Room> rooms;
    private final RoomDAO roomDAO;

    public RoomManager() {
        this.roomDAO = new RoomDAO();
        this.rooms = roomDAO.loadAllRooms();
        if (this.rooms.isEmpty()) seedDefaultRooms();
    }

    private void seedDefaultRooms() {
        rooms.add(new StandardRoom("101", 500000.0, true, 1));
        rooms.add(new StandardRoom("102", 500000.0, true, 1));
        rooms.add(new StandardRoom("103", 600000.0, true, 1));
        rooms.add(new DeluxeRoom("201", 800000.0, true, 2));
        rooms.add(new DeluxeRoom("202", 800000.0, true, 2));
        rooms.add(new DeluxeRoom("203", 900000.0, false, 2));
        rooms.add(new SuiteRoom("301", 1500000.0, true, 3));
        rooms.add(new SuiteRoom("302", 2000000.0, false, 3));
        save();
    }

    // Theo Bài giảng Chương 3: Khai báo hàm có thể ném ra Exception (từ khóa throws)
    public boolean addRoom(Room room) throws DuplicateDataException {
        if (findById(room.getRoomId()) != null) {
            // Theo Bài giảng Chương 3: Sử dụng từ khóa throw để ném ngoại lệ
            throw new DuplicateDataException("Mã phòng " + room.getRoomId() + " đã tồn tại trong hệ thống!");
        }
        rooms.add(room);
        return save();
    }

    public boolean updateRoom(Room updated) {
        for (int i = 0; i < rooms.size(); i++) {
            if (rooms.get(i).getRoomId().equals(updated.getRoomId())) {
                rooms.set(i, updated);
                return save();
            }
        }
        return false;
    }

    public boolean deleteRoom(String roomId) {
        boolean removed = rooms.removeIf(r -> r.getRoomId().equals(roomId));
        if (removed) save();
        return removed;
    }

    public Room findById(String roomId) {
        return rooms.stream()
            .filter(r -> r.getRoomId().equalsIgnoreCase(roomId))
            .findFirst().orElse(null);
    }

    public List<Room> searchRooms(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(rooms);
        String kw = keyword.toLowerCase();
        return rooms.stream()
            .filter(r -> r.getRoomId().toLowerCase().contains(kw)
                      || r.getRoomType().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    public List<Room> getAvailableRooms() {
        return rooms.stream().filter(Room::isAvailable).collect(Collectors.toList());
    }

    public List<Room> getAllRooms() { return new ArrayList<>(rooms); }

    public void setRoomAvailability(String roomId, boolean available) {
        Room r = findById(roomId);
        if (r != null) { r.setAvailable(available); save(); }
    }

    private boolean save() { return roomDAO.saveAllRooms(rooms); }

    // Statistics
    public long countAvailable() { return rooms.stream().filter(Room::isAvailable).count(); }
    public long countOccupied()  { return rooms.stream().filter(r -> !r.isAvailable()).count(); }
}
