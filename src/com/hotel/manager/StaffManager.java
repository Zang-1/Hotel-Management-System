package com.hotel.manager;

import com.hotel.dao.StaffDAO;
import com.hotel.model.Staff;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * StaffManager — Business logic for Staff management.
 */
public class StaffManager {
    private List<Staff> staffList;
    private final StaffDAO staffDAO;

    public StaffManager() {
        this.staffDAO  = new StaffDAO();
        this.staffList = staffDAO.loadAllStaff();
        if (this.staffList.isEmpty()) seedDefaultStaff();
    }

    private void seedDefaultStaff() {
        staffList.add(new Staff("S001", "Pham Thi Hoa", Staff.Role.MANAGER, "0901111111", 15000.0, "Morning"));
        staffList.add(new Staff("S002", "Nguyen Van Binh", Staff.Role.RECEPTIONIST, "0902222222", 8000.0, "Morning"));
        staffList.add(new Staff("S003", "Le Thi Cam", Staff.Role.RECEPTIONIST, "0903333333", 8000.0, "Afternoon"));
        staffList.add(new Staff("S004", "Tran Van Duc", Staff.Role.HOUSEKEEPING, "0904444444", 6000.0, "Morning"));
        staffList.add(new Staff("S005", "Ho Thi Em", Staff.Role.SECURITY, "0905555555", 7000.0, "Night"));
        save();
    }

    public boolean addStaff(Staff staff) {
        if (findById(staff.getStaffId()) != null) return false;
        staffList.add(staff);
        return save();
    }

    public boolean updateStaff(Staff updated) {
        for (int i = 0; i < staffList.size(); i++) {
            if (staffList.get(i).getStaffId().equals(updated.getStaffId())) {
                staffList.set(i, updated);
                return save();
            }
        }
        return false;
    }

    public boolean deleteStaff(String staffId) {
        boolean removed = staffList.removeIf(s -> s.getStaffId().equals(staffId));
        if (removed) save();
        return removed;
    }

    public Staff findById(String staffId) {
        return staffList.stream()
            .filter(s -> s.getStaffId().equalsIgnoreCase(staffId))
            .findFirst().orElse(null);
    }

    public List<Staff> searchStaff(String keyword) {
        if (keyword == null || keyword.isBlank()) return new ArrayList<>(staffList);
        String kw = keyword.toLowerCase();
        return staffList.stream()
            .filter(s -> s.getName().toLowerCase().contains(kw)
                      || s.getStaffId().toLowerCase().contains(kw)
                      || s.getRole().name().toLowerCase().contains(kw))
            .collect(Collectors.toList());
    }

    public List<Staff> getAllStaff() { return new ArrayList<>(staffList); }

    public String generateNextId() {
        int max = staffList.stream()
            .mapToInt(s -> {
                try { return Integer.parseInt(s.getStaffId().replaceAll("[^0-9]", "")); }
                catch (Exception e) { return 0; }
            }).max().orElse(0);
        return String.format("S%03d", max + 1);
    }

    private boolean save() { return staffDAO.saveAllStaff(staffList); }
}
