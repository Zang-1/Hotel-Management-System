package com.hotel.model;

/**
 * StandardRoom - inherits from Room.
 * Demonstrates: Inheritance, Polymorphism (overrides calculatePricePerNight)
 * Standard rooms have no extra surcharge.
 */
public class StandardRoom extends Room {
    private static final long serialVersionUID = 1L;

    public StandardRoom(String roomId, double basePricePerNight, boolean isAvailable, int floor) {
        super(roomId, "Standard", basePricePerNight, isAvailable, floor,
              "Comfortable standard room with essential amenities");
    }

    /**
     * Polymorphism: Standard rooms use base price directly.
     */
    @Override
    public double calculatePricePerNight() {
        return getBasePricePerNight();
    }
}
