package com.hotel.util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * FileHandler utility — handles all file I/O via Java Object Serialization.
 * Demonstrates: File Handling, Exception handling
 */
public class FileHandler {

    /**
     * Save a list of objects to a binary .dat file using ObjectOutputStream (Serialization).
     */
    @SuppressWarnings("unchecked")
    public static <T> boolean saveDataToFile(List<T> data, String filePath) {
        File file = new File(filePath);
        file.getParentFile().mkdirs(); // ensure directory exists
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new BufferedOutputStream(new FileOutputStream(filePath)))) {
            oos.writeObject(data);
            return true;
        } catch (IOException e) {
            System.err.println("[FileHandler] Error saving to " + filePath + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Load a list of objects from a binary .dat file using ObjectInputStream (Deserialization).
     * Returns an empty list if file doesn't exist or on error.
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> readDataFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(
                new BufferedInputStream(new FileInputStream(filePath)))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[FileHandler] Error reading from " + filePath + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }

    /**
     * Delete a data file.
     */
    public static boolean deleteFile(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.delete();
    }
}
