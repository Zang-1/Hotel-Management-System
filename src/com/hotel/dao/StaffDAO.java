package com.hotel.dao;

import com.hotel.model.Staff;
import com.hotel.util.FileHandler;
import java.util.List;

public class StaffDAO {
    private static final String FILE_PATH = "data/staff_data.dat";

    public List<Staff> loadAllStaff() {
        return FileHandler.readDataFromFile(FILE_PATH);
    }

    public boolean saveAllStaff(List<Staff> staffList) {
        return FileHandler.saveDataToFile(staffList, FILE_PATH);
    }
}
