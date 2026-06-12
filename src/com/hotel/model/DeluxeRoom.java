package com.hotel.model;

/**
 * DeluxeRoom - inherits from Room.
 * Demonstrates: Inheritance, Polymorphism (overrides calculatePricePerNight)
 * Deluxe rooms apply a 50% premium surcharge on the base price.
 */
public class DeluxeRoom extends Room {
    private static final long serialVersionUID = 1L;
    private static final double DELUXE_SURCHARGE = 1.5; // 50% premium

    public DeluxeRoom(String roomId, double basePricePerNight, boolean isAvailable, int floor) {
        super(roomId, "Deluxe", basePricePerNight, isAvailable, floor,
              "Luxurious deluxe room with premium amenities and city view");
    }

    /**
     * Polymorphism: Deluxe rooms apply 50% premium surcharge.
     */
    @Override
    public double calculatePricePerNight() {
        return getBasePricePerNight() * DELUXE_SURCHARGE;
    }
}
