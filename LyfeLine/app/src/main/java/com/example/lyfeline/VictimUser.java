package com.example.lyfeline;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VictimUser extends User {

    final String USER_COLLECTION_ID = "Victims";
    String address;
    String city;
    String state;

    public VictimUser() {
    }

    public VictimUser( String firstName, String lastName, String address, String city,
                      String state ) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
        this.isVictim = true;
        this.isEmt = false;

    }
    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Boolean getIsVictim() {
        return isVictim;
    }

    public void setIsVictim(Boolean victim) {
        isVictim = victim;
    }

    public Boolean getIsEmt() {
        return isEmt;
    }

    public void setIsEmt(Boolean emt) {
        isEmt = emt;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
