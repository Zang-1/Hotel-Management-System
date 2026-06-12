package com.hotel.model;

/**
 * SuiteRoom - inherits from Room.
 * Demonstrates: Inheritance, Polymorphism
 * Suite rooms apply a 120% premium surcharge (2.2x base price).
 */
public class SuiteRoom extends Room {
    private static final long serialVersionUID = 1L;
    private static final double SUITE_SURCHARGE = 2.2; // 120% premium

    public SuiteRoom(String roomId, double basePricePerNight, boolean isAvailable, int floor) {
        super(roomId, "Suite", basePricePerNight, isAvailable, floor,
              "Exclusive suite with panoramic views, living area and butler service");
    }

    /**
     * Polymorphism: Suite rooms apply 120% premium surcharge.
     */
    @Override
    public double calculatePricePerNight() {
        return getBasePricePerNight() * SUITE_SURCHARGE;
    }
}
