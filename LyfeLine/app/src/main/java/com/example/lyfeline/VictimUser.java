package com.example.lyfeline;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class VictimUser extends User {
    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    final String USER_COLLECTION_ID = "Victims";

    public VictimUser( String uid, String firstName, String lastName, String address, String city,
                      String state ) {
        this.userID = uid;
        this.firstName = firstName;
        this.lastName = lastName;
        this.address = address;
        this.city = city;
        this.state = state;
    }

    public void writeToDatabase() {
        getFbRef();
        dbRef.child(USER_COLLECTION_ID).child(userID).child("First Name").setValue(firstName);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("Last Name").setValue(lastName);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("Address").setValue(address);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("City").setValue(city);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("State").setValue(state);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("Is Victim").setValue(true);
        dbRef.child(USER_COLLECTION_ID).child(userID).child("Is Rescuer").setValue(false);

    }

    public void getFbRef() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference();
    }
}
