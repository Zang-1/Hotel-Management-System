package com.hotel.model;

import java.io.Serializable;

/**
 * Guest (Customer) model class.
 * Demonstrates: Encapsulation, Constructor usage
 */
public class Guest implements Serializable {
    private static final long serialVersionUID = 1L;

    // Encapsulation: all fields private
    private String guestId;
    private String name;
    private String phoneNumber;
    private String identityCard;
    private String email;
    private String address;

    // Constructor
    public Guest(String guestId, String name, String phoneNumber, String identityCard, String email, String address) {
        this.guestId      = guestId;
        this.name         = name;
        this.phoneNumber  = phoneNumber;
        this.identityCard = identityCard;
        this.email        = email;
        this.address      = address;
    }

    // Overloaded constructor (backwards compat with existing code)
    public Guest(String guestId, String name, String phoneNumber, String identityCard) {
        this(guestId, name, phoneNumber, identityCard, "", "");
    }

    // Getters
    public String getGuestId()      { return guestId; }
    public String getName()         { return name; }
    public String getPhoneNumber()  { return phoneNumber; }
    public String getIdentityCard() { return identityCard; }
    public String getEmail()        { return email; }
    public String getAddress()      { return address; }

    // Setters
    public void setGuestId(String guestId)           { this.guestId = guestId; }
    public void setName(String name)                 { this.name = name; }
    public void setPhoneNumber(String phoneNumber)   { this.phoneNumber = phoneNumber; }
    public void setIdentityCard(String identityCard) { this.identityCard = identityCard; }
    public void setEmail(String email)               { this.email = email; }
    public void setAddress(String address)           { this.address = address; }

    @Override
    public String toString() {
        return name + " (" + guestId + ")";
    }
}
