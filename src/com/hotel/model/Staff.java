package com.hotel.model;

import java.io.Serializable;

/**
 * Staff model class.
 * Demonstrates: Encapsulation, Constructor usage
 */
public class Staff implements Serializable {
    private static final long serialVersionUID = 1L;

    public enum Role { MANAGER, RECEPTIONIST, HOUSEKEEPING, SECURITY, MAINTENANCE }

    // Encapsulation
    private String staffId;
    private String name;
    private Role role;
    private String phoneNumber;
    private double salary;
    private String shift; // Morning / Afternoon / Night

    // Constructor
    public Staff(String staffId, String name, Role role, String phoneNumber, double salary, String shift) {
        this.staffId     = staffId;
        this.name        = name;
        this.role        = role;
        this.phoneNumber = phoneNumber;
        this.salary      = salary;
        this.shift       = shift;
    }

    // Getters
    public String getStaffId()    { return staffId; }
    public String getName()       { return name; }
    public Role getRole()         { return role; }
    public String getPhoneNumber(){ return phoneNumber; }
    public double getSalary()     { return salary; }
    public String getShift()      { return shift; }

    // Setters
    public void setStaffId(String staffId)       { this.staffId = staffId; }
    public void setName(String name)             { this.name = name; }
    public void setRole(Role role)               { this.role = role; }
    public void setPhoneNumber(String phone)     { this.phoneNumber = phone; }
    public void setSalary(double salary)         { this.salary = salary; }
    public void setShift(String shift)           { this.shift = shift; }

    @Override
    public String toString() {
        return name + " [" + role + "]";
    }
}
