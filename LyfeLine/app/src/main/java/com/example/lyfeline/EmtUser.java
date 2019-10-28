package com.example.lyfeline;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class EmtUser extends User {

    private FirebaseAuth mAuth;
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    final String EMT_COLLECTION_ID = "EMT";

    public EmtUser( String uid, String firstName, String lastName ) {
        this.userID = uid;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public void writeToDatabase() {
        getFbRef();
        dbRef.child(EMT_COLLECTION_ID).child(userID).child("First Name").setValue(firstName);
        dbRef.child(EMT_COLLECTION_ID).child(userID).child("Last Name").setValue(lastName);
        dbRef.child(EMT_COLLECTION_ID).child(userID).child("Is Victim").setValue(false);
        dbRef.child(EMT_COLLECTION_ID).child(userID).child("Is Rescuer").setValue(true);

    }

    public void getFbRef() {
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference();
    }
}

