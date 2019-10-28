package com.example.lyfeline;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Main2Activity extends AppCompatActivity {
    EditText victimEmail, victimPass, victimFirstName, victimLastName, victimAddress, victimCity,
            victimState;
    EditText emtEmail, emtPass, emtFirstName, emtLastName, emtCode;
    Button buttonVictimRegister, buttonEmtRegister, buttonEmt, buttonVictim;
    private FirebaseAuth mAuth;
    final String EMT_CODE = "123";
    private FirebaseDatabase mDatabase;
    private DatabaseReference dbRef;
    final String VICTIM_PATH = "Victims/";
    final String EMT_PATH = "Emt/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.emt_or_victim);

        //Buttons
        buttonEmt = (Button) findViewById(R.id.buttonEMT);
        buttonVictim = (Button) findViewById(R.id.buttonVictim);
        buttonVictimRegister = (Button) findViewById(R.id.buttonRegisterVictim);
        buttonEmtRegister = (Button) findViewById(R.id.buttonRegisterEMT);

        //EditTexts EMT
        emtEmail = (EditText) findViewById(R.id.editTextEmailEMT);
        emtPass = (EditText) findViewById(R.id.editTextPasswordEMT);
        emtFirstName = (EditText) findViewById(R.id.editTextFirstEMT);
        emtLastName = (EditText) findViewById(R.id.editTextLastEMT);
        emtCode = (EditText) findViewById(R.id.editTextCodeEMT);

        mAuth = FirebaseAuth.getInstance();

    }


    public void onClickResolve(View v) {
        switch (v.getId()) {
            case R.id.buttonEMT:
                setContentView(R.layout.emt_create_account);
                break;
            case R.id.buttonVictim:
                setContentView(R.layout.victim_create_account);
                break;
            case R.id.buttonRegisterVictim:
                registerVictim();
                break;
            case R.id.buttonRegisterEMT:
                registerEMT();
                Toast.makeText(this, "reg emt button clicked", Toast.LENGTH_LONG).show();
                break;
        }
    }

    public void registerVictim() {
        victimEmail = (EditText) findViewById(R.id.editTextEmail);
        victimPass = (EditText) findViewById(R.id.editTextPassword);
        victimFirstName = (EditText) findViewById(R.id.editTextFirst);
        victimLastName = (EditText) findViewById(R.id.editTextLast);
        victimAddress = (EditText) findViewById(R.id.editTextAddress);
        victimCity = (EditText) findViewById(R.id.editTextCity);
        victimState = (EditText) findViewById(R.id.editTextState);

        String email = victimEmail.getText().toString();
        String password = victimPass.getText().toString();
        final String firstN = victimFirstName.getText().toString();
        final String lastN = victimLastName.getText().toString();
        final String address = victimAddress.getText().toString();
        final String city = victimCity.getText().toString();
        final String state = victimState.getText().toString();

        if (!email.matches("") && !password.matches("") && !firstN.matches("") &&
                !lastN.matches("") && !address.matches("") && !city.matches("")
                && !state.matches("")) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                createNewVictim(userID, firstN, lastN, address, city, state);
                                Toast.makeText(getApplicationContext(), "successful registry", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Couldn't Create Account, "
                                        + "Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(this, "Please Complete All Fields", Toast.LENGTH_LONG).show();
        }

    }

    public void createNewVictim(String userID, String firstName, String lastName, String address,
                                String city, String state) {
        VictimUser victim = new VictimUser(firstName, lastName, address, city, state);
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference(VICTIM_PATH + userID);
        dbRef.setValue(victim);

        Intent victimGui = new Intent(this, VictimGui.class);
        startActivity(victimGui);

    }

    public void registerEMT() {
        emtEmail = (EditText) findViewById(R.id.editTextEmailEMT);
        emtPass = (EditText) findViewById(R.id.editTextPasswordEMT);
        emtFirstName = (EditText) findViewById(R.id.editTextFirstEMT);
        emtLastName = (EditText) findViewById(R.id.editTextLastEMT);
        emtCode = (EditText) findViewById(R.id.editTextCodeEMT);

        String email = emtEmail.getText().toString();
        String password = emtPass.getText().toString();
        final String firstN = emtFirstName.getText().toString();
        final String lastN = emtLastName.getText().toString();
        final String code = emtCode.getText().toString();


        if (!email.matches("") && !password.matches("") && !firstN.matches("") &&
                !lastN.matches("") && code.matches(EMT_CODE)) {
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                String userID = user.getUid();
                                createNewEMT(userID, firstN, lastN);
                                Toast.makeText(getApplicationContext(), "successful registry", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Couldn't Create Account, "
                                        + "Please Try Again", Toast.LENGTH_LONG).show();
                            }
                        }
                    });

        } else {
            Toast.makeText(this, "Please Complete All Fields", Toast.LENGTH_LONG).show();
        }
    }
    public void createNewEMT(String userID, String firstName, String lastName) {
        EmtUser emt = new EmtUser(firstName, lastName);
        mDatabase = FirebaseDatabase.getInstance();
        dbRef = mDatabase.getReference(EMT_PATH + userID);
        dbRef.setValue(emt);

        Intent emtGui = new Intent(this, EmtGui.class);
        startActivity(emtGui);

    }
}
