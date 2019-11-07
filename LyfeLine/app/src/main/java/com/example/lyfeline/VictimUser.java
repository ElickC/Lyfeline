package com.example.lyfeline;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VictimUser extends User {

    final String USER_COLLECTION_ID = "Victims";

    public VictimUser() {
    }

    public VictimUser( String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
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


}
