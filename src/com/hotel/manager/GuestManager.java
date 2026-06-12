package com.hotel.manager;

import com.hotel.dao.GuestDAO;
import com.hotel.model.Guest;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GuestManager — Business logic for Guest/Customer management.
 */
public class GuestManager {
    private List<Guest> guests;
    private final GuestDAO guestDAO;

    public GuestManager() {
        this.guestDAO = new GuestDAO();
        this.guests = guestDAO.loadAllGuests();
        if (this.guests.isEmpty()) seedDefaultGuests();
    }

    private void seedDefaultGuests() {
        guests.add(new Guest("G001", "Nguyen Van An", "0901234567", "001234567890", "an@email.com", "Ha Noi"));
        guests.add(new Guest("G002", "Tran Thi Bich", "0912345678", "001234567891", "bich@email.com", "Ho Chi Minh"));
        guests.add(new Guest("G003", "Le Quoc Cuong", "0923456789", "001234567892", "cuong@email.com", "Da Nang"));
        save();
    }

    public boolean addGuest(Guest guest) {
        if (findById(guest.getGuestId()) != null) return false;
        guests.add(guest);
        return save();
    }

    public boolean updateGuest(Guest updated) {
        for (int i = 0; i < guests.size(); i++) {
            if (guests.get(i).getGuestId().equals(updated.getGuestId())) {
                guests.set(i, updated);
                return save();
            }
        }
        return false;
    }

    public boolean deleteGuest(String guestId) {
        boolean removed = guests.removeIf(g -> g.getGuestId().equals(guestId));
        if (removed) save();
        return removed;
    }

    public Guest findById(String guestId) {
        return guests.stream()
            .filter(g -> g.getGuestId().equalsIgnoreCase(guestId))
            .findFirst().orElse(null);
    }

    public List<Guest> searchGuests(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(guests);
        String kw = keyword.toLowerCase();
        return guests.stream()
            .filter(g -> g.getName().toLowerCase().contains(kw)
                      || g.getGuestId().toLowerCase().contains(kw)
                      || g.getPhoneNumber().contains(kw)
                      || g.getIdentityCard().contains(kw))
            .collect(Collectors.toList());
    }

    public List<Guest> getAllGuests() { return new ArrayList<>(guests); }

    public String generateNextId() {
        int max = guests.stream()
            .mapToInt(g -> {
                try { return Integer.parseInt(g.getGuestId().replaceAll("[^0-9]", "")); }
                catch (Exception e) { return 0; }
            }).max().orElse(0);
        return String.format("G%03d", max + 1);
    }

    private boolean save() { return guestDAO.saveAllGuests(guests); }
}
