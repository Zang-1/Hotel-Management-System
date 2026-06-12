package com.hotel.model;

import java.io.Serializable;

/**
 * Abstract base class for all room types.
 * Demonstrates: Encapsulation, Abstract class, Inheritance (base)
 */
public abstract class Room implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulation: all fields private
    private String roomId;
    private String roomType;
    private double basePricePerNight;
    private boolean isAvailable;
    private int floor;
    private String description;

    // Constructor
    public Room(String roomId, String roomType, double basePricePerNight, boolean isAvailable, int floor, String description) {
        this.roomId = roomId;
        this.roomType = roomType;
        this.basePricePerNight = basePricePerNight;
        this.isAvailable = isAvailable;
        this.floor = floor;
        this.description = description;
    }

    // Polymorphism: abstract method — each subclass calculates price differently
    public abstract double calculatePricePerNight();

    // Getters
    public String getRoomId()           { return roomId; }
    public String getRoomType()         { return roomType; }
    public double getBasePricePerNight(){ return basePricePerNight; }
    public boolean isAvailable()        { return isAvailable; }
    public int getFloor()               { return floor; }
    public String getDescription()      { return description; }

    // Setters
    public void setRoomId(String roomId)                   { this.roomId = roomId; }
    public void setRoomType(String roomType)               { this.roomType = roomType; }
    public void setBasePricePerNight(double price)         { this.basePricePerNight = price; }
    public void setAvailable(boolean available)            { this.isAvailable = available; }
    public void setFloor(int floor)                        { this.floor = floor; }
    public void setDescription(String description)         { this.description = description; }

    @Override
    public String toString() {
        return String.format("Room %s (%s - $%.0f)", roomId, roomType, calculatePricePerNight());
    }
}
